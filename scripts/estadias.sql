INSERT INTO estadia (check_in, check_out, habitacion_id, huesped_titular_id) VALUES
-- 1. Estadía Familia. Habitación 3 (Family Plan). Titular: Garcia (ID 1).
-- Calc original: 16 al 23 Dic. Ajustado a hoy por límite.
('2025-12-13', '2025-12-23', 3, 1),

-- 2. Estadía Negocios. Habitación 41 (Individual). Titular: Smith (ID 11).
-- Calc original: 13 al 17 Dic. Entra justo hoy.
('2025-12-13', '2025-12-17', 41, 11),

-- 3. Estadía Pareja Extranjera. Habitación 14 (Doble Sup). Titular: Silva (ID 12).
-- Calc original: 18 al 22 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-22', 14, 12),

-- 4. Estadía Corta. Habitación 22 (Doble Std). Titular: Rodriguez (ID 4).
-- Calc original: 19 al 20 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-20', 22, 4),

-- 5. Estadía VIP. Habitación 1 (Suite). Titular: Fernandez (ID 5).
-- Calc original: 17 al 24 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-24', 1, 5),

-- 6. Estadía Histórica. Habitación 25 (Doble Std). Titular: Figueroa (ID 20).
-- Calc original: 30 Nov al 05 Dic. (Noviembre tiene 30 días). No aplica restricción.
('2025-11-30', '2025-12-05', 25, 20),

-- 7. Estadía Familia. Habitación 4 (Family Plan). Titular: Gallardo (ID 23).
-- Calc original: 19 al 25 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-25', 4, 23),

-- 8. Estadía Larga Trabajo. Habitación 42 (Individual). Titular: Pereyra (ID 51).
-- Calc original: 16 al 30 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-30', 42, 51),

-- 9. Estadía Check-in HOY. Habitación 15 (Doble Sup). Titular: Leiva (ID 30).
-- Calc original: 20 al 22 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-22', 15, 30),

-- 10. Estadía Pareja. Habitación 30 (Doble Std). Titular: Robledo (ID 60).
-- Calc original: 17 al 20 Dic. Ajustado a hoy.
('2025-12-13', '2025-12-20', 30, 60);