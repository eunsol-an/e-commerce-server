package kr.hhplus.be.server.domain.ranking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingBatchJob {
    private final RankingService rankingService;

    // 5분마다 실행
    @Scheduled(cron = "0 */5 * * * *")
    public void aggregateRecentRanking() {
        rankingService.refreshTop5ProductsCache();
    }

    // 매일 자정마다 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshCache() {
        // 캐시 초기화 → 새로 집계된 데이터 기반으로 다시 채움
        rankingService.evictTop5ProductsCache();
    }
}

