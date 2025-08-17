package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderInfo.Order save(Order newOrder) {
        Order save = orderRepository.save(newOrder);
        return OrderInfo.Order.of(save);
    }

    public Order createOrder(OrderCommand.Create command, List<Product> products) {
        List<OrderItem> orderItems = this.createOrderItem(command.items(), products);
        return Order.create(command.userId(), command.couponId(), orderItems);
    }

    public List<OrderItem> createOrderItem(List<OrderCommand.Item> itemsCommand, List<Product> products) {
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        return itemsCommand.stream()
                .map(itemCommand -> {
                    Product product = productMap.get(itemCommand.productId());
                    return OrderItem.create(
                            product.getId(),
                            itemCommand.quantity(),
                            product.getPrice()
                    );
                })
                .toList();
    }
}
