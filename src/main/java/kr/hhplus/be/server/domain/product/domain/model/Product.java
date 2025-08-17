package kr.hhplus.be.server.domain.product.domain.model;

import kr.hhplus.be.server.exception.ApiException;
import lombok.Builder;
import lombok.Getter;

import static kr.hhplus.be.server.exception.ErrorCode.OUT_OF_STOCK;

@Getter
public class Product {
    private Long id;
    private String name;
    private Long price;
    private Integer stockQuantity;

    @Builder
    private Product(Long id, String name, Long price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public static Product of(Long id, String name, Long price, Integer stockQuantity) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    public static Product create(String name, Long price, Integer stockQuantity) {
        return Product.builder()
                .id(null)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    public boolean isAvailable(int requestStockQuantity) {
        return this.stockQuantity >= requestStockQuantity;
    }

    public void deductStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new ApiException(OUT_OF_STOCK);
        }
        this.stockQuantity -= quantity;
    }
}
