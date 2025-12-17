# lotecs-auth 개선 로드맵

## 우선순위별 개선 항목

### P0 (Critical - 운영 전 필수)

| 항목 | 영향도 | 작업량 | 상태 |
|------|--------|--------|------|
| SSO Provider 서킷브레이커 적용 | 로그인 전체 | 중 | 미완료 |
| Redis 장애 Fallback 처리 | 인증/인가 전체 | 중 | 미완료 |
| Admin API 인증 필터 추가 | 보안 | 소 | 미완료 |

### P1 (High - 운영 전 권장)

| 항목 | 영향도 | 작업량 | 상태 |
|------|--------|--------|------|
| DB 커넥션 타임아웃 조정 (30s -> 5s) | 장애 대응 | 소 | 미완료 |
| 외부 호출 타임아웃 표준화 | 장애 대응 | 소 | 미완료 |
| 계정 잠금 동시성 제어 | 데이터 정합성 | 소 | 미완료 |

### P2 (Medium - 운영 중 개선)

| 항목 | 영향도 | 작업량 | 상태 |
|------|--------|--------|------|
| Redis Sentinel/Cluster 구성 | 가용성 | 대 | 미완료 |
| 도메인별 커스텀 예외 클래스 | 유지보수 | 중 | 미완료 |
| 에러 코드 체계화 | 클라이언트 연동 | 중 | 미완료 |

### P3 (Low - 장기 개선)

| 항목 | 영향도 | 작업량 | 상태 |
|------|--------|--------|------|
| Actuator 엔드포인트 보안 설정 | 정보 노출 | 소 | 미완료 |
| 로그인 시도 이력 저장 (감사용) | 감사/추적 | 중 | 미완료 |
| i18n 메시지 적용 | 사용자 경험 | 중 | 미완료 |

## 상세 구현 계획

### Phase 1: 서킷브레이커 적용

**목표:** 외부 시스템 장애 시 서비스 안정성 확보

1. lotecs-circuit-breaker 모듈 의존성 추가
2. SSO Provider 서킷브레이커 적용
   - RelaySsoProvider
   - KeycloakSsoProvider
   - LdapSsoProvider
3. Redis 호출 Fallback 처리
   - JWT 블랙리스트 (Fail-Open)
   - Rate Limit (Graceful Degradation)
4. 서킷브레이커 상태 모니터링 설정

### Phase 2: 보안 강화

**목표:** 운영환경 보안 취약점 제거

1. Admin API 인증 필터 구현
   - JWT 토큰 검증
   - 관리자 권한 확인
2. Actuator 엔드포인트 보안
   - 허용 IP 제한
   - 인증 필수화
3. CORS 설정 강화
   - 허용 도메인 명시

### Phase 3: 안정성 개선

**목표:** 장애 발생 시 영향 최소화

1. 타임아웃 표준화
   - DB: 5초
   - Redis: 1초
   - 외부 API: 3~5초
2. 동시성 제어
   - 계정 잠금 로직 개선
   - 분산 락 적용 검토
3. Redis 고가용성 구성
   - Sentinel 또는 Cluster 구성

### Phase 4: 운영 편의성

**목표:** 운영/모니터링 효율화

1. 커스텀 예외 클래스 체계화
2. 에러 코드 정의
3. 로그인 이력 저장
4. 메트릭 대시보드 구성

## 참고 문서

- [architecture-analysis.md](./architecture-analysis.md) - 서비스 아키텍처 분석
- [production-risk-analysis.md](./production-risk-analysis.md) - 운영환경 위험 요소
- [circuit-breaker-implementation-guide.md](./circuit-breaker-implementation-guide.md) - 서킷브레이커 구현 가이드
