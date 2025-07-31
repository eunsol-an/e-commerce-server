package kr.hhplus.be.server.domain.coupon.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponPolicy {
    private Long id;
    private long discountAmount;
    private int totalQuantity;
    private int issuedCount;
    private int validDays;

    @Builder
    private CouponPolicy(Long id, long discountAmount, int totalQuantity, int issuedCount, int validDays) {
        this.id = id;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.issuedCount = issuedCount;
        this.validDays = validDays;
    }

    public static CouponPolicy of(Long id, long discountAmount, int totalQuantity, int issuedCount, int validDays) {
        return CouponPolicy.builder()
                .id(id)
                .discountAmount(discountAmount)
                .totalQuantity(totalQuantity)
                .issuedCount(issuedCount)
                .validDays(validDays)
                .build();
    }

    public boolean isIssuable() {
        return issuedCount < totalQuantity;
    }

    public void increaseIssuedCount() {
        this.issuedCount += 1;
    }
}
