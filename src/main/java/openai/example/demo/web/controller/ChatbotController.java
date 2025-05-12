package openai.example.demo.web.controller;

import openai.example.demo.domain.Message;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    public ResponseEntity<String> chat(@RequestParam("prompt") String prompt) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        ChatbotRequest chatbotRequest = new ChatbotRequest();
        chatbotRequest.addMessage("system", "");
        chatbotRequest.addMessage("assistant", "");
        chatbotRequest.addMessage("user", "");
        chatbotRequest.builder()
                        .temperature(0)
                                .max_completion_tokens(256)
                                        .frequency_penalty(1.0)
                                                .presence_penalty(-0.5).build();

        ChatbotResponse response = restTemplate.postForObject(
                openaiApiUrl, chatbotRequest, ChatbotResponse.class
        );

        return ResponseEntity.ok(response.getChoices().get(0).getMessage().getContent());
    }
}
