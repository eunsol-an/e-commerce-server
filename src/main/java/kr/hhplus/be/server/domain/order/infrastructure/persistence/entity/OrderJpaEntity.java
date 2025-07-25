package kr.hhplus.be.server.domain.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@NoArgsConstructor
public class OrderJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(name = "coupon_policy_id")
    private Long couponPolicyId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    @Column(nullable = false)
    private long totalItemPrice;

    @Column(nullable = false)
    private long discountAmount;

    @Column(nullable = false)
    private long paidAmount;

}
