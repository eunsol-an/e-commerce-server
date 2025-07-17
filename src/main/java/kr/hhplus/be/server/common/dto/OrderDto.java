package kr.hhplus.be.server.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class OrderDto {
    @Schema(description = "주문 요청 Request")
    public record OrderRequest(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "주문 아이템 목록", example = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}")
            List<OrderItemRequest> items,
            @Schema(description = "쿠폰 ID", example = "10")
            Long couponId // nullable
    ) {}

    @Schema(description = "주문 요청 Response")
    public record OrderResponse(
            @Schema(description = "주문 ID", example = "1")
            Long orderId,
            @Schema(description = "총 상품 금액", example = "47000")
            Long totalItemPrice,
            @Schema(description = "할인 금액", example = "1000")
            Long discountAmount,
            @Schema(description = "총 결제 금액", example = "46000")
            Long paidAmount,
            @Schema(description = "주문 아이템 목록", example = "1")
            List<OrderItemResponse> items
    ) {}

    @Schema(description = "주문 아이템 Request")
    public record OrderItemRequest(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "주문 수량", example = "2")
            Integer quantity
    ) {}

    @Schema(description = "주문 아이템 Response")
    public record OrderItemResponse (
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품명", example = "머쉬룸 스탠드")
            String name,
            @Schema(description = "상품 가격", example = "32000")
            Long price,
            @Schema(description = "재고 수량", example = "100")
            Integer quantity
    ) {

    }
}
