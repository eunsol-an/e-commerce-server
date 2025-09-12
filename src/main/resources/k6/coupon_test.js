// javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// 환경 변수 설정 (필요에 따라 조정)
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const POLICY_ID = Number(__ENV.POLICY_ID || '1');

// 인증 토큰이 필요한 경우 제공 (없으면 자동 무시)
const AUTH_TOKEN = __ENV.AUTH_TOKEN || '';

// 피크(스파이크) 부하 설정
// - PEAK_RPS: 스파이크 도달 목표 RPS
// - HOLD_SEC: 피크 유지 시간
// - RAMP_SEC: 피크로 치솟는 데 걸리는 시간
// - COOLDOWN_SEC: 피크 이후 감소 시간
const PEAK_RPS = Number(__ENV.PEAK_RPS || '2000');
const HOLD_SEC = Number(__ENV.HOLD_SEC || '30');
const RAMP_SEC = Number(__ENV.RAMP_SEC || '5');
const COOLDOWN_SEC = Number(__ENV.COOLDOWN_SEC || '10');

// 사용자 ID 분포(중복 충돌/경합 상황을 일부 반영하기 위해 랜덤 사용)
const USER_MIN_ID = Number(__ENV.USER_MIN_ID || '1');
const USER_MAX_ID = Number(__ENV.USER_MAX_ID || '10000'); // 테스트용 가상 사용자 풀

// 헤더 기본값
function buildHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    if (AUTH_TOKEN) headers['Authorization'] = `Bearer ${AUTH_TOKEN}`;
    return headers;
}

// 커스텀 메트릭
export const issueLatency = new Trend('issue_latency', true);
export const issueErrorRate = new Rate('issue_error_rate');

export const options = {
    discardResponseBodies: true,
    thresholds: {
        // 전체 실패율(네트워크/프로토콜 레벨)
        http_req_failed: ['rate<0.01'],
        // 전체 요청 지연시간
        http_req_duration: ['p(95)<200'],
        // 쿠폰 발급 응답 체크 통과율
        checks: ['rate>0.95'],
    },
    scenarios: {
        spike_issue: {
            executor: 'ramping-arrival-rate',
            startRate: 0,
            timeUnit: '1s',
            preAllocatedVUs: Math.max(PEAK_RPS / 10, 50), // 사전 할당 VU(대략치)
            maxVUs: Math.max(PEAK_RPS * 2, 2000), // 상한(충분히 크게)
            stages: [
                // 워밍업 (선택적으로 0 유지)
                { target: 0, duration: '5s' },
                // 스파이크 상승 구간
                { target: PEAK_RPS, duration: `${RAMP_SEC}s` },
                // 피크 유지
                { target: PEAK_RPS, duration: `${HOLD_SEC}s` },
                // 쿨다운
                { target: 0, duration: `${COOLDOWN_SEC}s` },
            ],
            exec: 'issueCoupon',
        },
    },
};

// 유틸: 난수
function randInt(min, maxInclusive) {
    return Math.floor(Math.random() * (maxInclusive - min + 1)) + min;
}

export function issueCoupon() {
    const userId = randInt(USER_MIN_ID, USER_MAX_ID);

    const url = `${BASE_URL}/coupons/issue`;
    const payload = JSON.stringify({
        userId: userId,
        couponPolicyId: POLICY_ID,
    });

    const res = http.post(url, payload, {
        headers: buildHeaders(),
        tags: { endpoint: 'issueCoupon' },
    });

    // 성공으로 간주할 응답 코드 정의
    // - 기본은 200 OK
    // - 환경과 정책에 따라, 중복발급/재시도 시 409/429/4xx도 예상 가능
    const ok = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    issueLatency.add(res.timings.duration);
    issueErrorRate.add(!ok);

    // 초 단위 슬립은 arrival-rate 실행기에서는 의미가 거의 없음(다음 도착 스케줄에 의해 호출됨)
    // 필요 시 소량의 think time을 추가할 수 있으나, 피크 재현에는 일반적으로 불필요
    // sleep(0.01);
}