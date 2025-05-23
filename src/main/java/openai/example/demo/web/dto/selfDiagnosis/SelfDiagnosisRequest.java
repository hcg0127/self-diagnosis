package openai.example.demo.web.dto.selfDiagnosis;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class SelfDiagnosisRequest {

    // V2
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

    // V2
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDepartmentDTO {

        // V3
        @NotNull
        private String lang;

        @NotNull
        private Long symptomId;

        @NotNull
        private List<List<String>> answers;
    }

    // V3
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateSymptomQuestionsDTO {

        @NotNull
        private String lang;

        @NotNull
        private Long symptomId;
    }
}
