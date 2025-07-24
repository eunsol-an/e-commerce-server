package kr.hhplus.be.server.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.point.presentation.PointDto;
import kr.hhplus.be.server.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "포인트", description = "포인트 관련 API")
public interface PointApiDocs {

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 요청한 금액만큼 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "충전 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 충전 금액", summary = "금액이 0 이하", value = "{\"code\":400, \"message\":\"INVALID REQUEST\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "없는 유저", summary = "유저를 찾을 수 없음", value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러", summary = "포인트 충전 중 서버 에러 발생", value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/points/charge")
    ResponseEntity<Void> chargePoint(
            @RequestBody
            @Parameter(description = "충전 요청 정보", required = true)
            PointDto.PointChargeRequest request
    );

    @Operation(summary = "포인트 사용", description = "사용자의 포인트를 요청한 금액만큼 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 사용 금액", summary = "금액이 0 이하", value = "{\"code\":400, \"message\":\"INVALID REQUEST\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "없는 유저", summary = "유저를 찾을 수 없음", value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 에러", summary = "포인트 사용 중 서버 에러 발생", value = "{\"code\":500, \"message\":\"INTERNAL SERVER ERROR\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/points/use")
    ResponseEntity<Void> usePoint(
            @RequestBody
            @Parameter(description = "사용 요청 정보", required = true)
            PointDto.PointUseRequest request
    );

    @Operation(summary = "포인트 잔액 조회", description = "사용자의 현재 포인트 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PointDto.BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class),
                            examples = {
                                    @ExampleObject(
                                            name = "없는 유저", summary = "유저를 찾을 수 없음", value = "{\"code\":404, \"message\":\"USER NOT FOUND\"}"
                                    )
                            }
                    )
            ),
    })
    @GetMapping("/points")
    ResponseEntity<PointDto.BalanceResponse> getBalance(
            @RequestParam
            @Parameter(description = "조회할 사용자 ID", required = true)
            Long userId
    );
}
