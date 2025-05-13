package openai.example.demo.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import openai.example.demo.domain.Message;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
    public ResponseEntity<ChatbotResponse> chat(@RequestParam("prompt") String prompt) throws IOException {

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

        ChatbotResponse chatbotResponse = restTemplate.postForObject(
                openaiApiUrl, chatbotRequest, ChatbotResponse.class
        );

        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();

        // OpenAI response를 DTO로 변환하기 위한 객체 생성
//        SelfDiagnosisResponse.ChoiceContent toResponse = mapper.readValue(choiceContent, SelfDiagnosisResponse.ChoiceContent.class);

        // response에서 JSON을 DTO로 변환
//        SelfDiagnosisResponse.CreateResultDTO response = SelfDiagnosisResponse.CreateResultDTO
//                .builder()
//                .id(chatbotResponse.getId())
//                .departmentList(toResponse.getDepartmentList())
//                .reason(toResponse.getReason())
//                .createdAt(LocalDateTime.now())
//                .build();

        return ResponseEntity.ok(chatbotResponse);
    }
}
