package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.dto.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> placeOrder(
            @RequestBody OrderDto.OrderRequest request
    ) {
        List<OrderDto.OrderItemResponse> itemResponseList = List.of(
                new OrderDto.OrderItemResponse(1L, "머쉬룸 스탠드", 32000L, 1),
                new OrderDto.OrderItemResponse(2L, "플라워 러그", 15000L, 1)
        );
        return ResponseEntity.ok(new OrderDto.OrderResponse(1L, 1000L, 100L, 1000L, itemResponseList));
    }
}
