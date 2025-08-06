package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("ProductService 통합 테스트")
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품이 DB에 저장되고 서비스로 조회된다")
    void 상품_저장_및_조회() {
        // given
        Product saved = productRepository.save(Product.of(null, "머쉬룸 조명", 32000L, 154));
        Long productId = saved.getId();

        // when
        ProductInfo.Product result = productService.getProduct(productId);

        // then
        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("머쉬룸 조명");
        assertThat(result.price()).isEqualTo(32000L);
        assertThat(result.stockQuantity()).isEqualTo(154L);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생")
    void 상품_조회_실패() {
        // given
        Long productId = 999L;

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            productService.getProduct(productId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(PRODUCT_NOT_FOUND);
    }

}
