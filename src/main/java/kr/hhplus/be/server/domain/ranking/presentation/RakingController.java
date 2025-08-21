package kr.hhplus.be.server.domain.ranking.presentation;

import kr.hhplus.be.server.domain.ranking.application.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RakingController {
    private final RankingService rankingService;

    @GetMapping("/popular")
    public ResponseEntity<List<RankingDto.PopularProductResponse>> getPopularProducts() {
        return ResponseEntity.ok(RankingDto.PopularProductResponse.of(rankingService.getTop5ProductsLast3Days()));
    }
}
