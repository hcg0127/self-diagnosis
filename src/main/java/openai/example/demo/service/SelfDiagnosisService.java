package openai.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openai.example.demo.apiPayload.code.status.ErrorStatus;
import openai.example.demo.apiPayload.exception.handler.JsonParserHandler;
import openai.example.demo.apiPayload.exception.handler.LanguageHandler;
import openai.example.demo.apiPayload.exception.handler.SymptomHandler;
import openai.example.demo.converter.SelfDiagnosisConverter;
import openai.example.demo.domain.DetailSymptom;
import openai.example.demo.domain.Symptom;
import openai.example.demo.repository.detailSymptomRepository.DetailSymptomRepository;
import openai.example.demo.repository.symptomRepository.SymptomRepository;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelfDiagnosisService {

    private final ChatbotService chatbotService;

    private final SymptomRepository symptomRepository;

    private final DetailSymptomRepository detailSymptomRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // V2: Symptom을 Entity 대신 Set<String>으로 저장 -> O(1)로 찾기
//    private static final Set<String> SYMPTOM_LIST = Set.of("두통", "어지러움", "메스꺼움", "구토", "복통", "가슴 통증", "소화 불량", "변비", "설사", "배변 시 통증",
//            "소변 시 통증", "혈뇨", "빈뇨", "요실금", "기침", "가래", "호흡 곤란", "재채기", "코막힘", "콧물",
//            "인후통", "쉰 목소리", "청력 저하", "귀 통증", "이명", "눈물", "눈 가려움", "시야 흐림", "눈 충혈", "결막염 증상",
//            "손 떨림", "팔 저림", "다리 저림", "관절 통증", "근육통", "허리 통증", "목 통증", "어깨 통증", "무릎 통증", "발바닥 통증",
//            "피부 가려움", "피부 발진", "두드러기", "물집", "탈모", "피부 각질", "손발톱 이상", "입안 염증", "구강 건조", "입 냄새",
//            "불면", "피로감", "기억력 감퇴", "집중력 저하", "우울감", "불안감", "짜증", "식욕 저하", "체중 감소", "체중 증가",
//            "생리통", "생리불순", "질 분비물 증가", "질 가려움", "성교통", "유방 통증", "유두 분비물", "고혈압", "저혈압", "가슴 두근거림",
//            "부정맥", "손발 저림", "손발 붓기", "몸 떨림", "발열", "오한", "야간 발한", "식은땀", "체온 변화", "오심",
//            "경련", "실신", "의식 혼미", "시야 상실", "편마비", "언어장애", "연하곤란.json", "걸음걸이 이상", "자세 불균형", "피부 창백",
//            "멍이 잘 듦", "잇몸 출혈", "코피", "혈변", "호흡 시 통증", "숨소리 이상", "가슴 답답함", "팔 마비", "다리 마비", "입술 마비"
//    );

    // COMMON: response(JSONObject) -> parsing -> ResultDTO
    public SelfDiagnosisResponse.CreateResultDTO parseChatMessage(ChatbotResponse chatbotResponse) throws ParseException {

        // response의 message(role, content)에서 content 추출
        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();
        boolean isMatched = (choiceContent.charAt(0) == '{' && choiceContent.charAt(choiceContent.length() - 1) == '}' && choiceContent.charAt(choiceContent.length() - 2) == '}');

        if (isMatched) {
            // content를 JSON으로 파싱
            JSONParser parser = new JSONParser();
            JSONObject content = (JSONObject) parser.parse(choiceContent);

            // content에서 medical_departments(JSONArray) 추출 및 List로 변환
            JSONArray medical_departments = (JSONArray) content.get("medical_departments");
            List<SelfDiagnosisResponse.Department> departmentList = new ArrayList<>();
            for (int i = 0; i < medical_departments.size(); i++) {
                JSONObject medical_department = (JSONObject) medical_departments.get(i);
                String en = medical_department.get("en").toString();
                String ko = medical_department.get("ko").toString();
                SelfDiagnosisResponse.Department department = new SelfDiagnosisResponse.Department(en,ko);
                departmentList.add(department);
            }

            // content에서 reason(Object) 추출 및 String으로 변환
            JSONObject reason = (JSONObject) content.get("reason");
            SelfDiagnosisResponse.Reason reasonObject = new SelfDiagnosisResponse.Reason(reason.get("en").toString(), reason.get("ko").toString());

            return SelfDiagnosisConverter.createResultDTO(chatbotResponse.getId(), departmentList, reasonObject);
        } else
            throw new JsonParserHandler(ErrorStatus.JSON_FORMAT_UNMATCHED);
    }

    // V1: request(system - symptom - condition - additionalNote) -> response
//    public SelfDiagnosisResponse.CreateResultDTO createSelfDiagnosis(SelfDiagnosisRequest.CreateDTO request) throws IOException, ParseException {
//
//        // Chatbot request와 response 만들기
//        ChatbotRequest chatbotRequest = chatbotService.createChatbotRequest(request);
//        ChatbotResponse chatbotResponse = chatbotService.craeteChatbotResponse(chatbotRequest);
//
//        return parseChatMessage(chatbotResponse);
//    }

    // V2: request(symptom) -> response(symptom.json)
//    public SelfDiagnosisResponse.SymptomQuestionResultDTO createSymptomQuestions(String symptom) throws IOException {
//
//        if (!SYMPTOM_LIST.contains(symptom)) {
//            throw new SymptomHandler(ErrorStatus.SYMPTOM_NOT_FOUND);
//        }
//
//        String path = "src/main/resources/static/symptom-question/" + symptom + ".json";
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readTree(new File(path));
//        SelfDiagnosisResponse.SymptomQuestionResultDTO result = mapper.treeToValue(rootNode, SelfDiagnosisResponse.SymptomQuestionResultDTO.class);
//
//        return SelfDiagnosisConverter.createSymptomQuestionResultDTO(symptom, result.getQuestions());
//    }

    // V2: request(symptom, answers) -> response
//    public SelfDiagnosisResponse.CreateResultDTO createDepartment(SelfDiagnosisRequest.CreateDepartmentDTO request) throws IOException, ParseException {
//
//        SelfDiagnosisResponse.SymptomQuestionResultDTO symptomQuestion = createSymptomQuestions(request.getSymptom());
//
//        List<String> questionList = symptomQuestion.getQuestions().stream()
//                .map(SelfDiagnosisResponse.Question::getQuestion).collect(Collectors.toList());
//        List<List<String>> answerList = request.getAnswers();
//
//        if (questionList.size() != answerList.size()) {
//            throw new SymptomHandler(ErrorStatus.SYMPTOM_SIZE_NOT_MATCH);
//        }
//
//        // Chatbot request와 response 만들기
//        ChatbotRequest chatbotRequest = chatbotService.createChatbotRequest(questionList, answerList);
//        ChatbotResponse chatbotResponse = chatbotService.craeteChatbotResponse(chatbotRequest);
//
//        return parseChatMessage(chatbotResponse);
//    }

    // V3: Symptom Entity 생성 & lang 받아오기
    public SelfDiagnosisResponse.SymptomQuestionResultDTO createSymptomQuestions(SelfDiagnosisRequest.CreateSymptomQuestionsDTO request) throws IOException {

        String lang = request.getLang();
        Symptom symptom = symptomRepository.findById(request.getSymptomId())
                .orElseThrow(() -> new SymptomHandler(ErrorStatus.SYMPTOM_NOT_FOUND));

        if (!lang.equals("en") && !lang.equals("ko")) {
            throw new LanguageHandler(ErrorStatus.LANG_NOT_SUPPORTED);
        }

        String symptomName = lang.equals("en") ? symptom.getEnName() : symptom.getKoName();
        String path = "src/main/resources/static/symptom-question/" + lang + "/" + symptomName + ".json";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(path));
        SelfDiagnosisResponse.SymptomQuestionResultDTO result = mapper.treeToValue(rootNode, SelfDiagnosisResponse.SymptomQuestionResultDTO.class);

        return SelfDiagnosisConverter.createSymptomQuestionResultDTO(symptomName, result.getQuestions());
    }

    // V3: symptom, questionList, answerList를 GPT request에 넘기고 응답값 반환
    public SelfDiagnosisResponse.CreateResultDTO createDepartmentResult(SelfDiagnosisRequest.CreateDepartmentDTO request) throws IOException, ParseException {

        // 증상에 맞는 질문 가져오기
        SelfDiagnosisResponse.SymptomQuestionResultDTO symptomQuestion = createSymptomQuestions(new SelfDiagnosisRequest.CreateSymptomQuestionsDTO(request.getLang(), request.getSymptomId()));

        List<String> questionList = symptomQuestion.getQuestions().stream()
                .map(SelfDiagnosisResponse.Question::getQuestion).collect(Collectors.toList());
        List<List<String>> answerList = request.getAnswers();

        if (questionList.size() != answerList.size()) {
            throw new SymptomHandler(ErrorStatus.SYMPTOM_SIZE_NOT_MATCH);
        }

        Symptom symptom = symptomRepository.findById(request.getSymptomId()).get();
        String symptomName = request.getLang().equals("en") ? symptom.getEnName() : symptom.getKoName();

        // Chatbot request와 response 만들기
        ChatbotRequest chatbotRequest = chatbotService.createChatbotRequest(symptomName, questionList, answerList);
        ChatbotResponse chatbotResponse = chatbotService.craeteChatbotResponse(chatbotRequest);

        return parseChatMessage(chatbotResponse);
    }

    // V4: 자주 찾는 symptom 및 detailSymptom 상위 5개 반환
    public SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO getTop5SymptomAndDetailSymptom(String lang) {

        List<Symptom> symptomList = symptomRepository.findTop5ByOrderBySearchCountDesc();
        List<DetailSymptom> detailSymptomList = detailSymptomRepository.findTop5ByOrderBySearchCountDesc();

        List<SelfDiagnosisResponse.Symptom> symptoms;
        List<SelfDiagnosisResponse.DetailSymptom> detailSymptoms;

        if (lang.equals("en")) {
            symptoms = symptomList.stream()
                    .map(symptom -> new SelfDiagnosisResponse.Symptom(symptom.getId(), symptom.getEnName(), symptom.getEnDescription()))
                    .collect(Collectors.toList());
            detailSymptoms = detailSymptomList.stream()
                    .map(detailSymptom -> new SelfDiagnosisResponse.DetailSymptom(detailSymptom.getId(), detailSymptom.getEnDetailDescription()))
                    .collect(Collectors.toList());
        } else if (lang.equals("ko")) {
            symptoms = symptomList.stream()
                    .map(symptom -> new SelfDiagnosisResponse.Symptom(symptom.getId(), symptom.getKoName(), symptom.getKoDescription()))
                    .collect(Collectors.toList());
            detailSymptoms = detailSymptomList.stream()
                    .map(detailSymptom -> new SelfDiagnosisResponse.DetailSymptom(detailSymptom.getId(), detailSymptom.getKoDetailDescription()))
                    .collect(Collectors.toList());
        } else {
            throw new LanguageHandler(ErrorStatus.LANG_NOT_SUPPORTED);
        }

        return SelfDiagnosisConverter.getTop5SymptomAndDetailSymptomResultDTO(symptoms, detailSymptoms);
    }

    // V4: DetailSymptom과 관련된 모든 Symptom 반환
    public SelfDiagnosisResponse.getSymptomsWithDetailSymptomResultDTO getSymptomsWithDetailSymptom(String lang, Long detailSymptomId) {

        List<Symptom> symptomList = symptomRepository.findSymptomsWithDetailSymptom(detailSymptomId);

        if (symptomList.isEmpty())
            throw new SymptomHandler(ErrorStatus.SYMPTOM_NOT_FOUND);

        List<SelfDiagnosisResponse.Symptom> symptoms;

        if (lang.equals("en")) {
            symptoms = symptomList.stream()
                    .map(symptom -> new SelfDiagnosisResponse.Symptom(symptom.getId(), symptom.getEnName(), symptom.getEnDescription()))
                    .collect(Collectors.toList());
        } else if (lang.equals("ko")) {
            symptoms = symptomList.stream()
                    .map(symptom -> new SelfDiagnosisResponse.Symptom(symptom.getId(), symptom.getKoName(), symptom.getKoDescription()))
                    .collect(Collectors.toList());
        } else {
            throw new LanguageHandler(ErrorStatus.LANG_NOT_SUPPORTED);
        }

        return SelfDiagnosisConverter.getSymptomsWithDetailSymptomResultDTO(symptoms);
    }

    // V4: DetailSymptom 검색어 자동 완성
    public SelfDiagnosisResponse.getDetailSymptomsResultDTO searchDetailSymptoms(String lang, String word) {

        HashOperations<String,String,Long> hashOperations = redisTemplate.opsForHash();

        if (redisTemplate.getExpire("DS") < 0) {
            List<DetailSymptom> detailSymptomList = detailSymptomRepository.findAll();
            Map<String,Long> stringLongMap;
            if (lang.equals("en")) {
                stringLongMap = detailSymptomList.stream()
                        .collect(Collectors.toMap(DetailSymptom::getEnDetailDescription,DetailSymptom::getId));
            } else if (lang.equals("ko")) {
                stringLongMap = detailSymptomList.stream()
                        .collect(Collectors.toMap(DetailSymptom::getKoDetailDescription,DetailSymptom::getId));
            } else {
                throw new LanguageHandler(ErrorStatus.LANG_NOT_SUPPORTED);
            }
            hashOperations.putAll("DS",stringLongMap);
            redisTemplate.expire("DS",1L, TimeUnit.MINUTES);
        }

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match("*" + word + "*")
                .build();

        Cursor<Map.Entry<String,Long>> cursor = hashOperations
                .scan("DS",scanOptions);

        List<SelfDiagnosisResponse.DetailSymptom> detailSymptoms = new ArrayList<>();

        while (cursor.hasNext()) {
            Map.Entry<String, Long> entry = cursor.next();
            detailSymptoms.add(SelfDiagnosisConverter
                    .getDetailSymptom(entry.getKey(), ((Number) entry.getValue()).longValue()));
        }

        return SelfDiagnosisConverter.getDetailSymptomsResultDTO(detailSymptoms);
    }
}
