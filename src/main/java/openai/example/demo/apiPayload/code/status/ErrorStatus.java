package openai.example.demo.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import openai.example.demo.apiPayload.code.BaseErrorCode;
import openai.example.demo.apiPayload.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //parse
    JSON_FORMAT_UNMATCHED(HttpStatus.NOT_FOUND, "JSON4001", "JSON 형식이 올바르지 않습니다. 토큰의 최댓값을 늘려주세요."),

    //symptom
    SYMPTOM_NOT_FOUND(HttpStatus.NOT_FOUND, "SYMPTOM4001", "해당하는 증상을 찾을 수 없습니다."),
    SYMPTOM_SIZE_NOT_MATCH(HttpStatus.BAD_REQUEST, "SYMPTOM4002", "질문과 답변의 개수가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
