-- DP Insurance System Schema
-- MySQL 8.0

CREATE DATABASE IF NOT EXISTS insurance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_db;

-- ============================================================
-- Actors
-- ============================================================

CREATE TABLE IF NOT EXISTS customers (
    customer_id   VARCHAR(20)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    resident_no   VARCHAR(20),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    address       VARCHAR(200),
    birth_date    DATE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS claims_handlers (
    employee_id     VARCHAR(20)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    department      VARCHAR(100),
    position        VARCHAR(50),
    transfer_limit  BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dispatch_agents (
    employee_id  VARCHAR(20)  PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50),
    region       VARCHAR(100),
    vehicle_no   VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS finance_managers (
    employee_id  VARCHAR(20)  PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS education_trainers (
    employee_id  VARCHAR(20)  PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS insurance_reviewers (
    employee_id  VARCHAR(20)  PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    department   VARCHAR(100),
    position     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sales_managers (
    manager_id   VARCHAR(50)  PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    department   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS designers (
    channel_id     VARCHAR(20)  PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    location       VARCHAR(200),
    license_number VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS agencies (
    channel_id     VARCHAR(20)  PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    location       VARCHAR(200),
    agency_number  VARCHAR(50)
);

-- ============================================================
-- Insurance Products & Contracts
-- ============================================================

CREATE TABLE IF NOT EXISTS insurance_products (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name     VARCHAR(100) NOT NULL,
    category         VARCHAR(50),
    monthly_premium  BIGINT DEFAULT 0,
    coverage_summary TEXT,
    exclusion_summary TEXT
);

CREATE TABLE IF NOT EXISTS contracts (
    contract_no      VARCHAR(20)  PRIMARY KEY,
    policy_no        VARCHAR(20),
    customer_id      VARCHAR(20),
    customer_name    VARCHAR(100),
    contract_date    DATE,
    expiry_date      DATE,
    monthly_premium  BIGINT DEFAULT 0,
    insurance_type   VARCHAR(50),
    status           VARCHAR(20)  DEFAULT 'NORMAL',
    is_expiring_soon BOOLEAN      DEFAULT FALSE,
    is_overdue       BOOLEAN      DEFAULT FALSE,
    overdue_count    INT          DEFAULT 0
);

-- ============================================================
-- Accident & Claims Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS accident_reports (
    accident_no    VARCHAR(20)  PRIMARY KEY,
    customer_id    VARCHAR(20),
    customer_name  VARCHAR(100),
    accident_type  VARCHAR(50),
    accident_sub   VARCHAR(50),
    reported_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS dispatches (
    dispatch_no   VARCHAR(20)  PRIMARY KEY,
    accident_no   VARCHAR(20),
    status        VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS dispatch_records (
    record_no     VARCHAR(20)  PRIMARY KEY,
    dispatch_no   VARCHAR(20),
    agent_name    VARCHAR(100),
    arrival_time  TIMESTAMP,
    site_status   VARCHAR(500),
    action_taken  VARCHAR(1000),
    status        VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS claim_requests (
    claim_no       VARCHAR(20)  PRIMARY KEY,
    customer_id    VARCHAR(20),
    customer_name  VARCHAR(100),
    contract_no    VARCHAR(20),
    claim_type     VARCHAR(20),
    diagnosis      VARCHAR(200),
    claim_reasons  VARCHAR(500),
    bank_name      VARCHAR(100),
    account_no     VARCHAR(50),
    account_holder VARCHAR(100),
    requested_at   TIMESTAMP,
    status         VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS damage_investigations (
    investigation_no VARCHAR(20)  PRIMARY KEY,
    claim_no         VARCHAR(20),
    claim_customer   VARCHAR(100),
    handler_emp_id   VARCHAR(20),
    handler_name     VARCHAR(100),
    our_fault_ratio  DOUBLE       DEFAULT 0,
    counter_ratio    DOUBLE       DEFAULT 0,
    recognized_damage BIGINT      DEFAULT 0,
    opinion          TEXT,
    result           VARCHAR(20),
    reject_reason    TEXT,
    investigated_at  TIMESTAMP,
    status           VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS claim_calculations (
    calculation_no   VARCHAR(20)  PRIMARY KEY,
    investigation_no VARCHAR(20),
    recognized_damage BIGINT      DEFAULT 0,
    fault_ratio      DOUBLE       DEFAULT 0,
    final_amount     BIGINT       DEFAULT 0,
    exceeded_deductible BOOLEAN   DEFAULT FALSE,
    adjusted         BOOLEAN      DEFAULT FALSE,
    status           VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS claim_payments (
    payment_no      VARCHAR(20)  PRIMARY KEY,
    calculation_no  VARCHAR(20),
    final_amount    BIGINT       DEFAULT 0,
    status          VARCHAR(20)
);

-- ============================================================
-- Payment Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS payments (
    payment_no     VARCHAR(20)  PRIMARY KEY,
    customer_id    VARCHAR(20),
    customer_name  VARCHAR(100),
    total_amount   BIGINT       DEFAULT 0,
    payment_method VARCHAR(50),
    requested_at   TIMESTAMP,
    status         VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS payment_records (
    record_no      VARCHAR(20)  PRIMARY KEY,
    contract_no    VARCHAR(20),
    customer_name  VARCHAR(100),
    amount         BIGINT       DEFAULT 0,
    method         VARCHAR(50),
    payment_date   DATE,
    status         VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS overdue_notice_settings (
    id                   INT PRIMARY KEY DEFAULT 1,
    max_overdue_count    INT     DEFAULT 3,
    notice_method        VARCHAR(50),
    auto_cancel_enabled  BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS refund_calculations (
    refund_no          VARCHAR(20)  PRIMARY KEY,
    cancellation_no    VARCHAR(20),
    total_paid_premium BIGINT       DEFAULT 0,
    payment_period     VARCHAR(50),
    reserve_amount     BIGINT       DEFAULT 0,
    applied_rate       DOUBLE       DEFAULT 0,
    base_refund        BIGINT       DEFAULT 0,
    unpaid_premium     BIGINT       DEFAULT 0,
    final_refund       BIGINT       DEFAULT 0,
    status             VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS refund_payments (
    payment_no      VARCHAR(20)  PRIMARY KEY,
    refund_no       VARCHAR(20),
    cancellation_no VARCHAR(20),
    final_amount    BIGINT       DEFAULT 0,
    status          VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS cancellations (
    cancellation_no  VARCHAR(20)  PRIMARY KEY,
    contract_no      VARCHAR(20),
    customer_name    VARCHAR(100),
    monthly_premium  BIGINT       DEFAULT 0,
    reason           VARCHAR(500),
    expected_refund  BIGINT       DEFAULT 0,
    status           VARCHAR(20),
    cancelled_at     TIMESTAMP
);

-- ============================================================
-- Consultation & Application Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS consultation_requests (
    consult_no    VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    channel       VARCHAR(100),
    requested_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS interview_schedules (
    schedule_no   VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    scheduled_at  TIMESTAMP,
    location      VARCHAR(200),
    status        VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS interview_records (
    record_no     VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    content       TEXT,
    recorded_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS proposals (
    proposal_no     VARCHAR(20)  PRIMARY KEY,
    customer_name   VARCHAR(100),
    product_name    VARCHAR(100),
    monthly_premium BIGINT       DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS policy_applications (
    application_no  INT          PRIMARY KEY,
    customer_id     VARCHAR(20),
    customer_name   VARCHAR(100),
    product_name    VARCHAR(100),
    period          INT          DEFAULT 1,
    payment_method  VARCHAR(50),
    submitted_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS insurance_applications (
    application_no  INT          PRIMARY KEY,
    customer_id     VARCHAR(20),
    customer_name   VARCHAR(100),
    product_name    VARCHAR(100),
    monthly_premium BIGINT       DEFAULT 0,
    payment_method  VARCHAR(50),
    applied_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS revivals (
    revival_no    VARCHAR(20)  PRIMARY KEY,
    contract_no   VARCHAR(20),
    customer_name VARCHAR(100),
    revived_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS underwritings (
    underwriting_no  VARCHAR(20)  PRIMARY KEY,
    app_type         VARCHAR(20),
    app_no           VARCHAR(20),
    customer_name    VARCHAR(100),
    result           VARCHAR(20),
    reviewed_at      TIMESTAMP
);

-- ============================================================
-- Education Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS education_plans (
    plan_no         VARCHAR(20)  PRIMARY KEY,
    trainer_name    VARCHAR(100),
    title           VARCHAR(200),
    target_audience VARCHAR(200),
    scheduled_date  DATE,
    status          VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS education_preparations (
    prep_no         VARCHAR(20)  PRIMARY KEY,
    plan_no         VARCHAR(20),
    trainer_name    VARCHAR(100),
    venue           VARCHAR(200),
    material_ready  BOOLEAN      DEFAULT FALSE,
    status          VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS education_executions (
    execution_no    VARCHAR(20)  PRIMARY KEY,
    prep_no         VARCHAR(20),
    trainer_name    VARCHAR(100),
    executed_at     TIMESTAMP,
    attendee_count  INT          DEFAULT 0,
    status          VARCHAR(20)
);

-- ============================================================
-- Sales Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS channel_recruitments (
    recruitment_no VARCHAR(20)  PRIMARY KEY,
    manager_name   VARCHAR(100),
    candidate_name VARCHAR(100),
    channel_type   VARCHAR(50),
    status         VARCHAR(20),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS channel_screenings (
    screening_no   VARCHAR(20)  PRIMARY KEY,
    candidate_name VARCHAR(100),
    channel_type   VARCHAR(50),
    qualification  VARCHAR(200),
    status         VARCHAR(20),
    reviewed_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bonus_requests (
    request_no   VARCHAR(20)  PRIMARY KEY,
    requester    VARCHAR(100),
    amount       BIGINT       DEFAULT 0,
    reason       TEXT,
    status       VARCHAR(20),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS activity_plans (
    plan_no        VARCHAR(20)  PRIMARY KEY,
    author_name    VARCHAR(100),
    activity_type  VARCHAR(50),
    scheduled_date DATE,
    target         VARCHAR(200),
    status         VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS sales_activity_managements (
    activity_no   VARCHAR(20)  PRIMARY KEY,
    manager_name  VARCHAR(100),
    channel_name  VARCHAR(100),
    activity_type VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sales_org_evaluations (
    evaluation_no VARCHAR(20)  PRIMARY KEY,
    org_name      VARCHAR(100),
    grade         VARCHAR(20),
    score         DOUBLE       DEFAULT 0,
    evaluated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_registrations (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id      VARCHAR(20),
    name             VARCHAR(100),
    ssn_masked       VARCHAR(20),
    phone            VARCHAR(20),
    insurance_type   VARCHAR(50),
    contract_date    DATE,
    expiry_date      DATE,
    monthly_premium  BIGINT DEFAULT 0,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Inquiry Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS inquiries (
    inquiry_no    VARCHAR(20)  PRIMARY KEY,
    customer_name VARCHAR(100),
    inquiry_type  VARCHAR(50),
    title         VARCHAR(100),
    content       TEXT,
    status        VARCHAR(20),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Contract Management Domain
-- ============================================================

CREATE TABLE IF NOT EXISTS contract_statistics (
    stats_no      VARCHAR(20)  PRIMARY KEY,
    total_count   INT          DEFAULT 0,
    active_count  INT          DEFAULT 0,
    expired_count INT          DEFAULT 0,
    cancelled_count INT        DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);