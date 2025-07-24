package kr.hhplus.be.server.domain.point.presentation;

import kr.hhplus.be.server.docs.PointApiDocs;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController implements PointApiDocs {
    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(
            @RequestBody PointDto.PointChargeRequest request
) {
        pointService.charge(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/use")
    public ResponseEntity<Void> usePoint(
            @RequestBody PointDto.PointUseRequest request
    ) {
        pointService.use(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PointDto.BalanceResponse> getBalance(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(PointDto.BalanceResponse.of(pointService.getBalance(userId)));
    }
}
