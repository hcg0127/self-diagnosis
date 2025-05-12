package openai.example.demo.web.dto.chatbot;

import lombok.*;
import openai.example.demo.domain.Message;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {

    private String id; // completionId

    private List<Choice> choices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice {

        private int index;

        private Message message;
    }
}
