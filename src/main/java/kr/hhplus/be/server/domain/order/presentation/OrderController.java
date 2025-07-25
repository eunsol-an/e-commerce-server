package kr.hhplus.be.server.domain.order.presentation;

import kr.hhplus.be.server.docs.OrderApiDocs;
import kr.hhplus.be.server.domain.order.application.OrderCommand;
import kr.hhplus.be.server.domain.order.application.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApiDocs {
    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<Void> placeOrder(
            @RequestBody OrderDto.OrderRequest request
    ) {
        orderFacade.orderPayment(OrderCommand.Create.of(request.userId(), request.items(), request.couponId()));
        return ResponseEntity.ok().build();
    }
}
