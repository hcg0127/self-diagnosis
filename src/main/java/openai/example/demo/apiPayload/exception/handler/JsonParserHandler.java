package openai.example.demo.apiPayload.exception.handler;

import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.exception.GeneralException;

public class JsonParserHandler extends GeneralException {

    public JsonParserHandler(BaseErrorCode code) {
        super(code);
    }
}
