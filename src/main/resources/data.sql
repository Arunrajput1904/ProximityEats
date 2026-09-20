INSERT INTO user_service.role(type)
VALUES('ADMIN'),('USER'),('MANAGER'),('DELIVERY_BOY');
-- ==========================================
-- 1. INSERT SOCIETIES (25 Zones, 50 Societies)
-- ==========================================
INSERT INTO society_n (id, society_name, zone_name) VALUES
-- Nadaun Tehsil Pairs
(1, 'Nadaun Khas', 'Zone 1'),
(2, 'Jalari', 'Zone 1'),
(3, 'Bhumpal', 'Zone 2'),
(4, 'Dhaneta', 'Zone 2'),
(5, 'Rangas', 'Zone 3'),
(6, 'Bara', 'Zone 3'),
(7, 'Amlehar', 'Zone 4'),
(8, 'Ambi', 'Zone 4'),
(9, 'Saproh', 'Zone 5'),
(10, 'Kitpal', 'Zone 5'),
(11, 'Hathol', 'Zone 6'),
(12, 'Bela', 'Zone 6'),
(13, 'Tillu', 'Zone 7'),
(14, 'Sera', 'Zone 7'),
(15, 'Kashmir', 'Zone 8'),
(16, 'Pakhroo', 'Zone 8'),
(17, 'Batran', 'Zone 9'),
(18, 'Rail', 'Zone 9'),
(19, 'Karour', 'Zone 10'),
(20, 'Sanahi', 'Zone 10'),
(21, 'Jasai', 'Zone 11'),
(22, 'Kohla', 'Zone 11'),
(23, 'Fattehpur', 'Zone 12'),
(24, 'Churan', 'Zone 12'),
(25, 'Gagal', 'Zone 13'),

-- Hamirpur Tehsil Pairs
(26, 'Hamirpur Khas', 'Zone 13'),
(27, 'Anu Kalan', 'Zone 14'),
(28, 'Anu Khurd', 'Zone 14'),
(29, 'Bajuri', 'Zone 15'),
(30, 'Jhaniari', 'Zone 15'),
(31, 'Mattan Sidh', 'Zone 16'),
(32, 'Amroh', 'Zone 16'),
(33, 'Aghar', 'Zone 17'),
(34, 'Jangal Ropa', 'Zone 17'),
(35, 'Dugha', 'Zone 18'),
(36, 'Nalti', 'Zone 18'),
(37, 'Taal', 'Zone 19'),
(38, 'Swahlwa', 'Zone 19'),
(39, 'Dosarka', 'Zone 20'),
(40, 'Daruhi', 'Zone 20'),
(41, 'Lambloo', 'Zone 21'),
(42, 'Bohni', 'Zone 21'),
(43, 'Phahal', 'Zone 22'),
(44, 'Khagal', 'Zone 22'),
(45, 'Sason', 'Zone 23'),
(46, 'Brahmani', 'Zone 23'),
(47, 'Mohin', 'Zone 24'),
(48, 'Kakru', 'Zone 24'),
(49, 'Bhota', 'Zone 25'),
(50, 'Gauna', 'Zone 25');


-- ==========================================
-- 2. INSERT EDGES (Distances)
-- ==========================================
INSERT INTO society_edge (id, society1_id, society2_id, distance) VALUES
-- ---------------------------------------------------------
-- PART A: All 25 Intra-Zone Pairs (Strictly <= 5 km)
-- ---------------------------------------------------------
(1, 1, 2, 4),     -- Zone 1: Nadaun Khas <-> Jalari
(2, 3, 4, 4),     -- Zone 2: Bhumpal <-> Dhaneta
(3, 5, 6, 5),     -- Zone 3: Rangas <-> Bara
(4, 7, 8, 4),     -- Zone 4: Amlehar <-> Ambi
(5, 9, 10, 5),    -- Zone 5: Saproh <-> Kitpal
(6, 11, 12, 4),   -- Zone 6: Hathol <-> Bela
(7, 13, 14, 3),   -- Zone 7: Tillu <-> Sera
(8, 15, 16, 4),   -- Zone 8: Kashmir <-> Pakhroo
(9, 17, 18, 3),   -- Zone 9: Batran <-> Rail
(10, 19, 20, 4),  -- Zone 10: Karour <-> Sanahi
(11, 21, 22, 5),  -- Zone 11: Jasai <-> Kohla
(12, 23, 24, 4),  -- Zone 12: Fattehpur <-> Churan
(13, 25, 26, 3),  -- Zone 13: Gagal <-> Hamirpur Khas
(14, 27, 28, 3),  -- Zone 14: Anu Kalan <-> Anu Khurd
(15, 29, 30, 4),  -- Zone 15: Bajuri <-> Jhaniari
(16, 31, 32, 4),  -- Zone 16: Mattan Sidh <-> Amroh
(17, 33, 34, 4),  -- Zone 17: Aghar <-> Jangal Ropa
(18, 35, 36, 5),  -- Zone 18: Dugha <-> Nalti
(19, 37, 38, 3),  -- Zone 19: Taal <-> Swahlwa
(20, 39, 40, 5),  -- Zone 20: Dosarka <-> Daruhi
(21, 41, 42, 4),  -- Zone 21: Lambloo <-> Bohni (Corrected to <= 5km)
(22, 43, 44, 4),  -- Zone 22: Phahal <-> Khagal
(23, 45, 46, 5),  -- Zone 23: Sason <-> Brahmani
(24, 47, 48, 4),  -- Zone 24: Mohin <-> Kakru
(25, 49, 50, 3),  -- Zone 25: Bhota <-> Gauna

-- ---------------------------------------------------------
-- PART B: Cross-Zone Connections (Capped at 15 km)
-- ---------------------------------------------------------
(26, 5, 29, 15),  -- Rangas <-> Bajuri
(27, 3, 30, 15),  -- Bhumpal <-> Jhaniari
(28, 6, 35, 15),  -- Bara <-> Dugha
(29, 15, 42, 14), -- Kashmir <-> Bohni
(30, 2, 27, 15),  -- Jalari <-> Anu Kalan
(31, 1, 26, 15),  -- Nadaun Khas <-> Hamirpur Khas
(32, 5, 30, 15),  -- Rangas <-> Jhaniari
(33, 3, 29, 15),  -- Bhumpal <-> Bajuri
(34, 1, 3, 11),   -- Nadaun Khas <-> Bhumpal
(35, 1, 14, 7),   -- Nadaun Khas <-> Sera
(36, 2, 5, 9),    -- Jalari <-> Rangas
(37, 3, 15, 10),  -- Bhumpal <-> Kashmir
(38, 1, 4, 15),   -- Nadaun Khas <-> Dhaneta
(39, 4, 7, 7),    -- Dhaneta <-> Amlehar
(40, 2, 8, 6),    -- Jalari <-> Ambi
(41, 6, 9, 8),    -- Bara <-> Saproh
(42, 10, 11, 6),  -- Kitpal <-> Hathol
(43, 12, 13, 7),  -- Bela <-> Tillu
(44, 26, 27, 6),  -- Hamirpur Khas <-> Anu Kalan
(45, 26, 29, 7),  -- Hamirpur Khas <-> Bajuri
(46, 26, 31, 7),  -- Hamirpur Khas <-> Mattan Sidh
(47, 27, 35, 6),  -- Anu Kalan <-> Dugha
(48, 26, 39, 6),  -- Hamirpur Khas <-> Dosarka
(49, 31, 49, 14), -- Mattan Sidh <-> Bhota
(50, 26, 33, 11); -- Hamirpur Khas <-> Aghar