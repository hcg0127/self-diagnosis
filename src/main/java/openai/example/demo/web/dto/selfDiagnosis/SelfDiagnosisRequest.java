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

        // V3
        @NotNull
        private String lang;

        @NotNull
        private String symptom;

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
        private String symptomName;
    }
}
