package kr.hhplus.be.server.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.presentation.CouponDto;
import kr.hhplus.be.server.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "쿠폰", description = "쿠폰 관련 API")
@RequestMapping("/coupons")
public interface CouponApiDocs {

    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CouponDto.CouponIssueResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 요청",
                                            summary = "이미 발급한 유저",
                                            value = "{\"code\":400, \"message\":\"INVALID REQUEST\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유저 없음",
                                            summary = "해당 유저가 존재하지 않음",
                                            value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "409", description = "요청 충돌",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "쿠폰 소진",
                                            summary = "잔여 쿠폰 없음",
                                            value = "{\"code\":409, \"message\":\"CONFLICT\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러",
                                            summary = "쿠폰 발급 중 서버 에러 발생",
                                            value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }))
    })
    @PostMapping("/issue")
    ResponseEntity<CouponDto.CouponIssueResponse> issueCoupon(
            @RequestBody
            @Parameter(description = "쿠폰 발급 요청 정보", required = true)
            CouponDto.CouponIssueRequest request
    );

    @Operation(summary = "쿠폰 목록 조회", description = "사용자의 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CouponDto.CouponListResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유저 없음",
                                            summary = "해당 유저가 존재하지 않음",
                                            value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러",
                                            summary = "쿠폰 조회 중 서버 에러 발생",
                                            value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }))
    })
    @GetMapping
    ResponseEntity<List<CouponDto.CouponListResponse>> getCoupons(
            @RequestParam
            @Parameter(description = "조회할 사용자 ID", required = true)
            Long userId
    );
}
