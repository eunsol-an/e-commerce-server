package kr.hhplus.be.server.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.dto.ProductDto;
import kr.hhplus.be.server.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
@RequestMapping("/products")
public interface ProductApiDocs {

    @Operation(summary = "전체 상품 조회", description = "모든 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.ProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러",
                                            summary = "상품 목록 조회 중 서버 에러 발생",
                                            value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }
                    ))
    })
    @GetMapping
    ResponseEntity<List<ProductDto.ProductResponse>> getAllProducts();

    @Operation(summary = "인기 상품 조회", description = "인기 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 상품 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.PopularProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러",
                                            summary = "인기 상품 조회 중 서버 에러 발생",
                                            value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }
                    ))
    })
    @GetMapping("/popular")
    ResponseEntity<List<ProductDto.PopularProductResponse>> getPopularProducts();
}
