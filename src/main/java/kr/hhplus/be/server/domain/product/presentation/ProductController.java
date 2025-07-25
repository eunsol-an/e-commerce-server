package kr.hhplus.be.server.domain.product.presentation;

import kr.hhplus.be.server.docs.ProductApiDocs;
import kr.hhplus.be.server.domain.product.application.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController implements ProductApiDocs {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductDto.ProductListResponse> getAllProducts() {
        List<ProductDto.ProductResponse> productResponseList = ProductDto.ProductResponse.of(productService.getAllProducts());
        return ResponseEntity.ok(ProductDto.ProductListResponse.of(productResponseList));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ProductDto.ProductResponse.of(productService.getProduct(productId)));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductDto.PopularProductResponse>> getPopularProducts() {
        List<ProductDto.PopularProductResponse> productPopularResponseList = List.of(
                new ProductDto.PopularProductResponse(1L, "머쉬룸 스탠드", 320000L, 10),
                new ProductDto.PopularProductResponse(2L, "플라워 러그", 75000L, 5)
        );
        return ResponseEntity.ok(productPopularResponseList);
    }
}
