package openai.example.demo.apiPayload.exception.handler;

import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.exception.GeneralException;

public class JsonParserHanlder extends GeneralException {

    public JsonParserHanlder(BaseErrorCode code) {
        super(code);
    }
}
