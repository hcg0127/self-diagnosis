package openai.example.demo.web.dto.selfDiagnosis;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SelfDiagnosisResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateResultDTO {
        String id; // completionId
        List<Department> departmentList;
        String reason;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Department {
        String ko;
        String en;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChoiceContent {
        List<Department> departmentList;
        String reason;
    }
}
