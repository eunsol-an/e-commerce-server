package kr.hhplus.be.server.domain.order.application;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.common.annotation.DistributedLock;
import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.point.applicatioin.PointCommand;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.product.application.ProductService;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.ranking.application.RankingService;
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
    private final RankingService rankingService;

    @Transactional
    @DistributedLock(
            prefix = "product",
            keys = {"#command.items.![productId]"}, // 멀티락
            waitTime = 5,
            leaseTime = 2
    )
    public void orderPayment(OrderCommand.Create command) {
        // 1. 재고 확인
        List<Product> products = productService.validateStocks(command.items());

        // 2. 주문 생성
        Order newOrder = orderService.createOrder(command, products);

        // 3. 쿠폰 유효성 검증 및 사용
        if (command.couponId() != null) {
            long discountAmount = couponService.applyCoupon(command.couponId(), command.userId());
            newOrder.applyDiscount(discountAmount);
        }

        // 4. 포인트 잔액 확인
        pointService.validateBalance(command.userId(), newOrder.getPaidAmount());

        // 5. 포인트 차감
        pointService.use(PointCommand.Use.of(UserPoint.of(command.userId(), newOrder.getPaidAmount(), 0L)));

        // 6. 재고 차감
        productService.deductStock(command.items());

        // 7. 주문 저장
        orderService.save(newOrder);

        // 8. 주문 후 랭킹 점수 증가
        newOrder.getItems().forEach(item -> rankingService.increaseScore(item.getProductId(), item.getQuantity()));
    }
}

