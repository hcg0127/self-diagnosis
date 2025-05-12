package openai.example.demo.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openai.example.demo.domain.Message;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRequest {

    private String model;

    private List<Message> messages;

    private double temperature;

    private Integer max_completion_tokens;

    private double frequency_penalty;

    private double presence_penalty;

    public ChatbotRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }
}
