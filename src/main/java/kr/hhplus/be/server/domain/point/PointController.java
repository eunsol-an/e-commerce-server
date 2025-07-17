package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.dto.PointDto;
import kr.hhplus.be.server.docs.PointApiDocs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointController implements PointApiDocs {

    @PostMapping("/charge")
    public ResponseEntity<PointDto.BalanceResponse> chargePoint(
            @RequestBody PointDto.PointChargeRequest request
) {
        return ResponseEntity.ok(new PointDto.BalanceResponse(request.userId(), request.amount()));
    }

    @GetMapping
    public ResponseEntity<PointDto.BalanceResponse> getBalance(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(new PointDto.BalanceResponse(userId, 1000L));
    }
}
