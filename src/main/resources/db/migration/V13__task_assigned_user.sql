-- V13: el responsable de una tarea pasa de texto libre a referencia a un usuario.
-- assigned_to queda sin uso; una migracion posterior (V14) lo elimina cuando se verifique.
ALTER TABLE tasks
    ADD COLUMN assigned_user_id BIGINT NULL,
    ADD CONSTRAINT fk_task_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users (id) ON DELETE SET NULL;

-- Relleno: empareja el texto con un miembro del mismo hogar por correo o por nombre
UPDATE tasks t
JOIN household_members hm ON hm.household_id = t.household_id
JOIN users u ON u.id = hm.user_id
SET t.assigned_user_id = u.id
WHERE t.assigned_to IS NOT NULL
  AND (LOWER(u.email) = LOWER(TRIM(t.assigned_to)) OR LOWER(u.name) = LOWER(TRIM(t.assigned_to)));