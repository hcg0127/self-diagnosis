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
}
