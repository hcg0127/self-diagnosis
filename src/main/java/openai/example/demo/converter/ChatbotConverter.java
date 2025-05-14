package openai.example.demo.converter;

import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;

import java.util.ArrayList;

@RequiredArgsConstructor
public class ChatbotConverter {

    public static ChatbotRequest toChatbotRequest(String model, double temperature, Integer max_completion_tokens, double frequency_penalty, double presence_penalty, Object response_format) {
        return ChatbotRequest.builder()
                .model(model)
                .messages(new ArrayList<>())
                .temperature(temperature)
                .max_completion_tokens(max_completion_tokens)
                .frequency_penalty(frequency_penalty)
                .presence_penalty(presence_penalty)
                .response_format(response_format)
                .build();
    }
}
