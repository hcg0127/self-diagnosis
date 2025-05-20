package openai.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openai.example.demo.converter.ChatbotConverter;
import openai.example.demo.web.dto.chatbot.ChatbotRequest;
import openai.example.demo.web.dto.chatbot.ChatbotResponse;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-url}")
    private String openaiApiUrl;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    // V2
//    public ChatbotRequest createChatbotRequest(SelfDiagnosisRequest.CreateDTO req) throws IOException {
//
//        // OpenAI API URL로 보낼 request 작성
//        // request에 넣을 response_format 불러오기
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readTree(new File("src/main/resources/static/self-diagnosis/chat-response-format.json"));
//        ChatbotRequest chatbotRequest = ChatbotConverter.toChatbotRequest(model, 0, 256, 1.0, -0.5, rootNode);
//
//        // request에 develop message 추가
//        // develop message = 모델에게 정확한 지시 내리기
//        String developerPrompt = Files.readString(Paths.get("src/main/resources/static/self-diagnosis/developer-prompt.txt"), StandardCharsets.UTF_8);
//        chatbotRequest.addMessage("developer", developerPrompt);
//
//        // request에 user message 추가
//        // user message = 사용자의 요청
//        String userPrompt = "Please recommend a medical department that suits my symptoms.\n"
//                + " - body system: " + req.getSystem() + "\n"
//                + " - symptom: " + req.getSymptom() + "\n";
//        if (req.getCondition() != null)
//            userPrompt += " - condition: " + req.getCondition() + "\n";
//        if (req.getAdditionalNote() != null)
//            userPrompt += " - additional note: " + req.getAdditionalNote();
//        chatbotRequest.addMessage("user", userPrompt);
//
//        return chatbotRequest;
//    }

    // COMMON
    public ChatbotResponse craeteChatbotResponse(ChatbotRequest chatbotRequest) {

        // RestTemplate = 외부 URL로 HTTP 전송
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        // OpenAI response를 객체로 변환해서 가져오기
        return restTemplate.postForObject(openaiApiUrl, chatbotRequest, ChatbotResponse.class);
    }

    // V3
    public ChatbotRequest createChatbotRequest(String symptom, List<String> questionList, List<List<String>> answerList) throws IOException {

        // OpenAI API URL로 보낼 request 작성
        // request에 넣을 response_format 불러오기
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src/main/resources/static/self-diagnosis/chat-response-format.json"));
        ChatbotRequest chatbotRequest = ChatbotConverter.toChatbotRequest(model, 0, 256, 1.0, -0.5, rootNode);

        // request에 develop message 추가
        // develop message = 모델에게 정확한 지시 내리기
        String developerPrompt = Files.readString(Paths.get("src/main/resources/static/self-diagnosis/developer-prompt.txt"), StandardCharsets.UTF_8);
        chatbotRequest.addMessage("developer", developerPrompt);

        // request에 user message 추가
        // user message = 사용자의 요청
        int len = questionList.size();
        StringBuilder userPrompt = new StringBuilder("Please recommend a medical department that suits my symptoms.\n")
                .append("- My symptom is ").append(symptom).append("\n");
        for (int i=0; i<len; i++) {
            userPrompt.append("- ")
                    .append(questionList.get(i))
                    .append(": ")
                    .append(answerList.get(i));
        }
        chatbotRequest.addMessage("user", userPrompt.toString());

        return chatbotRequest;
    }
}
