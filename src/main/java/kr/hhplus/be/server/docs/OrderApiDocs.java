package kr.hhplus.be.server.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.order.presentation.OrderDto;
import kr.hhplus.be.server.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "주문", description = "주문 관련 API")
@RequestMapping("/orders")
public interface OrderApiDocs {

    @Operation(summary = "주문 생성", description = "사용자의 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 상품 요청",
                                            summary = "존재하지 않는 상품 ID 포함",
                                            value = "{\"code\":400, \"message\":\"INVALID PRODUCT ID\"}"
                                    ),
                                    @ExampleObject(
                                            name = "쿠폰 없음",
                                            summary = "해당 쿠폰을 보유하고 있지 않거나, 이미 사용함",
                                            value = "{\"code\":400, \"message\":\"INVALID COUPON ID\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "402", description = "결제 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잔액 부족",
                                            summary = "잔액이 부족함",
                                            value = "{\"code\":402, \"message\":\"PAYMENT REQUIRED\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유저 없음",
                                            summary = "주문하려는 유저가 존재하지 않음",
                                            value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "409", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "재고 부족",
                                            summary = "상품의 재고가 부족함",
                                            value = "{\"code\":409, \"message\":\"OUT OF STOCK\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러",
                                            summary = "주문 처리 중 서버 에러 발생",
                                            value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }))
    })
    @PostMapping
    ResponseEntity<Void> placeOrder(
            @RequestBody
            OrderDto.OrderRequest request
    );
}
