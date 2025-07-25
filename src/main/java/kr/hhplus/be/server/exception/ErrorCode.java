package kr.hhplus.be.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(-9999, "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * USER POINT
     */
    USER_NOT_FOUND(-1000, "USER NOT FOUND", HttpStatus.NOT_FOUND),
    USER_POINT_INVALID_CHARGE_AMOUNT(-1001, "INVALID CHARGE AMOUNT", HttpStatus.BAD_REQUEST),
    USER_POINT_INVALID_USE_AMOUNT(-1002, "INVALID USE AMOUNT", HttpStatus.BAD_REQUEST),
    USER_POINT_INSUFFICIENT_BALANCE(-1003, "INSUFFICIENT BALANCE", HttpStatus.BAD_REQUEST),
    USER_POINT_BALANCE_LIMIT_EXCEEDED(-1004, "BALANCE LIMIT EXCEEDED", HttpStatus.BAD_REQUEST),

    /**
     * PRODUCT
     */
    PRODUCT_NOT_FOUND(-1100, "PRODUCT NOT FOUND", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(-1101, "OUT OF STOCK", HttpStatus.BAD_REQUEST),

    /**
     * COUPON
     */
    COUPON_NOT_FOUNT(-1200, "COUPON NOT FOUND", HttpStatus.NOT_FOUND),
    COUPON_INVALID_STATUS(-1201, "INVALID COUPON STATUS", HttpStatus.BAD_REQUEST),
    ALREADY_ISSUED_COUPON(-1202, "ALREADY USED COUPON", HttpStatus.BAD_REQUEST),

    /**
     * ORDER
     */
    POINT_NOT_ENOUGH(-1300, "POINT NOT ENOUGH", HttpStatus.BAD_REQUEST),

    ;
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
