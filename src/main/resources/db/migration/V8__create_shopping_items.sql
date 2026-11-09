CREATE TABLE shopping_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    household_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    bought BOOLEAN NOT NULL DEFAULT FALSE,
    bought_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shopping_household FOREIGN KEY (household_id) REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT chk_shopping_quantity CHECK (quantity >= 1)
);

CREATE INDEX idx_shopping_household_bought ON shopping_items (household_id, bought);