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

//    @PostMapping("/v1/chat")
//    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDTO req) throws IOException, ParseException {
//        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createSelfDiagnosis(req);
//        return ApiResponse.onSuccess(result);
//    }

//    @PostMapping("/v2/symptom-questions")
//    public ApiResponse<SelfDiagnosisResponse.SymptomQuestionResultDTO> getSymptomQuestions(@RequestParam("symptom") String symptom) throws IOException {
//        SelfDiagnosisResponse.SymptomQuestionResultDTO result = selfDiagnosisService.createSymptomQuestions(symptom);
//        return ApiResponse.onSuccess(result);
//    }

//    @PostMapping("/v2/chat")
//    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDepartmentDTO req) throws IOException, ParseException {
//        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createDepartment(req);
//        return ApiResponse.onSuccess(result);
//    }

    @GetMapping("/v3/symptom-questions")
    public ApiResponse<SelfDiagnosisResponse.SymptomQuestionResultDTO> getSymptomQuestions(@RequestBody @Valid SelfDiagnosisRequest.CreateSymptomQuestionsDTO request) throws IOException {
        SelfDiagnosisResponse.SymptomQuestionResultDTO result = selfDiagnosisService.createSymptomQuestions(request);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/v3/chat")
    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> getSelfDiagnosisResult(@RequestBody @Valid SelfDiagnosisRequest.CreateDepartmentDTO request) throws IOException, ParseException {
        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createDepartmentResult(request);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/v4/top5")
    public ApiResponse<SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO> getTop5SymptomAndDetailSymptom(@RequestParam("lang") String lang) {
        SelfDiagnosisResponse.Top5SymptomAndDetailSymptomResultDTO result = selfDiagnosisService.getTop5SymptomAndDetailSymptom(lang);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/v4/symptoms")
    public ApiResponse<SelfDiagnosisResponse.getSymptomsWithDetailSymptomResultDTO> getSymptoms(@RequestParam("lang") String lang, @RequestParam("detailSymptomId") Long detailSymptomId) {
        SelfDiagnosisResponse.getSymptomsWithDetailSymptomResultDTO result = selfDiagnosisService.getSymptomsWithDetailSymptom(lang, detailSymptomId);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/v4/detail-symptoms/search")
    public ApiResponse<SelfDiagnosisResponse.getDetailSymptomsResultDTO> getDetailSymptoms(@RequestParam("lang") String lang, @RequestParam("word") String word) {
        SelfDiagnosisResponse.getDetailSymptomsResultDTO result = selfDiagnosisService.searchDetailSymptoms(lang, word);
        return ApiResponse.onSuccess(result);
    }
}
