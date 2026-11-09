-- V12: normalizacion (3FN), integridad y limpieza de redundancias

-- 1) Indice duplicado: la restriccion UNIQUE de email ya lo cubre
ALTER TABLE users DROP INDEX idx_users_email;

-- 2) 3FN en transacciones: la categoria determina el tipo
CREATE TABLE transaction_categories (
    code VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    PRIMARY KEY (code),
    UNIQUE KEY uq_category_type (code, type),
    CONSTRAINT chk_category_type CHECK (type IN ('EXPENSE', 'INCOME'))
);

INSERT INTO transaction_categories (code, type) VALUES
    ('FOOD', 'EXPENSE'), ('UTILITIES', 'EXPENSE'), ('RENT', 'EXPENSE'), ('TRANSPORT', 'EXPENSE'),
    ('HEALTH', 'EXPENSE'), ('EDUCATION', 'EXPENSE'), ('ENTERTAINMENT', 'EXPENSE'),
    ('HOME_MAINTENANCE', 'EXPENSE'), ('OTHER_EXPENSE', 'EXPENSE'),
    ('SALARY', 'INCOME'), ('EXTRA_INCOME', 'INCOME'), ('OTHER_INCOME', 'INCOME');

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_category FOREIGN KEY (category, type)
        REFERENCES transaction_categories (code, type);

-- 3) Un solo dueno por hogar (la columna generada vale 1 solo para OWNER; NULL no choca)
ALTER TABLE household_members
    ADD COLUMN owner_marker TINYINT GENERATED ALWAYS AS (CASE WHEN role = 'OWNER' THEN 1 ELSE NULL END) STORED,
    ADD UNIQUE KEY uq_one_owner_per_household (household_id, owner_marker);

-- 4) Valores validos en la propia base de datos
ALTER TABLE users
    ADD CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN')),
    ADD CONSTRAINT chk_user_provider CHECK (auth_provider IN ('LOCAL', 'GOOGLE'));
ALTER TABLE households
    ADD CONSTRAINT chk_household_type CHECK (type IN ('CASA', 'APARTAMENTO', 'ESTUDIO', 'OTRO')),
    ADD CONSTRAINT chk_household_residents CHECK (resident_count >= 1);
ALTER TABLE household_members
    ADD CONSTRAINT chk_member_role CHECK (role IN ('OWNER', 'MEMBER'));
ALTER TABLE invitations
    ADD CONSTRAINT chk_invitation_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED'));
ALTER TABLE tasks
    ADD CONSTRAINT chk_task_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    ADD CONSTRAINT chk_task_priority CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW'));
ALTER TABLE inventory_items
    ADD CONSTRAINT chk_inventory_category CHECK (category IN ('FOOD', 'CLEANING', 'MEDICINE', 'TOOLS', 'OTHER'));
ALTER TABLE shopping_items
    ADD CONSTRAINT chk_shopping_category CHECK (category IN ('FOOD', 'CLEANING', 'MEDICINE', 'TOOLS', 'OTHER'));