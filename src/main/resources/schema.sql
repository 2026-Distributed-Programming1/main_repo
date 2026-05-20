-- ============================================================
-- DP Insurance System  —  DDL  (MySQL 8.0)
-- 생성 기준: dp/dao 패키지 전체 분석 (44 테이블)
-- 실행 방법: docker-compose up -d
--            또는  mysql -u admin -p1234 insurance_db < schema.sql
--
-- 테이블 생성 순서는 외래키 의존성 계층을 고려하였습니다.
--   Tier 1 : 독립 엔터티 (참조 없음)
--   Tier 2 : customers / education_plans / accident_reports 참조
--   Tier 3 : contracts / claim_requests / dispatches 참조
--   Tier 4 : cancellations / damage_investigations / education_preparations 참조
--   Tier 5 : refund_calculations / claim_calculations 참조
--
-- ※ 앱 코드에서 FK 컬럼에 NULL 삽입이 허용되므로
--    FOREIGN KEY CONSTRAINT 는 선언하지 않고 주석으로 관계를 표시합니다.
-- ============================================================

CREATE DATABASE IF NOT EXISTS insurance_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE insurance_db;

-- ============================================================
-- Tier 1-A : 고객
-- ============================================================

CREATE TABLE IF NOT EXISTS customers (
    customer_id   VARCHAR(20)   PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    resident_no   VARCHAR(20),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    address       VARCHAR(200),
    birth_date    DATE,
    registered_at TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tier 1-B : 임직원 계열
-- ============================================================

CREATE TABLE IF NOT EXISTS education_trainers (
    employee_id  VARCHAR(20)   PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sales_managers (
    manager_id   VARCHAR(50)   PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    department   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS insurance_reviewers (
    employee_id  VARCHAR(20)   PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS claims_handlers (
    employee_id    VARCHAR(20)  PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    department     VARCHAR(100),
    position       VARCHAR(50),
    transfer_limit BIGINT       DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dispatch_agents (
    employee_id  VARCHAR(20)   PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50),
    region       VARCHAR(100),
    vehicle_no   VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS finance_managers (
    employee_id  VARCHAR(20)   PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

-- ============================================================
-- Tier 1-C : 판매채널 (설계사 / 대리점)
-- ============================================================

CREATE TABLE IF NOT EXISTS designers (
    channel_id     VARCHAR(20)  PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    location       VARCHAR(200),
    license_number VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS agencies (
    channel_id    VARCHAR(20)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    location      VARCHAR(200),
    agency_number VARCHAR(50)
);

-- ============================================================
-- Tier 1-D : 상품 / 시스템 설정
-- ============================================================

-- product_name 을 PK 로 사용해야 ON DUPLICATE KEY UPDATE 가 동작함
-- (InsuranceProductDAO.save 의 ON DUPLICATE KEY UPDATE 기준이 product_name)
CREATE TABLE IF NOT EXISTS insurance_products (
    product_name      VARCHAR(100) PRIMARY KEY,
    category          VARCHAR(50),
    monthly_premium   BIGINT       DEFAULT 0,
    coverage_summary  TEXT,
    exclusion_summary TEXT
);

-- 시스템 단일 레코드 (id = 1 고정)
CREATE TABLE IF NOT EXISTS overdue_notice_settings (
    id                  INT      PRIMARY KEY DEFAULT 1,
    max_overdue_count   INT      DEFAULT 3,
    notice_method       VARCHAR(50),
    auto_cancel_enabled BOOLEAN  DEFAULT FALSE
);

-- ============================================================
-- Tier 2 : customers 참조
-- ============================================================

-- 계약 (contracts.customer_id → customers.customer_id)
CREATE TABLE IF NOT EXISTS contracts (
    contract_no      VARCHAR(20)  PRIMARY KEY,
    policy_no        VARCHAR(20),
    customer_id      VARCHAR(20),           -- → customers.customer_id
    customer_name    VARCHAR(100),
    contract_date    DATE,
    expiry_date      DATE,
    monthly_premium  BIGINT       DEFAULT 0,
    insurance_type   VARCHAR(50),
    status           VARCHAR(20)  DEFAULT 'NORMAL',
    is_expiring_soon BOOLEAN      DEFAULT FALSE,
    is_overdue       BOOLEAN      DEFAULT FALSE,
    overdue_count    INT          DEFAULT 0
);

-- 고객 정보 등록 이력 (판매채널이 등록한 원본 양식)
CREATE TABLE IF NOT EXISTS customer_registrations (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    customer_id     VARCHAR(20),            -- → customers.customer_id
    name            VARCHAR(100),
    ssn_masked      VARCHAR(20),
    phone           VARCHAR(20),
    insurance_type  VARCHAR(50),
    contract_date   DATE,
    expiry_date     DATE,
    monthly_premium BIGINT       DEFAULT 0,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 보험료 납부 요청
CREATE TABLE IF NOT EXISTS payments (
    payment_no     VARCHAR(20)  PRIMARY KEY,
    customer_id    VARCHAR(20),             -- → customers.customer_id
    customer_name  VARCHAR(100),
    total_amount   BIGINT       DEFAULT 0,
    payment_method VARCHAR(50),
    requested_at   TIMESTAMP,
    status         VARCHAR(20)
);

-- 사고 접수
CREATE TABLE IF NOT EXISTS accident_reports (
    accident_no   VARCHAR(20)  PRIMARY KEY,
    customer_id   VARCHAR(20),              -- → customers.customer_id
    customer_name VARCHAR(100),
    accident_type VARCHAR(50),
    reported_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(20)
);

-- 보험 청구 접수
CREATE TABLE IF NOT EXISTS claim_requests (
    claim_no       VARCHAR(20)  PRIMARY KEY,
    customer_id    VARCHAR(20),             -- → customers.customer_id
    customer_name  VARCHAR(100),
    contract_no    VARCHAR(20),             -- → contracts.contract_no
    claim_type     VARCHAR(20),
    diagnosis      VARCHAR(200),
    claim_reasons  VARCHAR(500),
    bank_name      VARCHAR(100),
    account_no     VARCHAR(50),
    account_holder VARCHAR(100),
    requested_at   TIMESTAMP,
    status         VARCHAR(20)
);

-- 보험 가입 신청 (InsuranceApplicationDAO)
CREATE TABLE IF NOT EXISTS insurance_applications (
    application_no  INT          PRIMARY KEY,
    customer_id     VARCHAR(20),            -- → customers.customer_id
    customer_name   VARCHAR(100),
    product_name    VARCHAR(100),
    monthly_premium BIGINT       DEFAULT 0,
    payment_method  VARCHAR(50),
    applied_at      TIMESTAMP
);

-- 청약 (PolicyApplicationDAO)
CREATE TABLE IF NOT EXISTS policy_applications (
    application_no INT          PRIMARY KEY,
    customer_id    VARCHAR(20),             -- → customers.customer_id
    customer_name  VARCHAR(100),
    product_name   VARCHAR(100),
    period         INT          DEFAULT 1,
    payment_method VARCHAR(50),
    submitted_at   TIMESTAMP
);

-- ============================================================
-- Tier 2 : 교육 도메인 (독립)
-- ============================================================

-- 교육 계획안
CREATE TABLE IF NOT EXISTS education_plans (
    plan_no         VARCHAR(20)  PRIMARY KEY,
    trainer_name    VARCHAR(100),
    title           VARCHAR(200),
    target_audience VARCHAR(200),
    scheduled_date  DATE,
    status          VARCHAR(20)
);

-- ============================================================
-- Tier 2 : 상담 도메인
-- ============================================================

-- 상담 요청 (channel: 방문/전화/온라인)
CREATE TABLE IF NOT EXISTS consultation_requests (
    consult_no   VARCHAR(20)  PRIMARY KEY,
    channel      VARCHAR(100),
    location     VARCHAR(200),
    contact      VARCHAR(100),
    content      TEXT,
    status       VARCHAR(20),
    requested_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 인터뷰 일정
CREATE TABLE IF NOT EXISTS interview_schedules (
    schedule_no   VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    type          VARCHAR(20),
    scheduled_at  TIMESTAMP,
    location      VARCHAR(200),
    status        VARCHAR(20)
);

-- 인터뷰 기록
CREATE TABLE IF NOT EXISTS interview_records (
    record_no     VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    content       TEXT,
    recorded_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 설계서 (제안서)
CREATE TABLE IF NOT EXISTS proposals (
    proposal_no     VARCHAR(20)  PRIMARY KEY,
    customer_name   VARCHAR(100),
    product_name    VARCHAR(100),
    monthly_premium BIGINT       DEFAULT 0,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 심사 (언더라이팅)
CREATE TABLE IF NOT EXISTS underwritings (
    underwriting_no VARCHAR(20)  PRIMARY KEY,
    app_type        VARCHAR(20),
    app_no          VARCHAR(20),
    customer_name   VARCHAR(100),
    result          VARCHAR(20),
    reviewed_at     TIMESTAMP
);

-- 부활
CREATE TABLE IF NOT EXISTS revivals (
    revival_no    VARCHAR(20)  PRIMARY KEY,
    contract_no   VARCHAR(20),              -- → contracts.contract_no
    customer_name VARCHAR(100),
    revived_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tier 2 : 영업 도메인
-- ============================================================

-- 채널 모집
CREATE TABLE IF NOT EXISTS channel_recruitments (
    recruitment_no VARCHAR(20)  PRIMARY KEY,
    manager_name   VARCHAR(100),
    channel_type   VARCHAR(50),
    recruit_count  INT          DEFAULT 0,
    start_date     DATE,
    end_date       DATE,
    condition_text VARCHAR(200),
    status         VARCHAR(20),
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 채널 심사
CREATE TABLE IF NOT EXISTS channel_screenings (
    screening_no   VARCHAR(20)  PRIMARY KEY,
    candidate_name VARCHAR(100),
    channel_type   VARCHAR(50),
    qualification  VARCHAR(200),
    status         VARCHAR(20),
    reviewed_at    TIMESTAMP
);

-- 활동 계획
CREATE TABLE IF NOT EXISTS activity_plans (
    plan_no                VARCHAR(20)  PRIMARY KEY,
    author_name            VARCHAR(100),
    activity_type          VARCHAR(50),
    scheduled_date         DATE,
    target                 VARCHAR(200),
    proposed_insurance_type VARCHAR(50),
    status                 VARCHAR(20)
);

-- 성과급 요청
CREATE TABLE IF NOT EXISTS bonus_requests (
    request_no VARCHAR(20)  PRIMARY KEY,
    requester  VARCHAR(100),
    amount     BIGINT       DEFAULT 0,
    reason     TEXT,
    status     VARCHAR(20),
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 영업 활동 관리
CREATE TABLE IF NOT EXISTS sales_activity_managements (
    activity_no   VARCHAR(20)  PRIMARY KEY,
    manager_name  VARCHAR(100),
    channel_name  VARCHAR(100),
    activity_type VARCHAR(50),
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 영업 조직 평가
CREATE TABLE IF NOT EXISTS sales_org_evaluations (
    evaluation_no VARCHAR(20)  PRIMARY KEY,
    org_name      VARCHAR(100),
    grade         VARCHAR(20),
    score         DOUBLE       DEFAULT 0,
    evaluated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tier 2 : 민원 도메인
-- ============================================================

CREATE TABLE IF NOT EXISTS inquiries (
    inquiry_no    VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    inquiry_type  VARCHAR(50),
    content       TEXT,
    status        VARCHAR(20),
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tier 3 : contracts 참조
-- ============================================================

-- 납부 기록
CREATE TABLE IF NOT EXISTS payment_records (
    record_no     VARCHAR(20)  PRIMARY KEY,
    contract_no   VARCHAR(20),              -- → contracts.contract_no
    customer_name VARCHAR(100),
    amount        BIGINT       DEFAULT 0,
    method        VARCHAR(50),
    payment_date  DATE,
    status        VARCHAR(20)
);

-- 해지
CREATE TABLE IF NOT EXISTS cancellations (
    cancellation_no VARCHAR(20)  PRIMARY KEY,
    contract_no     VARCHAR(20),            -- → contracts.contract_no
    customer_name   VARCHAR(100),
    monthly_premium BIGINT       DEFAULT 0,
    reason          VARCHAR(500),
    expected_refund BIGINT       DEFAULT 0,
    status          VARCHAR(20),
    cancelled_at    TIMESTAMP
);

-- 계약 통계 (스냅샷)
CREATE TABLE IF NOT EXISTS contract_statistics (
    stats_no        VARCHAR(20)  PRIMARY KEY,
    total_count     INT          DEFAULT 0,
    active_count    INT          DEFAULT 0,
    expired_count   INT          DEFAULT 0,
    cancelled_count INT          DEFAULT 0,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Tier 3 : accident_reports 참조
-- ============================================================

-- 현장 출동
CREATE TABLE IF NOT EXISTS dispatches (
    dispatch_no VARCHAR(20)  PRIMARY KEY,
    accident_no VARCHAR(20),                -- → accident_reports.accident_no
    status      VARCHAR(20)
);

-- ============================================================
-- Tier 3 : claim_requests 참조
-- ============================================================

-- 피해 조사
CREATE TABLE IF NOT EXISTS damage_investigations (
    investigation_no  VARCHAR(20)  PRIMARY KEY,
    claim_no          VARCHAR(20),          -- → claim_requests.claim_no
    claim_customer    VARCHAR(100),
    customer_id       VARCHAR(20),          -- → customers.customer_id
    handler_emp_id    VARCHAR(20),
    handler_name      VARCHAR(100),
    our_fault_ratio   DOUBLE       DEFAULT 0,
    counter_ratio     DOUBLE       DEFAULT 0,
    recognized_damage BIGINT       DEFAULT 0,
    opinion           TEXT,
    result            VARCHAR(20),
    reject_reason     TEXT,
    investigated_at   TIMESTAMP,
    status            VARCHAR(20)
);

-- ============================================================
-- Tier 3 : education_plans 참조
-- ============================================================

-- 교육 제반 준비
-- (plan_no 는 앱 코드에서 현재 null 로 삽입됨 — 향후 연결 가능)
CREATE TABLE IF NOT EXISTS education_preparations (
    prep_no        VARCHAR(20)  PRIMARY KEY,
    plan_no        VARCHAR(20),             -- → education_plans.plan_no
    trainer_name   VARCHAR(100),
    venue          VARCHAR(200),
    material_ready BOOLEAN      DEFAULT FALSE,
    status         VARCHAR(20)
);

-- ============================================================
-- Tier 4 : cancellations 참조
-- ============================================================

-- 환급금 계산
CREATE TABLE IF NOT EXISTS refund_calculations (
    refund_no          VARCHAR(20)  PRIMARY KEY,
    cancellation_no    VARCHAR(20),         -- → cancellations.cancellation_no
    total_paid_premium BIGINT       DEFAULT 0,
    payment_period     VARCHAR(50),
    reserve_amount     BIGINT       DEFAULT 0,
    applied_rate       DOUBLE       DEFAULT 0,
    base_refund        BIGINT       DEFAULT 0,
    unpaid_premium     BIGINT       DEFAULT 0,
    final_refund       BIGINT       DEFAULT 0,
    status             VARCHAR(20)
);

-- ============================================================
-- Tier 4 : damage_investigations 참조
-- ============================================================

-- 보상금 산출
CREATE TABLE IF NOT EXISTS claim_calculations (
    calculation_no      VARCHAR(20)  PRIMARY KEY,
    investigation_no    VARCHAR(20),        -- → damage_investigations.investigation_no
    recognized_damage   BIGINT       DEFAULT 0,
    fault_ratio         DOUBLE       DEFAULT 0,
    final_amount        BIGINT       DEFAULT 0,
    exceeded_deductible BOOLEAN      DEFAULT FALSE,
    adjusted            BOOLEAN      DEFAULT FALSE,
    status              VARCHAR(20)
);

-- ============================================================
-- Tier 4 : dispatches 참조
-- ============================================================

-- 출동 기록
CREATE TABLE IF NOT EXISTS dispatch_records (
    record_no   VARCHAR(20)  PRIMARY KEY,
    dispatch_no VARCHAR(20),                -- → dispatches.dispatch_no
    agent_name  VARCHAR(100),
    status      VARCHAR(20)
);

-- ============================================================
-- Tier 4 : education_preparations 참조
-- ============================================================

-- 교육 실시
-- (prep_no 는 앱 코드에서 현재 null 로 삽입됨 — 향후 연결 가능)
CREATE TABLE IF NOT EXISTS education_executions (
    execution_no   VARCHAR(20)  PRIMARY KEY,
    prep_no        VARCHAR(20),             -- → education_preparations.prep_no
    trainer_name   VARCHAR(100),
    executed_at    TIMESTAMP,
    attendee_count INT          DEFAULT 0,
    status         VARCHAR(20)
);

-- ============================================================
-- Tier 5 : refund_calculations 참조
-- ============================================================

-- 환급금 지급
CREATE TABLE IF NOT EXISTS refund_payments (
    payment_no      VARCHAR(20)  PRIMARY KEY,
    refund_no       VARCHAR(20),            -- → refund_calculations.refund_no
    cancellation_no VARCHAR(20),
    final_amount    BIGINT       DEFAULT 0,
    status          VARCHAR(20)
);

-- ============================================================
-- Tier 5 : claim_calculations 참조
-- ============================================================

-- 보험금 지급
CREATE TABLE IF NOT EXISTS claim_payments (
    payment_no     VARCHAR(20)  PRIMARY KEY,
    calculation_no VARCHAR(20),             -- → claim_calculations.calculation_no
    final_amount   BIGINT       DEFAULT 0,
    status         VARCHAR(20)
);