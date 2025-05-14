package openai.example.demo.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openai.example.demo.apiPayload.ApiResponse;
import openai.example.demo.service.SelfDiagnosisService;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisRequest;
import openai.example.demo.web.dto.selfDiagnosis.SelfDiagnosisResponse;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController()
@RequestMapping("/self-diagnosis")
@RequiredArgsConstructor
public class SelfDiagnosisController {

    private final SelfDiagnosisService selfDiagnosisService;

    @PostMapping("/chat")
    public ApiResponse<SelfDiagnosisResponse.CreateResultDTO> chat(@Valid @RequestBody SelfDiagnosisRequest.CreateDTO req) throws IOException, ParseException {
        SelfDiagnosisResponse.CreateResultDTO result = selfDiagnosisService.createSelfDiagnosis(req);
        return ApiResponse.onSuccess(result);
    }
}
