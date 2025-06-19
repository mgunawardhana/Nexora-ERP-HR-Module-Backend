CREATE TABLE employee_details
(
    id                        BIGSERIAL PRIMARY KEY,
    user_id                   INTEGER                  NOT NULL UNIQUE,
    employee_code             VARCHAR(255) UNIQUE,
    department                VARCHAR(255),
    designation               VARCHAR(255),
    join_date                 DATE,
    current_salary            NUMERIC(12, 2) CHECK (current_salary > 0),
    phone_number              VARCHAR(255),
    address                   TEXT,
    emergency_contact_name    VARCHAR(255),
    emergency_contact_phone   VARCHAR(255),
    date_of_birth             DATE,
    national_id               VARCHAR(255),
    bank_account_number       VARCHAR(255),
    bank_name                 VARCHAR(255),
    tax_id                    VARCHAR(255),
    manager_id                INTEGER,
    team_size                 INTEGER,
    specialization            VARCHAR(255),
    contract_start_date       DATE,
    contract_end_date         DATE,
    hourly_rate               NUMERIC(8, 2),
    certifications            TEXT,
    education_level           VARCHAR(255),
    university                VARCHAR(255),
    graduation_year           INTEGER,
    previous_experience_years INTEGER,
    employment_status         VARCHAR(50),
    probation_end_date        DATE,
    shift_timings             VARCHAR(255),
    access_level              VARCHAR(255),
    budget_authority          NUMERIC(12, 2),
    sales_target              NUMERIC(12, 2),
    commission_rate           NUMERIC(5, 2),
    intern_duration_months    INTEGER,
    mentor_id                 INTEGER,
    office_location           VARCHAR(255),
    work_mode                 VARCHAR(50),
    notes                     TEXT,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create an index for the foreign key to improve query performance
CREATE INDEX idx_employee_details_user_id ON employee_details (user_id);

-- Create a trigger to update the updated_at timestamp on row updates
CREATE
    OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at
        = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_updated_at
    BEFORE UPDATE
    ON employee_details
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- #####################################################################################################################

-- Create enum types for RegistrationStatus, RegistrationStep, ApprovalStatus, and PriorityLevel
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'registration_status') THEN
            CREATE TYPE registration_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'REJECTED');
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'registration_step') THEN
            CREATE TYPE registration_step AS ENUM ('STEP1', 'STEP2', 'STEP3'); -- Adjust based on actual enum values
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'approval_status') THEN
            CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'priority_level') THEN
            CREATE TYPE priority_level AS ENUM ('LOW', 'MEDIUM', 'HIGH');
        END IF;
    END
$$;

-- Create user_registrations table
CREATE TABLE user_registrations
(
    id                                 BIGSERIAL PRIMARY KEY,
    user_id                            INTEGER                  NOT NULL,
    registered_by                      INTEGER                  NOT NULL,
    registration_status                registration_status      NOT NULL,
    registration_step                  registration_step,
    approval_status                    approval_status,
    approved_by                        INTEGER,
    approval_date                      TIMESTAMP WITH TIME ZONE,
    rejection_reason                   TEXT,
    registration_notes                 TEXT,
    documents_verified                 BOOLEAN                  NOT NULL DEFAULT FALSE,
    background_check_completed         BOOLEAN                  NOT NULL DEFAULT FALSE,
    orientation_completed              BOOLEAN                  NOT NULL DEFAULT FALSE,
    system_access_provided             BOOLEAN                  NOT NULL DEFAULT FALSE,
    employee_handbook_provided         BOOLEAN                  NOT NULL DEFAULT FALSE,
    id_card_issued                     BOOLEAN                  NOT NULL DEFAULT FALSE,
    equipment_assigned                 BOOLEAN                  NOT NULL DEFAULT FALSE,
    probation_period_set               BOOLEAN                  NOT NULL DEFAULT FALSE,
    welcome_email_sent                 BOOLEAN                  NOT NULL DEFAULT FALSE,
    hr_interview_completed             BOOLEAN                  NOT NULL DEFAULT FALSE,
    department_head_approval           BOOLEAN                  NOT NULL DEFAULT FALSE,
    finance_approval                   BOOLEAN                  NOT NULL DEFAULT FALSE,
    it_setup_completed                 BOOLEAN                  NOT NULL DEFAULT FALSE,
    security_clearance_obtained        BOOLEAN                  NOT NULL DEFAULT FALSE,
    start_date_confirmed               TIMESTAMP WITH TIME ZONE,
    onboarding_completion_date         TIMESTAMP WITH TIME ZONE,
    registration_completion_percentage INTEGER                  NOT NULL DEFAULT 0 CHECK (registration_completion_percentage >=
                                                                                          0 AND
                                                                                          registration_completion_percentage <=
                                                                                          100),
    priority_level                     priority_level,
    deadline_date                      TIMESTAMP WITH TIME ZONE,
    created_at                         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_registered_by FOREIGN KEY (registered_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_approved_by FOREIGN KEY (approved_by) REFERENCES users (id) ON DELETE SET NULL
);

-- Create indexes for foreign keys
CREATE INDEX idx_user_registrations_user_id ON user_registrations (user_id);
CREATE INDEX idx_user_registrations_registered_by ON user_registrations (registered_by);
CREATE INDEX idx_user_registrations_approved_by ON user_registrations (approved_by);

-- Function to calculate registration_completion_percentage
CREATE OR REPLACE FUNCTION calculate_registration_completion_percentage()
    RETURNS TRIGGER AS
$$
DECLARE
    total_steps CONSTANT INTEGER := 14;
    completed_steps      INTEGER := 0;
BEGIN
    -- Count completed steps
    completed_steps :=
            (CASE WHEN NEW.documents_verified THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.background_check_completed THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.orientation_completed THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.system_access_provided THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.employee_handbook_provided THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.id_card_issued THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.equipment_assigned THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.probation_period_set THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.welcome_email_sent THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.hr_interview_completed THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.department_head_approval THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.finance_approval THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.it_setup_completed THEN 1 ELSE 0 END) +
            (CASE WHEN NEW.security_clearance_obtained THEN 1 ELSE 0 END);

    -- Calculate percentage
    NEW.registration_completion_percentage := (completed_steps * 100) / total_steps;

    -- Update registration_status and onboarding_completion_date
    IF NEW.registration_completion_percentage = 100 THEN
        NEW.registration_status := 'COMPLETED';
        IF NEW.onboarding_completion_date IS NULL THEN
            NEW.onboarding_completion_date := CURRENT_TIMESTAMP;
        END IF;
    ELSIF NEW.registration_completion_percentage > 0 THEN
        NEW.registration_status := 'IN_PROGRESS';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_updated_at
    BEFORE UPDATE
    ON user_registrations
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Triggers to calculate completion percentage
CREATE TRIGGER trigger_calculate_completion_insert
    BEFORE INSERT
    ON user_registrations
    FOR EACH ROW
EXECUTE FUNCTION calculate_registration_completion_percentage();

CREATE TRIGGER trigger_calculate_completion_update
    BEFORE UPDATE
    ON user_registrations
    FOR EACH ROW
    WHEN (
        OLD.documents_verified IS DISTINCT FROM NEW.documents_verified OR
        OLD.background_check_completed IS DISTINCT FROM NEW.background_check_completed OR
        OLD.orientation_completed IS DISTINCT FROM NEW.orientation_completed OR
        OLD.system_access_provided IS DISTINCT FROM NEW.system_access_provided OR
        OLD.employee_handbook_provided IS DISTINCT FROM NEW.employee_handbook_provided OR
        OLD.id_card_issued IS DISTINCT FROM NEW.id_card_issued OR
        OLD.equipment_assigned IS DISTINCT FROM NEW.equipment_assigned OR
        OLD.probation_period_set IS DISTINCT FROM NEW.probation_period_set OR
        OLD.welcome_email_sent IS DISTINCT FROM NEW.welcome_email_sent OR
        OLD.hr_interview_completed IS DISTINCT FROM NEW.hr_interview_completed OR
        OLD.department_head_approval IS DISTINCT FROM NEW.department_head_approval OR
        OLD.finance_approval IS DISTINCT FROM NEW.finance_approval OR
        OLD.it_setup_completed IS DISTINCT FROM NEW.it_setup_completed OR
        OLD.security_clearance_obtained IS DISTINCT FROM NEW.security_clearance_obtained
        )
EXECUTE FUNCTION calculate_registration_completion_percentage();