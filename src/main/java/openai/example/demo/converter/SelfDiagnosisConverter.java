package openai.example.demo.converter;

import lombok.RequiredArgsConstructor;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SelfDiagnosisConverter {

    public static SelfDiagnosisResponse.CreateResultDTO createResultDTO(String id, List<SelfDiagnosisResponse.Department> departmentList, SelfDiagnosisResponse.Reason reason) {
        return SelfDiagnosisResponse.CreateResultDTO.builder()
                .id(id)
                .departmentList(departmentList)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static SelfDiagnosisResponse.SymptomQuestionResultDTO createSymptomQuestionResultDTO(String symptom, List<SelfDiagnosisResponse.Question> questions) {
        return SelfDiagnosisResponse.SymptomQuestionResultDTO.builder()
                .symptom(symptom)
                .questions(questions)
                .build();
    }

    public static SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO getTop5SymptomAndDetailSymptomResultDTO(List<SelfDiagnosisResponse.Symptom> symptoms, List<SelfDiagnosisResponse.DetailSymptom> detailSymptoms) {
        return SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO.builder()
                .top5SymptomList(symptoms)
                .top5DetailSymptomList(detailSymptoms)
                .build();
    }
}
