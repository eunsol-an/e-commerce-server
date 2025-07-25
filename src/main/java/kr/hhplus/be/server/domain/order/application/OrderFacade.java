package kr.hhplus.be.server.domain.order.application;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.point.applicatioin.PointCommand;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.product.application.ProductService;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final PointService pointService;
    private final CouponService couponService;

    @Transactional
    public void orderPayment(OrderCommand.Create command) {
        // 1. 재고 확인
        List<OrderItem> orderItems = command.items().stream()
                .map(itemCommand -> {
                    Product product = productService.validateStock(itemCommand.productId(), itemCommand.quantity());
                    return OrderItem.create(product.getId(), itemCommand.quantity(), product.getPrice() * itemCommand.quantity());
                })
                .toList();


        // 2. 쿠폰 유효성 검증 및 사용
        long discountAmount = 0L;
        if (command.couponPolicyId() != null) {
            Coupon coupon = couponService.validate(command.couponPolicyId(), command.userId());
            CouponPolicy couponPolicy = couponService.getCouponPolicy(coupon.getId());
            discountAmount = couponPolicy.getDiscountAmount();
            couponService.use(command.couponPolicyId(), command.userId());
        }

        // 3. 결제 금액 계산
        long totalItemPrice = productService.calculateTotalItemPrice(command.items());

        // 4. 포인트 잔액 확인
        pointService.validateBalance(command.userId(), totalItemPrice - discountAmount);

        // 5. 포인트 차감
        pointService.use(PointCommand.Use.of(UserPoint.of(command.userId(), totalItemPrice - discountAmount)));

        // 6. 재고 차감
        productService.deductStock(command.items());

        // 7. 주문 생성
        Order newOrder = Order.create(command.userId(), command.couponPolicyId(), orderItems, totalItemPrice, discountAmount);

        // 8. 주문 저장
        orderService.save(newOrder);
    }
}

