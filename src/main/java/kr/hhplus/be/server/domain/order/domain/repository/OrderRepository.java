package kr.hhplus.be.server.domain.order.domain.repository;

import kr.hhplus.be.server.domain.order.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    Order save(Order order);
    List<Order> findAll();
}
