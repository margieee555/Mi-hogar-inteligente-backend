CREATE TABLE household_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    household_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_household FOREIGN KEY (household_id) REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_member_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_members_household ON household_members (household_id);

-- Los dueños actuales pasan a ser miembros OWNER de su hogar
INSERT INTO household_members (household_id, user_id, role)
SELECT id, owner_user_id, 'OWNER' FROM households;