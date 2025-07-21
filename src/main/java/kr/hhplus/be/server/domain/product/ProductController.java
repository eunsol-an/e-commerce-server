package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.dto.ProductDto;
import kr.hhplus.be.server.docs.ProductApiDocs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController implements ProductApiDocs {

    @GetMapping
    public ResponseEntity<List<ProductDto.ProductResponse>> getAllProducts() {
        List<ProductDto.ProductResponse> productResponseList = List.of(
                new ProductDto.ProductResponse(1L, "머쉬룸 스탠드", 32000L, 100),
                new ProductDto.ProductResponse(2L, "플라워 러그", 15000L, 100)
        );
        return ResponseEntity.ok(productResponseList);
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
