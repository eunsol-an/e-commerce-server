package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.exception.ApiException;
import kr.hhplus.be.server.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("ProductService 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    
    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("상품이 정상적으로 조회 된다")
    void 상품조회() {
        Long productId = 1L;
        Product product = new Product(productId, "머쉬룸 조명", 32000L, 154);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        ProductInfo.Product productInfo = productService.getProduct(productId);

        then(productRepository).should(times(1)).findById(productId);
        assertEquals(productId, productInfo.id());
        assertEquals(product.getName(), productInfo.name());
        assertEquals(product.getPrice(), productInfo.price());
        assertEquals(product.getStockQuantity(), productInfo.stockQuantity());
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    void 상품조회_예외() {
        Long productId = 1L;
        given(productRepository.findById(productId)).willThrow(new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        assertThrows(ApiException.class, () -> productService.getProduct(productId));
    }
}