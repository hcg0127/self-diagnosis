package openai.example.demo.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid SelfDiagnosisRequest.CreateDTO req) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // RestTemplate = 외부로 HTTP 전송
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        // OpenAI API URL로 보낼 request 작성
        JsonNode rootNode = mapper.readTree(new File("chat-response-format.json"));

        ChatbotRequest chatbotRequest = new ChatbotRequest().builder()
                .model(model)
                .temperature(0)
                .max_completion_tokens(256)
                .frequency_penalty(1.0)
                .presence_penalty(-0.5)
                .response_format(rootNode)
                .build();

        // request에 develop message 추가
        String developerPrompt = Files.readString(Paths.get("src/main/resources/static/self-diagnosis/developer-prompt.txt"), StandardCharsets.UTF_8);
        chatbotRequest.addMessage("develop", developerPrompt);

        // request에 user message 추가
        String userPrompt = "Please recommend a medical department that suits my symptoms.";
        chatbotRequest.addMessage("user", userPrompt);

        // OpenAI response 받아오기
        ChatbotResponse chatbotResponse = restTemplate
                .postForObject(openaiApiUrl, chatbotRequest, ChatbotResponse.class);
        String choiceContent = chatbotResponse.getChoices().getFirst().getMessage().getContent();

        // OpenAI response를 DTO로 변환하기 위한 객체 생성
        SelfDiagnosisResponse.ChoiceContent toResponse = mapper.readValue(choiceContent, SelfDiagnosisResponse.ChoiceContent.class);

        // response에서 JSON을 DTO로 변환
        SelfDiagnosisResponse.CreateResultDTO response = SelfDiagnosisResponse.CreateResultDTO
                .builder()
                .id(chatbotResponse.getId())
                .departmentList(toResponse.getDepartmentList())
                .reason(toResponse.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
