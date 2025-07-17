package kr.hhplus.be.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(-1000, "INVALID INPUT VALUE", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND(-1001, "ENTITY NOT FOUND", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE(-1002, "DUPLICATE RESOURCE", HttpStatus.CONFLICT),
    UNAUTHORIZED(-1003, "UNAUTHORIZED ACCESS", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(-1004, "FORBIDDEN ACCESS", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR(-9999, "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    ;
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
