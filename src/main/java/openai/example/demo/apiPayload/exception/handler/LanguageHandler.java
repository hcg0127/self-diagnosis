package openai.example.demo.apiPayload.exception.handler;

import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.exception.GeneralException;

public class LanguageHandler extends GeneralException {

    public LanguageHandler(BaseErrorCode code) {
        super(code);
    }
}
