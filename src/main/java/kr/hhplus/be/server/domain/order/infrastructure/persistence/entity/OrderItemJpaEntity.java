package kr.hhplus.be.server.domain.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    @Column(nullable = false, name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private long price;
}
