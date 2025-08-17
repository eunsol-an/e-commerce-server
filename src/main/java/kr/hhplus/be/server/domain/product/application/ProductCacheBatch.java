package kr.hhplus.be.server.domain.product.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheBatch {

    private final ProductCacheService productCacheService;

    // 매일 자정(00:00)에 캐시 새로고침
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshTop5ProductsCacheAtMidnight() {
        productCacheService.refreshTop5ProductsCache();
    }
}
