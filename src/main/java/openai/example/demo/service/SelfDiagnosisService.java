package openai.example.demo.service;

import lombok.RequiredArgsConstructor;
import openai.example.demo.converter.SelfDiagnosisConverter;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelfDiagnosisService {

    private final ChatbotService chatbotService;

    public SelfDiagnosisResponse.CreateResultDTO createSelfDiagnosis(SelfDiagnosisRequest.CreateDTO request) throws IOException, ParseException {

        // Chatbot request와 response 만들기
        ChatbotRequest chatbotRequest = chatbotService.createChatbotRequest(request);
        ChatbotResponse chatbotResponse = chatbotService.craeteChatbotResponse(chatbotRequest);

        // response의 message(role, content)에서 content 추출
        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();

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
        String reasonText = content.get("reason").toString();

        return SelfDiagnosisConverter.createResultDTO(chatbotResponse.getId(), departmentList, reasonText);
    }
}
