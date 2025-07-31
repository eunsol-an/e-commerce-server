package kr.hhplus.be.server.domain.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Setter
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    @Column(nullable = false)
    private long totalItemPrice;

    @Column(nullable = false)
    private long discountAmount;

    @Column(nullable = false)
    private long paidAmount;

}
