-- 1. 관리자 정보 (admin)
INSERT INTO admin (admin_id, password, admin_name, admin_email, admin_phone, admin_grade)
VALUES ('admin', '$2a$10$p9NAgxpXX.xFki1xFNOR9OLQqAPFVhEVaQo3zAP0BauaezCWRGBci', '관리자', 'admin@test.com',
        '010-1234-5678', 'SUPER');

-- 2. 고객 정보 (members)
INSERT INTO members (member_name, member_phone, member_birth, reg_date, reservation_count, member_point)
VALUES ('김민준', '01022222222', '1990-10-11', CURDATE(), 5, 500),
       ('박서연', '01033333333', '1992-02-02', CURDATE(), 4, 400),
       ('이지훈', '01044444444', '2002-03-04', CURDATE(), 1, 100),
       ('최예은', '01055555555', '2004-05-05', CURDATE(), 1, 100),
       ('정하은', '01066666666', '1997-06-07', CURDATE(), 2, 200),
       ('강준호', '01077777777', '1993-09-08', CURDATE(), 1, 100),
       ('윤지수', '01088888888', '1998-04-12', CURDATE(), 4, 400),
       ('오준석', '01099999999', '1999-09-25', CURDATE(), 1, 100),
       ('신하윤', '01000000000', '2003-06-03', CURDATE(), 3, 300),
       ('임서윤', '01011111111', '1994-01-01', CURDATE(), 2, 200);

-- 3. 객실 정보 (room_master)	(*** 추후 img_url 추가 필요 ***)
INSERT INTO room_master (room_no, room_name, room_type, room_view, room_floor, base_price, max_people, bed_type, area,
                         rating, room_status)
VALUES (1101, '스탠다드 시티', 'Standard', 'City', 11, 150000, 2, '더블 1', 26.0, 4.2, 'active'),
       (1102, '슈페리어 시티', 'Superior', 'City', 11, 170000, 2, '퀸 1', 30.0, 4.5, 'active'),
       (1103, '스탠다드 트윈 오션', 'Standard', 'Ocean', 11, 180000, 2, '싱글 2', 26.0, 4.1, 'active'),
       (1104, '슈페리어 마운틴', 'Superior', 'Mountain', 11, 180000, 2, '퀸 1', 30.0, 4.3, 'active'),
       (1105, '슈페리어 오션', 'Superior', 'Ocean', 11, 200000, 2, '퀸 1', 30.0, 4.5, 'active'),
       (1201, '슈페리어 레이크', 'Superior', 'Lake', 12, 180000, 2, '퀸 1', 30.0, 4.4, 'active'),
       (1202, '디럭스 시티', 'Deluxe', 'City', 12, 210000, 3, '킹 1', 38.0, 4.5, 'active'),
       (1203, '디럭스 마운틴', 'Deluxe', 'Mountain', 12, 215000, 3, '킹 1', 38.0, 4.6, 'active'),
       (1204, '디럭스 트윈 오션', 'Deluxe', 'Ocean', 12, 220000, 3, '퀸 1 + 싱글 1', 38.0, 4.7, 'active'),
       (1205, '디럭스 킹 오션', 'Deluxe', 'Ocean', 12, 240000, 3, '킹 1', 38.0, 4.8, 'active'),
       (1301, '스위트 패밀리 시티', 'Suite', 'City', 13, 290000, 4, '퀸 1 + 싱글 2', 52.0, 4.6, 'active'),
       (1302, '스위트 더블 마운틴', 'Suite', 'Mountain', 13, 300000, 4, '킹 2', 55.0, 4.7, 'active'),
       (1303, '스위트 더블 레이크', 'Suite', 'Lake', 13, 316000, 4, '킹 2', 55.0, 4.9, 'active'),
       (1304, '스위트 더블 오션', 'Suite', 'Ocean', 13, 330000, 4, '킹 2', 55.0, 4.8, 'active'),
       (1401, '로얄 스위트 오션', 'Suite', 'Ocean', 14, 450000, 5, '킹 2', 65.0, 5.0, 'active');

-- 3-1. 객실 이미지 정보
update room_master
set image_url = CASE room_view
                    WHEN 'City' THEN '/img/hotel_city.jpeg'
                    WHEN 'Ocean' THEN '/img/hotel_ocean.jpeg'
                    WHEN 'Mountain' THEN '/img/hotel_mountain.jpeg'
                    WHEN 'Lake' THEN '/img/hotel_lake.jpeg'
    END
WHERE room_view IN ('City', 'Ocean', 'Mountain', 'Lake');

-- 4. 객실 상태 (room_status)
INSERT INTO room_status (room_no, current_status)
VALUES (1101, 'Available'),
       (1102, 'Occupied'),          -- 테스트: 조회 결과에서 제외되어야 함
       (1103, 'Available'),
       (1104, 'Cleaning'),          -- 테스트: 조회 결과에서 제외되어야 함
       (1105, 'Available'),
       (1201, 'Available'),
       (1202, 'Available'),
       (1203, 'Occupied'),          -- 테스트: 조회 결과에서 제외되어야 함
       (1204, 'Available'),
       (1205, 'Cleaning Required'), -- 테스트: 조회 결과에서 제외되어야 함
       (1301, 'Available'),
       (1302, 'Available'),
       (1303, 'Maintenance'),       -- 테스트: 조회 결과에서 제외되어야 함
       (1304, 'Available'),
       (1401, 'Available');

-- 5. 예약 내역 (reservations)
INSERT INTO reservations (room_no, member_no, status, checkin_date, checkout_date, reg_people, add_option, pay_status)
VALUES (1101, 1, 'Out', '2026-03-30', '2026-04-01', 2, 0, 'Success'),
       (1102, 1, 'Cancelled', '2026-03-28', '2026-03-31', 2, 0, 'Refund'),
       (1103, 1, 'Out', '2026-03-31', '2026-04-02', 2, 0, 'Success'),
       (1104, 1, 'Out', '2026-03-29', '2026-04-02', 2, 0, 'Success'),
       (1105, 1, 'Out', '2026-03-28', '2026-03-30', 2, 0, 'Success'),
       (1201, 1, 'Out', '2026-04-01', '2026-04-03', 2, 0, 'Success'),
       (1202, 1, 'Cancelled', '2026-03-30', '2026-04-01', 3, 0, 'Refund'),
       (1301, 2, 'In', '2026-04-07', '2026-04-08', 4, 0, 'Success'),
       (1302, 2, 'Cancelled', '2026-04-07', '2026-04-10', 4, 0, 'Refund'),
       (1303, 2, 'In', '2026-04-07', '2026-04-09', 4, 0, 'Success'),
       (1304, 2, 'In', '2026-04-07', '2026-04-13', 4, 0, 'Success'),
       (1203, 2, 'In', '2026-04-07', '2026-04-16', 3, 0, 'Success'),
       (1204, 2, 'In', '2026-04-07', '2026-04-20', 3, 0, 'Waiting'),
       (1205, 2, 'Cancelled', '2026-04-07', '2026-04-15', 3, 0, 'Refund'),
       (1101, 3, 'Reserved', '2026-04-08', '2026-04-09', 2, 0, 'Success'),
       (1102, 3, 'Cancelled', '2026-04-10', '2026-04-15', 2, 0, 'Success'),
       (1103, 4, 'Reserved', '2026-04-20', '2026-04-27', 2, 0, 'Success'),
       (1104, 3, 'Reserved', '2026-04-30', '2026-05-05', 2, 0, 'Waiting'),
       (1103, 3, 'Reserved', '2026-04-29', '2026-04-30', 2, 0, 'Waiting'),
       (1101, 4, 'Cancelled', '2026-05-04', '2026-05-08', 2, 0, 'Waiting'),
       (1401, 5, 'Reserved', '2026-05-20', '2027-05-19', 5, 0, 'Failed');

-- 6. 요금 정책 (pricing_policy)
INSERT INTO pricing_policy (policy_name, repeat_type, repeat_value, start_date, end_date, discount_rate)
VALUES ('요일 정책', 'Weekly', '1,2,3,4,5,6,7', '2000-01-01', '2099-12-31', 0), -- 요일정책 기본값 0 설정
       ('주말 할증', 'Weekly', '6,7', '2026-01-01', '2026-12-31', 1.2),         -- 토/일 20% 할증
       ('봄 성수기', 'None', NULL, '2026-03-28', '2026-04-05', 1.15),           -- 봄 성수기 15% 할증
       ('비수기 할인', 'None', NULL, '2026-05-01', '2026-06-30', 0.9);

-- 비수기 10% 할인

-- 7. 요금 정책 적용한 객실가 (pricing_policy_room)
-- pricing_policy_room (policy_id 1 - 봄 성수기, 전 객실)
INSERT INTO pricing_policy_room (policy_id, room_no, room_price)
VALUES (1, 1101, 172500),
       (1, 1102, 195500),
       (1, 1103, 207000),
       (1, 1104, 207000),
       (1, 1105, 230000),
       (1, 1201, 207000),
       (1, 1202, 241500),
       (1, 1203, 247250),
       (1, 1204, 253000),
       (1, 1205, 276000),
       (1, 1301, 333500),
       (1, 1302, 345000),
       (1, 1303, 363400),
       (1, 1304, 379500),
       (1, 1401, 517500);

-- pricing_policy_room (policy_id 2 - 주말 할증, 전 객실)
INSERT INTO pricing_policy_room (policy_id, room_no, room_price)
VALUES (2, 1101, 180000),
       (2, 1102, 204000),
       (2, 1103, 216000),
       (2, 1104, 216000),
       (2, 1105, 240000),
       (2, 1201, 216000),
       (2, 1202, 252000),
       (2, 1203, 258000),
       (2, 1204, 264000),
       (2, 1205, 288000),
       (2, 1301, 348000),
       (2, 1302, 360000),
       (2, 1303, 379200),
       (2, 1304, 396000),
       (2, 1401, 540000);

-- pricing_policy_room (policy_id 3 - 비수기 할인, 전 객실)
INSERT INTO pricing_policy_room (policy_id, room_no, room_price)
VALUES (3, 1101, 135000),
       (3, 1102, 153000),
       (3, 1103, 162000),
       (3, 1104, 162000),
       (3, 1105, 180000),
       (3, 1201, 162000),
       (3, 1202, 189000),
       (3, 1203, 193500),
       (3, 1204, 198000),
       (3, 1205, 216000),
       (3, 1301, 261000),
       (3, 1302, 270000),
       (3, 1303, 284400),
       (3, 1304, 297000),
       (3, 1401, 405000);

-- 8. 옵션 정보 (**** UI 때문에 더미 데이터 꼭 지켜주세요****)
INSERT INTO option_master (option_name, option_category, option_target, option_price)
VALUES
-- 조식 관련
('조식 (성인)', 'Meal', 'Adult', 30000),
('조식 (소인)', 'Meal', 'Child', 18000),

-- 석식 관련
('석식 (성인)', 'Meal', 'Adult', 50000),
('석식 (소인)', 'Meal', 'Child', 30000),

-- 수영장 관련
('수영장 (성인)', 'Leisure', 'Adult', 40000),
('수영장 (소인)', 'Leisure', 'Child', 20000),

-- 헬스장 및 기타 서비스
('헬스장 (1일)', 'Leisure', 'Adult', 10000),
('침구류 추가', 'Consumable', 'Common', 33000),
('어메니티 추가', 'Consumable', 'Common', 5000);
