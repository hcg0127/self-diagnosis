package openai.example.demo.web.dto.chatbot;

import lombok.*;
import openai.example.demo.domain.Message;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRequest {

    private String model;

    private List<Message> messages;

    private double temperature;

    private Integer max_completion_tokens;

    private double frequency_penalty;

    private double presence_penalty;

    private Object response_format;

    public void addMessage(String role, String content) {
        Message message = new Message(role, content);
        this.messages.add(message);
    }
}
