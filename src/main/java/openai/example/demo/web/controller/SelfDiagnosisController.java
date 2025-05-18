package openai.example.demo.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openai.example.demo.apiPayload.ApiResponse;
import openai.example.demo.service.SelfDiagnosisService;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController()
@RequestMapping("/self-diagnosis")
@RequiredArgsConstructor
public class SelfDiagnosisController {

    private final SelfDiagnosisService selfDiagnosisService;

    @PostMapping("/v1/chat")
    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDTO req) throws IOException, ParseException {
        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createSelfDiagnosis(req);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/symptom-question")
    public ApiResponse<SelfDiagnosisResponse.SymptomQuestionResultDTO> getSymptomQuestion(@RequestBody @RequestParam("symptom") String symptom) throws IOException {
        SelfDiagnosisResponse.SymptomQuestionResultDTO result = selfDiagnosisService.createSymptomQuestion(symptom);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/v2/chat")
    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDepartmentDTO req) throws IOException, ParseException {
        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createDepartment(req);
        return ApiResponse.onSuccess(result);
    }
}
