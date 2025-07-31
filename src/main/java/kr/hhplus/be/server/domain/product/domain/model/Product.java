package kr.hhplus.be.server.domain.product.domain.model;

import kr.hhplus.be.server.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static kr.hhplus.be.server.exception.ErrorCode.OUT_OF_STOCK;

@Getter
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Long price;
    private Integer stockQuantity;

    public boolean isAvailable(int requestStockQuantity) {
        return this.stockQuantity > requestStockQuantity;
    }

    public void deductStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new ApiException(OUT_OF_STOCK);
        }
        this.stockQuantity -= quantity;
    }
    public long calculatePriceForQuantity(int quantity) {
        return this.price * quantity;
    }
}
