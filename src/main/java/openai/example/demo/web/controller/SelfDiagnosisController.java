package openai.example.demo.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    @PostMapping("/chatbot")
    public ResponseEntity<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid SelfDiagnosisRequest.CreateDTO req) throws IOException {

        // RestTemplate = 외부로 HTTP 전송
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        // OpenAI API URL로 보낼 request 작성
        ChatbotRequest chatbotRequest = new ChatbotRequest().builder()
                .model(model)
                .temperature(0)
                .max_completion_tokens(256)
                .frequency_penalty(1.0)
                .presence_penalty(-0.5)
                .build();

        // request에 message 추가
        String developerPrompt = Files.readString(Paths.get("developer-prompt.txt"), StandardCharsets.UTF_8);
        chatbotRequest.addMessage("develop", developerPrompt);
        chatbotRequest.addMessage("user", "");

        // OpenAI response 받아오기
        ChatbotResponse chatbotResponse = restTemplate
                .postForObject(openaiApiUrl, chatbotRequest, ChatbotResponse.class);

        // response에서 JSON을 DTO로 변환
        SelfDiagnosisResponse.CreateResultDTO response = SelfDiagnosisResponse.CreateResultDTO
                .builder()
                .id(chatbotResponse.getId())
                .departmentList(new ArrayList<>())
                .reason("")
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
