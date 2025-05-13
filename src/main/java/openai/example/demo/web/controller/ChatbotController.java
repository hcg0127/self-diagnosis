package openai.example.demo.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

// Chatbot 테스트용
@RestController
public class ChatbotController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-url}")
    private String openaiApiUrl;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @PostMapping("/chatbot")
    public ResponseEntity<SelfDiagnosisResponse.CreateResultDTO> chat(@RequestParam("prompt") String prompt) throws IOException, ParseException {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(new File("src/main/resources/static/self-diagnosis/chat-response-format.json"));
        ChatbotRequest chatbotRequest = new ChatbotRequest().builder()
                .model(model)
                .messages(new ArrayList<>())
                .temperature(0)
                .presence_penalty(-0.5)
                .frequency_penalty(1.0)
                .max_completion_tokens(100)
                .response_format(rootNode)
                .build();
        chatbotRequest.addMessage("user", prompt);
        String developerPrompt = Files.readString(Paths.get("src/main/resources/static/self-diagnosis/developer-prompt.txt"), StandardCharsets.UTF_8);
        chatbotRequest.addMessage("developer", developerPrompt);

        ChatbotResponse chatbotResponse = restTemplate.postForObject(
                openaiApiUrl, chatbotRequest, ChatbotResponse.class
        );

        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();
        JSONParser parser = new JSONParser();
        JSONObject content = (JSONObject) parser.parse(choiceContent);
        JSONArray medical_departments = (JSONArray) content.get("medical_departments");
        List<SelfDiagnosisResponse.Department> departmentList = new ArrayList<>();
        for (int i = 0; i < medical_departments.size(); i++) {
            JSONObject medical_department = (JSONObject) medical_departments.get(i);
            String en = medical_department.get("en").toString();
            String ko = medical_department.get("ko").toString();
            SelfDiagnosisResponse.Department department = new SelfDiagnosisResponse.Department(en,ko);
            departmentList.add(department);
        }
        String reasonText = content.get("reason").toString();

        // response에서 JSON을 DTO로 변환
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
