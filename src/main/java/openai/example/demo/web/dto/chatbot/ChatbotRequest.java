package openai.example.demo.web.dto.chatbot;

import lombok.*;
import openai.example.demo.domain.Message;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRequest {

    private String model;

    private List<Message> messages = new ArrayList<>();

    private double temperature;

    private Integer max_completion_tokens;

    private double frequency_penalty;

    private double presence_penalty;

    public ChatbotRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }

    public void addMessage(String role, String content) {
        Message message = new Message(role, content);
        this.messages.add(message);
    }
}
