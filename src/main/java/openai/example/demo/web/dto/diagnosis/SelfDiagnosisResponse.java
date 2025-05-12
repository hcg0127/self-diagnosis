package openai.example.demo.web.dto.diagnosis;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SelfDiagnosisResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateResultDTO {
        String completionId;
        List<String> departmentList;
        String reason;
        LocalDateTime createdAt;
    }
}
