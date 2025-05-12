package openai.example.demo.web.controller;

import openai.example.demo.domain.Message;
import openai.example.demo.web.dto.ChatbotRequest;
import openai.example.demo.web.dto.ChatbotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

        ChatbotRequest request = new ChatbotRequest(model, prompt);
        request.getMessages().add(new Message("assistant", ""));
        request.getMessages().add(new Message("system", ""));
        request.setTemperature(0);
        request.setMax_completion_tokens(256);
        request.setFrequency_penalty(1.0);
        request.setPresence_penalty(-0.5);

        ChatbotResponse response = restTemplate.postForObject(
                openaiApiUrl, request, ChatbotResponse.class
        );

        return ResponseEntity.ok(response.getChoices().get(0).getMessage().getContent());
    }
}
