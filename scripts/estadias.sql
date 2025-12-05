INSERT INTO estadia (check_in, check_out, habitacion_id, huesped_titular_id, reserva_id) VALUES
-- 1. Estadía Familia (En curso). Habitación 3 (Family Plan). Titular: Garcia (ID 1).
('2025-12-01', '2025-12-08', 3, 1, NULL),

-- 2. Estadía Negocios (Finalizada). Habitación 41 (Individual). Titular: Smith (ID 11).
('2025-11-28', '2025-12-02', 41, 11, NULL),

-- 3. Estadía Pareja Extranjera (En curso). Habitación 14 (Doble Sup). Titular: Silva (ID 12).
('2025-12-03', '2025-12-07', 14, 12, NULL),

-- 4. Estadía Corta (Finaliza Hoy). Habitación 22 (Doble Std). Titular: Rodriguez (ID 4).
('2025-12-04', '2025-12-05', 22, 4, NULL),

-- 5. Estadía VIP (En curso). Habitación 1 (Suite). Titular: Fernandez (ID 5).
('2025-12-02', '2025-12-09', 1, 5, NULL),

-- 6. Estadía Histórica (Pasada). Habitación 25 (Doble Std). Titular: Figueroa (ID 20).
('2025-11-15', '2025-11-20', 25, 20, NULL),

-- 7. Estadía Familia (Recién llegada ayer). Habitación 4 (Family Plan). Titular: Gallardo (ID 23).
('2025-12-04', '2025-12-10', 4, 23, NULL),

-- 8. Estadía Larga Trabajo (En curso). Habitación 42 (Individual). Titular: Pereyra (ID 51).
('2025-12-01', '2025-12-15', 42, 51, NULL),

-- 9. Estadía Check-in HOY. Habitación 15 (Doble Sup). Titular: Leiva (ID 30).
('2025-12-05', '2025-12-07', 15, 30, NULL),

-- 10. Estadía Pareja (Finaliza hoy). Habitación 30 (Doble Std). Titular: Robledo (ID 60).
('2025-12-02', '2025-12-05', 30, 60, NULL);
