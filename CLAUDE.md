# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 빌드 및 실행

```bash
./gradlew compileJava        # 컴파일만 (버그 수정 후 확인 시)
./gradlew build              # 전체 빌드
./gradlew run                # 애플리케이션 실행
```

**DB 초기화 (Docker)**
```bash
docker compose up -d         # 최초 기동 (schema.sql 자동 실행)
docker compose down -v && docker compose up -d  # 스키마 변경 후 재생성
```
- 컨테이너: `insurance_db` / 접속 정보: `admin:1234@localhost:3306/insurance_db`
- `schema.sql`은 볼륨 첫 초기화 시에만 실행됨 — 컬럼 추가 후엔 반드시 재생성

## 아키텍처

**데이터 흐름**
```
Main → Role 메뉴 → Runner (UC 진행자) → Domain (비즈니스 로직) → DAO → DBA → MySQL
```

**패키지 구조**
- `dp/actor/` — 시스템 사용자 역할 클래스 (EducationTrainer, SalesManager 등)
- `dp/runner/usecase/` — 유스케이스별 Runner (38개). 각 Runner는 UC 시나리오 절차(Basic Path / Alternative / Exception)를 그대로 따른다
- `dp/{domain}/` — 도메인 클래스 (claim, consultation, contract, education, sales 등)
- `dp/dao/` — DAO (44개). 테이블당 1개가 원칙이나 보조 데이터는 부모 DAO에서 처리
- `dp/db/DBA.java` — HikariCP 커넥션 풀 + SQL 실행 헬퍼
- `dp/enums/` — 상태/유형 열거형 33종
- `dp/runner/ConsoleHelper.java` — 콘솔 입출력 전담

**Runner 패턴**
- `static void run()` 메서드 하나로 구성
- 코드 내 주석이 UC 단계 번호(1, 2, … / A1, E1 …)와 1:1 대응
- `ConsoleHelper`로 입력을 받고, Domain 메서드로 비즈니스 로직을 처리한 뒤, DAO로 저장

**DAO 패턴**
- `save()` — `INSERT ... ON DUPLICATE KEY UPDATE` upsert
- `findAll()` / `findBy~()` — `DBA.executeQuery(sql, rs -> { ... })` 람다 매핑
- PK는 도메인 클래스의 `static int sequence`로 생성
- **DAO 생성 기준**: 어떤 유스케이스가 저장한 상태를 다른 유스케이스가 나중에 읽어야 할 때만 독립 DAO를 만든다

**DBA 사용법**
```java
DBA.executeUpdate("INSERT INTO ...", param1, param2, ...);
DBA.executeQuery("SELECT ...", rs -> new MyObject(rs.getString("col")));
DBA.queryOne("SELECT ...", rs -> ...);  // 단건 조회
```
파라미터 타입으로 `String`, `Integer`, `Long`, `LocalDate`, `LocalDateTime`, `Boolean` 등을 직접 전달할 수 있다.

**ConsoleHelper 주요 메서드**
- 입력: `readLine`, `readNonEmpty`, `readInt`, `readLong`, `readPositiveInt`, `readYesNo`, `readDate`, `readDateTime`, `readMenuChoice(title, ...options)`, `readMultiChoice`
- 출력: `printStage(actor, msg)`, `printInfo`, `printSuccess`, `printError`, `printWarning`, `printDivider`, `waitEnter`

## 전체 파일 구성

### 도메인별 클래스 ↔ DAO ↔ 테이블 매핑

| 도메인 패키지 | 주요 도메인 클래스 | DAO | DB 테이블 |
|---|---|---|---|
| `dp/education` | EducationPlan | EducationPlanDAO | education_plans |
| | EducationPreparation | EducationPreparationDAO | education_preparations |
| | EducationExecution | EducationExecutionDAO | education_executions, education_attendances |
| `dp/sales` | ChannelRecruitment | ChannelRecruitmentDAO | channel_recruitments |
| | ChannelScreening | ChannelScreeningDAO | channel_screenings |
| | ActivityPlan, ScheduleItem | ActivityPlanDAO | activity_plans, activity_schedule_items |
| | SalesActivityManagement | SalesActivityManagementDAO | sales_activity_managements |
| | SalesOrgEvaluation | SalesOrgEvaluationDAO | sales_org_evaluations |
| | BonusRequest | BonusRequestDAO | bonus_requests |
| | CustomerRegistration | CustomerRegistrationDAO | customer_registrations |
| `dp/consultation` | ConsultationRequest | ConsultationRequestDAO | consultation_requests |
| | InterviewSchedule | InterviewScheduleDAO | interview_schedules |
| | InterviewRecord | InterviewRecordDAO | interview_records |
| | Proposal | ProposalDAO | proposals |
| | Underwriting | UnderwritingDAO | underwritings |
| | InsuranceApplication | InsuranceApplicationDAO | insurance_applications |
| | PolicyApplication | PolicyApplicationDAO | policy_applications |
| | Revival | RevivalDAO | revivals |
| `dp/contract` | Contract | ContractDAO | contracts |
| | Cancellation | CancellationDAO | cancellations |
| | ContractStatistics | ContractStatisticsDAO | contract_statistics |
| | ExpiringContractManagement | ExpiringContractManagementDAO | expiring_contract_notices |
| `dp/claim` | AccidentReport | AccidentReportDAO | accident_reports |
| | Dispatch | DispatchDAO | dispatches |
| | DispatchRecord | DispatchRecordDAO | dispatch_records |
| | ClaimRequest | ClaimRequestDAO | claim_requests |
| | DamageInvestigation | DamageInvestigationDAO | damage_investigations |
| | ClaimCalculation | ClaimCalculationDAO | claim_calculations |
| | ClaimPayment | ClaimPaymentDAO | claim_payments |
| `dp/payment` | Payment, PaymentItem | PaymentDAO | payments, payment_items |
| | PaymentRecord | PaymentRecordDAO | payment_records |
| | RefundCalculation | RefundCalculationDAO | refund_calculations |
| | RefundPayment | RefundPaymentDAO | refund_payments |
| | OverdueNoticeSetting | OverdueNoticeSettingDAO | overdue_notice_settings |
| `dp/inquiry` | Inquiry | InquiryDAO | inquiries |
| `dp/actor` | Customer | CustomerDAO | customers |
| | Designer | DesignerDAO | designers |
| | Agency | AgencyDAO | agencies |
| | EducationTrainer | EducationTrainerDAO | education_trainers |
| | SalesManager | SalesManagerDAO | sales_managers |
| | InsuranceReviewer | InsuranceReviewerDAO | insurance_reviewers |
| | ClaimsHandler | ClaimsHandlerDAO | claims_handlers |
| | DispatchAgent | DispatchAgentDAO | dispatch_agents |
| | FinanceManager | FinanceManagerDAO | finance_managers |

### Runner 목록 (36개)

| Runner | 유스케이스 | 호출 역할 |
|---|---|---|
| EducationPlanRunner | 교육 계획안 작성 | 영업교육담당자 |
| EducationPreparationRunner | 교육 제반 등록 | 영업교육담당자 |
| EducationExecutionRunner | 교육 진행 | 영업교육담당자 |
| ChannelRecruitmentRunner | 판매채널 모집 | 영업관리자 |
| ChannelScreeningRunner | 판매채널 채용 심사 | 영업관리자 |
| ActivityPlanRunner | 활동 계획 작성 | 판매채널 |
| SalesActivityRunner | 영업 활동 관리 | 영업관리자 |
| SalesOrgEvaluationRunner | 영업조직 평가 | 영업관리자 |
| BonusRequestRunner | 성과급 지급 요청 | 영업관리자 |
| CustomerRegistrationRunner | 고객 정보 등록 | 판매채널 |
| ConsultationRequestRunner | 상담 요청 | 고객 |
| InterviewScheduleRunner | 면담일정 관리 | 설계사 |
| InterviewRecordRunner | 면담기록 관리 | 설계사 |
| ProposalRunner | 보험상품 제안 | 설계사 |
| UnderwritingRunner | 인수 심사 | 보험심사자 |
| InsuranceApplicationRunner | 보험 가입 신청 | 고객 |
| PolicyApplicationRunner | 청약서 작성 | 설계사 |
| RevivalRunner | 보험 부활 | 설계사 |
| ContractInfoRunner | 계약 정보 조회 | 계약관리자 |
| ContractStatisticsRunner | 계약 통계 관리 | 계약관리자 |
| ExpiringContractManagementRunner | 만기 계약 관리 | 계약관리자 |
| InsuranceCancellationRunner | 보험 해지 | 계약관리자 |
| AccidentReportRunner | 사고 접수 | 고객 |
| DispatchRecordRunner | 현장 출동 기록 | 출동요원 |
| ClaimRequestRunner | 보험금 요청 | 고객 |
| DamageInvestigationRunner | 손해 조사 | 보상담당자 |
| ClaimCalculationRunner | 보험금 산출 | 보상담당자 |
| ClaimPaymentRunner | 보험금 지급 | 보상담당자 |
| PaymentRunner | 보험료 납입 | 고객 |
| PaymentRecordRunner | 납부 내역 관리 | 재무회계담당자 |
| RefundCalculationRunner | 해약 환급금 산출 | 재무회계담당자 |
| RefundListRunner | 해약 환급 내역 조회 | 재무회계담당자 |
| RefundPaymentRunner | 해약 환급금 지급 | 재무회계담당자 |
| InquiryRunner | 문의 | 고객 |
| InsuranceProductInquiryRunner | 보험상품 조회 | 고객 |
| MyInsuranceViewRunner | 내 보험 조회 | 고객 |

### SequenceSync 메커니즘

JVM 재시작 시 도메인 클래스의 `private static int sequence`가 0으로 리셋되어 기존 DB PK와 충돌하는 문제를 방지한다.

`SequenceSync.sync()`는 `Main.main()` 초기에 호출되며, 리플렉션으로 각 도메인 클래스의 `sequence` 필드에 `SELECT MAX(pk)` 결과를 주입한다. PK 형식이 숫자가 아닌 경우(예: `CS-홍길동-2024-01-01`) 정규식으로 숫자만 추출하여 파싱한다.

- **Employee 서브클래스** (`ClaimsHandler`, `FinanceManager` 등)는 `Employee.sequence`를 공유하므로 여러 테이블의 MAX를 UNION ALL로 한꺼번에 조회한다.
- **SequenceSync가 등록하지 않은 DAO** (Sales 계열 등): 별도 PK 동기화가 없어 JVM 재시작 시 충돌 가능성이 있다.

### 설계 제약 및 알려진 한계

| 항목 | 현황 | 이유 |
|---|---|---|
| **FK 제약** | schema.sql에 FOREIGN KEY 선언 없음 | 앱 코드가 FK 컬럼에 NULL을 삽입하는 경우가 있어 의도적으로 제외 |
| **트랜잭션** | DBA가 호출마다 별도 Connection 사용 | 단일 `save()` 내에서 여러 테이블에 INSERT해도 원자성 보장 안 됨 |
| **static sequence PK** | 모든 도메인이 JVM 내 카운터로 PK 생성 | SequenceSync로 재시작 후 MAX 동기화하지만, 동시 실행 환경에서는 충돌 가능 |
| **테스트 컴파일 에러** | `ActivityPlanTest.java:123` — ScheduleItem 생성자 시그니처 불일치 | 테스트 코드 미수정 상태; `./gradlew compileJava`(main)는 정상, `./gradlew build`(test 포함)는 실패 |

---

## 버그 수정 규칙

1. **수정 전 BUG ID를 먼저 언급한다** — Edit/Write 호출 직전에 `BUG-XXX-NN: 무엇을 왜 고치는지` 한 줄 이상 명시
2. **하나씩 수정하고 컴파일 확인** — 버그 하나 수정 → `./gradlew compileJava` 통과 → 다음 버그
3. **레포트 범위만 수정한다** — 버그 레포트에 없는 리팩토링이나 추가 기능은 넣지 않는다

## 스키마 변경 이력

| 변경일 | 테이블 | 변경 내용 | 관련 BUG |
|---|---|---|---|
| 2026-05-28 | `interview_records` | `interviewed_at TIMESTAMP NULL` 컬럼 추가 | BUG-REC-04 |
| 2026-05-28 | `expiring_contract_notices` | 테이블 신규 생성 (schema.sql에 누락돼 있었음) | BUG-NEW-CTR-01 |
| 2026-05-29 | `payment_records` | `confirmed_at`, `rejected_at`, `reject_category`, `reject_reason` 컬럼 추가 | BUG-R2-PAY-01 |
| 2026-05-29 | `education_preparations` | `registered_at TIMESTAMP NULL` 컬럼 추가 | BUG-EXTRA-EDU-04 |
| 2026-05-29 | `overdue_notice_settings` | `saved_at TIMESTAMP NULL` 컬럼 추가 | BUG-R8-ONS-01 |
| 2026-05-29 | `policy_applications` | `uploaded_at TIMESTAMP NULL` 컬럼 추가 | BUG-R8-PA-01 |
| 2026-05-29 | 전체 (23개 테이블) | FOREIGN KEY 제약 추가 (NULLABLE, RESTRICT) | StructureDesign.md |

> **주의**: 스키마 변경 후엔 반드시 `docker compose down -v && docker compose up -d` 실행

---

## AdditionalBugReport.md 현황 (2026-05-28 기준)

| BUG ID | 내용 요약 | 상태 |
|---|---|---|
| BUG-NEW-EDU-01 | EducationPreparationDAO — status NULL 저장, findAll()에서 미복원 | ✅ 수정 완료 |
| BUG-NEW-EDU-02 | EducationExecutionDAO — status NULL 저장, memo 컬럼·저장 누락 | ✅ 수정 완료 |
| BUG-NEW-EDU-03 | EducationExecutionRunner — 마지막 제반 자동 선택 | ✅ 수정 완료 |
| BUG-NEW-SAL-01 | ActivityPlanDAO.findAll() — plan_no 미복원(PK 덮어쓰기) | ✅ 수정 완료 |
| BUG-NEW-SAL-02 | ChannelScreeningDAO — rejection_reason 컬럼·저장·복원 누락 | ✅ 수정 완료 |
| BUG-NEW-CON-01 | ConsultationRequestDAO.findAll() — setAcceptedAt() 미호출 | ✅ 수정 완료 |
| BUG-NEW-CON-02 | UnderwritingDAO — risk_grade·review_opinion 컬럼·저장 누락 | ✅ 수정 완료 |
| BUG-NEW-CTR-01 | expiring_contract_notices 테이블이 schema.sql에 누락 (DAO는 생성됨) | ✅ 수정 완료 |
| BUG-NEW-CLM-01 | ClaimPaymentDAO.findAll() — 수령인·계좌·지급일시 등 미복원 | ✅ 수정 완료 |
| BUG-NEW-FIN-01 | PaymentDAO.findAll() — payment_no·방법·일시·금액 미복원 | ✅ 수정 완료 |
| BUG-NEW-FIN-02 | RefundCalculationRunner — c.getCustomer() NPE 위험 | ✅ 수정 완료 |
| BUG-NEW-INQ-01 | InquiryDAO — ON DUPLICATE KEY가 answer_content를 NULL로 덮어씀 | ✅ 수정 완료 |

## CodeReviewReport.md 현황 (2026-05-28 기준)

| BUG ID | 내용 요약 | 상태 |
|---|---|---|
| BUG-EDU-05 ~ BUG-EDU-12 | Education 도메인 다수 | ✅ 수정 완료 |
| BUG-SAL-07 ~ BUG-SAL-16 | Sales 도메인 다수 | ✅ 수정 완료 |
| BUG-CON-01 ~ BUG-CON-04 | ConsultationRequest 도메인 | ✅ 수정 완료 |
| BUG-SCH-01 ~ BUG-SCH-03 | InterviewSchedule 도메인 | ✅ 수정 완료 |
| BUG-REC-01 | InterviewRecordRunner — A3 수정 시 기록 선택 메뉴 없이 마지막 항목 자동 선택 | ✅ 수정 완료 |
| BUG-REC-02 | InterviewRecordRunner — 저장 완료 출력에서 interviewedAt을 저장일시로 오표시 | ✅ 수정 완료 |
| BUG-REC-03 | InterviewRecordRunner — 수정 완료 출력에 modifiedAt 미포함 | ✅ 수정 완료 |
| BUG-REC-04 | InterviewRecordDAO — interviewed_at/recorded_at 컬럼 혼용 저장 오류 | ✅ 수정 완료 (schema.sql에 interviewed_at 컬럼 추가 필요) |
| BUG-PRP-01 | ProposalDAO — findAll() 메서드 누락 | ✅ 수정 완료 |
| BUG-UDW-01 | ReviewResult — confirm() 호출 시 processingNo 미생성, Runner 출력에 처리번호 미포함 | ✅ 수정 완료 |
| BUG-REV-01 | RevivalRunner — pay() 반환값 무시, E2 납입 실패 분기 없음 | ✅ 수정 완료 |
| BUG-REV-02 | Revival — contact 필드 없음, Runner에서 setContact() 미호출 | ✅ 수정 완료 |
| BUG-NEW-CTR-02 | ExpiringContractManagementRunner — saveNoticeRecord()/saveRenewalContract() 후 DAO.save() 누락 | ✅ 수정 완료 |
| BUG-NEW-CLM-02 | ClaimPaymentRunner — OTP 재시도 횟수 3회(→5회로 수정) | ✅ 수정 완료 |
| BUG-NEW-CLM-04 | ClaimPaymentRunner — execute() 성공 후 완료 안내 팝업 누락 | ✅ 수정 완료 |
| BUG-NEW-CLM-05 | ClaimPaymentDAO — ON DUPLICATE KEY UPDATE에 scheduled_at·payment_type 누락 | ✅ 수정 완료 |
| BUG-INQ-05 | Inquiry — setInquiryNo() 없음, InquiryDAO.mapRow()에서 inquiry_no 미복원 | ✅ 수정 완료 |
| BUG-INQ-06 | InquiryDAO.save() — answer_content·answered_at INSERT 및 UPSERT에서 누락 | ✅ 수정 완료 |
| BUG-REF-01 | RefundPaymentRunner — execute() 성공 후 수령인명·은행명·계좌번호·이체금액 팝업 누락 | ✅ 수정 완료 |