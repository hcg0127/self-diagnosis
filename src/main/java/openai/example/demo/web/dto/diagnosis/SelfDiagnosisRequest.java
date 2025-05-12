package openai.example.demo.web.dto.diagnosis;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
}
