package openai.example.demo.apiPayload.exception.handler;

import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.exception.GeneralException;

public class SymptomHandler extends GeneralException {

    public SymptomHandler(BaseErrorCode code) {
        super(code);
    }
}
