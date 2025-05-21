package openai.example.demo.converter;

import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SelfDiagnosisConverter {

    // V2
    public static SelfDiagnosisResponse.CreateResultDTO createResultDTO(String id, List<SelfDiagnosisResponse.Department> departmentList, SelfDiagnosisResponse.Reason reason) {
        return SelfDiagnosisResponse.CreateResultDTO.builder()
                .id(id)
                .departmentList(departmentList)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // V3
    public static SelfDiagnosisResponse.SymptomQuestionResultDTO createSymptomQuestionResultDTO(String symptom, List<SelfDiagnosisResponse.Question> questions) {
        return SelfDiagnosisResponse.SymptomQuestionResultDTO.builder()
                .symptom(symptom)
                .questions(questions)
                .build();
    }

    // V4
    public static SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO getTop5SymptomAndDetailSymptomResultDTO(List<SelfDiagnosisResponse.Symptom> symptoms, List<SelfDiagnosisResponse.DetailSymptom> detailSymptoms) {
        return SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO.builder()
                .top5SymptomList(symptoms)
                .top5DetailSymptomList(detailSymptoms)
                .build();
    }
}
