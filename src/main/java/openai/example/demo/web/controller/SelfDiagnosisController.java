package openai.example.demo.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/self-diagnosis")
@RequiredArgsConstructor
public class SelfDiagnosisController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-url}")
    private String openaiApiUrl;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @PostMapping("/chat")
    public ResponseEntity<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDTO req) throws IOException, ParseException {

        // RestTemplate = 외부 URL로 HTTP 전송
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        // OpenAI API URL로 보낼 request 작성
        // request에 넣을 response_format 불러오기
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src/main/resources/static/self-diagnosis/chat-response-format.json"));
        ChatbotRequest chatbotRequest = new ChatbotRequest().builder()
                .model(model)
                .messages(new ArrayList<>())
                .temperature(0)
                .max_completion_tokens(256)
                .frequency_penalty(1.0)
                .presence_penalty(-0.5)
                .response_format(rootNode)
                .build();

        // request에 develop message 추가
        // develop message = 모델에게 정확한 지시 내리기
        String developerPrompt = Files.readString(Paths.get("src/main/resources/static/self-diagnosis/developer-prompt.txt"), StandardCharsets.UTF_8);
        chatbotRequest.addMessage("developer", developerPrompt);

        // request에 user message 추가
        // user message = 사용자의 요청
        String userPrompt = "Please recommend a medical department that suits my symptoms.\n"
                + " - body system: " + req.getSystem() + "\n"
                + " - symptom: " + req.getSymptom() + "\n";
        if (req.getCondition() != null)
            userPrompt += " - condition: " + req.getCondition() + "\n";
        if (req.getAdditionalNote() != null)
            userPrompt += " - additional note: " + req.getAdditionalNote();
        chatbotRequest.addMessage("user", userPrompt);

        // OpenAI response를 객체로 변환해서 가져오기
        ChatbotResponse chatbotResponse = restTemplate
                .postForObject(openaiApiUrl, chatbotRequest, ChatbotResponse.class);

        // response의 message(role, content)에서 content 추출
        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();

        // content 파싱
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

        // DTO로 변환
        SelfDiagnosisResponse.CreateResultDTO response = SelfDiagnosisResponse.CreateResultDTO
                .builder()
                .id(chatbotResponse.getId())
                .departmentList(departmentList)
                .reason(reasonText)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
