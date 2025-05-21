package openai.example.demo.web.dto.selfDiagnosis;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SelfDiagnosisResponse {

    // V2
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateResultDTO {
        String id; // completionId
        List<Department> departmentList;
        Reason reason;
        LocalDateTime createdAt;
    }

    // V2
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Department {
        String en;
        String ko;
    }

    // V2
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reason {
        String en;
        String ko;
    }

    // V3
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SymptomQuestionResultDTO {
        String symptom;
        List<Question> questions;
    }

    // V3
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        String question;
        List<String> options;
    }

    // V4
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Top5SymptomAndDetailSymptomResultDTO {
        List<Symptom> top5SymptomList;
        List<DetailSymptom> top5DetailSymptomList;
    }

    // V4
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Symptom {
        Long symptomId;
        String symptomName;
        String symptomDescription;
    }

    // V4
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailSymptom {
        Long detailSymptomId;
        String detailSymptomDescription;
    }
}
