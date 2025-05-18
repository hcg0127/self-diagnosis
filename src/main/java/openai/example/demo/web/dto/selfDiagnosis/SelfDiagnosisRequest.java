package openai.example.demo.web.dto.selfDiagnosis;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class SelfDiagnosisRequest {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDTO {

        @NotNull
        private String system;

        @NotNull
        private String symptom;

        private String condition;

        private String additionalNote;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDepartmentDTO {

        @NotNull
        private String symptom;

        @NotNull
        private List<String> answers;
    }
}
