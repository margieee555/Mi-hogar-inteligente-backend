ALTER TABLE invitations ADD COLUMN expires_at TIMESTAMP NULL;

-- Las invitaciones que ya existían vencen 7 días después de su creación
UPDATE invitations SET expires_at = DATE_ADD(COALESCE(created_at, NOW()), INTERVAL 7 DAY);

CREATE INDEX idx_invitations_email ON invitations (email);