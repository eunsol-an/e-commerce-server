package kr.hhplus.be.server.domain.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
}
