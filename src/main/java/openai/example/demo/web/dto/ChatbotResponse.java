package openai.example.demo.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openai.example.demo.domain.Message;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {

    private List<Choice> choices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice {

        private int index;

        private Message message;
    }
}
