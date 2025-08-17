package kr.hhplus.be.server.domain.product.domain.model;

import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product 도메인 테스트")
class ProductTest {

    @Test
    @DisplayName("상품 재고가 요청 수량보다 많으면 isAvailable은 true를 반환한다")
    void 재고_충분() {
        // given
        Product product = Product.of(1L, "테스트상품", 1000L, 10);

        // when
        boolean available = product.isAvailable(5);

        // then
        assertTrue(available);
    }

    @Test
    @DisplayName("상품 재고가 요청 수량보다 적으면 isAvailable은 false를 반환한다")
    void 재고_부족() {
        Product product = Product.of(1L, "테스트상품", 1000L, 5);

        assertFalse(product.isAvailable(6)); // 초과 요청
    }

    @Test
    @DisplayName("재고 차감이 정상적으로 이루어진다")
    void 재고차감_정상() {
        // given
        Product product = Product.of(1L, "상품", 2000L, 10);

        // when
        product.deductStock(3);

        // then
        assertEquals(7, product.getStockQuantity());
    }

    @Test
    @DisplayName("재고 차감 후 재고는 음수가 될 수 없다")
    void 재고차감_음수예외() {
        // given
        Product product = Product.of(1L, "상품", 2000L, 2);

        // when & then
        assertThrows(ApiException.class, () -> product.deductStock(5));
    }
}