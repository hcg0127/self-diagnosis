package openai.example.demo.apiPayload.exception.handler;

import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.exception.GeneralException;

public class TempHanlder extends GeneralException {

    public TempHanlder(BaseErrorCode code) {
        super(code);
    }
}
