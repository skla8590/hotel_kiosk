-- 1. 관리자 정보 (admin)
INSERT INTO admin (admin_id, password, admin_name, admin_email, admin_phone, admin_grade)
VALUES ('admin', '$2a$10$p9NAgxpXX.xFki1xFNOR9OLQqAPFVhEVaQo3zAP0BauaezCWRGBci', '관리자', 'admin@test.com',
        '010-1234-5678', 'SUPER');

-- 2. 고객 정보 (members)
INSERT INTO members (member_name, member_phone, member_birth, reg_date, reservation_count, member_point)
VALUES
    ('홍길동', '01011112222', '1990-05-15', '2026-01-10', 3, 15000),
    ('김민지', '01022223333', '1995-08-22', '2026-02-14', 2, 8000),
    ('이준혁', '01033334444', '1988-03-30', '2026-03-05', 5, 30000),
    ('박서연', '01044445555', '2000-11-01', '2026-04-20', 1, 3000),
    ('최유진', '01055556666', '1992-07-18', '2026-05-09', 4, 22000),
    ('정하늘', '01066667777', '1985-12-25', '2026-06-01', 2, 9500),
    ('강민수', '01077778888', '1998-02-14', '2026-07-15', 1, 2000),
    ('윤소희', '01088889999', '1993-09-09', '2026-08-03', 3, 17500),
    ('임태양', '01099990000', '1987-04-04', '2026-09-12', 6, 45000),
    ('한지수', '01012345678', '1996-06-20', '2026-10-22', 2, 11000);

-- 3. 객실 정보 (room_master)
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

-- 4. 옵션 정보 (**** UI 때문에 더미 데이터 꼭 지켜주세요****)
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


-- 5. 요금 정책 (pricing_policy)
INSERT INTO pricing_policy (policy_name, repeat_type, repeat_value, start_date, end_date, discount_rate)
VALUES ('요일 정책', 'Weekly', '1,2,3,4,5,6,7', '2000-01-01', '2099-12-31', 0), -- 요일정책 기본값 0 설정
       ('주말 할증', 'Weekly', '6,7', '2026-01-01', '2026-12-31', 1.2),         -- 토/일 20% 할증
       ('봄 성수기', 'None', NULL, '2026-03-28', '2026-04-05', 1.15),           -- 봄 성수기 15% 할증
       ('비수기 할인', 'None', NULL, '2026-05-01', '2026-06-30', 0.9);           -- 비수기 10% 할인

-- 6. 재고 관리 (stocks)
INSERT INTO stocks (stock_name, stock_count, min_stock, stock_status)
VALUES
    ('수건 (대)', 150, 50, 'Clear'),
    ('수건 (소)', 200, 80, 'Clear'),
    ('샴푸', 300, 100, 'Clear'),
    ('바디워시', 280, 100, 'Clear'),
    ('치약', 250, 80, 'Clear'),
    ('칫솔', 240, 80, 'Clear'),
    ('일회용 슬리퍼', 180, 60, 'Clear'),
    ('생수 (500ml)', 45, 100, 'Shortage'),
    ('커피 믹스', 90, 100, 'Shortage'),
    ('미니바 스낵', 60, 80, 'Shortage');

-- 7. 공지사항 게시판 (inform_board)
INSERT INTO inform_board (title, content, writer, reg_date)
VALUES
    ('호텔 리모델링 안내', '2026년 3월 1일부터 3층 복도 리모델링 공사가 진행됩니다. 불편을 드려 죄송합니다.', '호텔 관리자', '2026-02-15'),
    ('수영장 운영 시간 변경 안내', '하절기(6~8월) 수영장 운영 시간이 오전 7시~오후 10시로 연장됩니다.', '호텔 관리자', '2026-05-20'),
    ('조식 뷔페 메뉴 업데이트', '6월부터 조식 뷔페에 새로운 메뉴가 추가됩니다. 많은 이용 부탁드립니다.', '호텔 관리자', '2026-05-28'),
    ('주차장 이용 안내', '지하 주차장 2구역이 임시 폐쇄됩니다. 지상 주차장을 이용해 주세요.', '호텔 관리자', '2026-06-01'),
    ('여름 패키지 이벤트 안내', '7~8월 성수기 특별 패키지를 출시합니다. 조식+석식+스파 이용권 포함 특가 제공.', '호텔 관리자', '2026-06-10');

-- 8. 실시간 객실 상태 (room_status)
INSERT INTO room_status (room_no, current_status)
VALUES
    (1101, 'Available'),
    (1102, 'Occupied'),
    (1103, 'Available'),
    (1104, 'Cleaning'),
    (1105, 'Occupied'),
    (1201, 'Available'),
    (1202, 'Maintenance'),
    (1203, 'Occupied'),
    (1204, 'Available'),
    (1205, 'Available'),
    (1301, 'Available'),
    (1302, 'Occupied'),
    (1303, 'Available'),
    (1304, 'Cleaning'),
    (1401, 'Available');

-- 9. 예약 정보
INSERT INTO reservations (room_no, member_no, status, checkin_date, checkout_date, check_in_at, check_out_at, reg_people, parking_num, add_option, pay_status, sms_status, created_at)
VALUES
-- idx=1  홍길동 / 1201호
(1201,  1, 'Out',       '2026-03-01', '2026-03-03', '2026-03-01 15:10:00', '2026-03-03 11:05:00', 2, '1234', 1, 'Success', 'Success', '2026-02-15 10:20:45'),
-- idx=2  김민지 / 1301호
(1301,  2, 'Out',       '2026-03-10', '2026-03-12', '2026-03-10 14:50:00', '2026-03-12 11:20:00', 2, NULL,   0, 'Success', 'Success', '2026-02-28 14:15:30'),
-- idx=3  이준혁 / 1101호
(1101,  3, 'Out',       '2026-04-05', '2026-04-07', '2026-04-05 15:30:00', '2026-04-07 10:55:00', 2, '5678', 1, 'Success', 'Success', '2026-03-12 11:05:12'),
-- idx=4  박서연 / 1204호
(1204,  4, 'Out',       '2026-04-20', '2026-04-22', '2026-04-20 16:00:00', '2026-04-22 11:00:00', 3, NULL,   0, 'Success', 'Success', '2026-04-02 09:30:00'),
-- idx=5  최유진 / 1105호
(1105,  5, 'In',        '2026-05-03', '2026-05-05', '2026-05-03 15:00:00', NULL,                  2, '9012',   1, 'Success', 'Success', '2026-04-15 16:40:22'),
-- idx=6  정하늘 / 1102호
(1102,  6, 'Reserved',  '2026-06-14', '2026-06-16', NULL,                  NULL,                  2, NULL,   0, 'Success', 'Success', '2026-05-01 13:10:05'),
-- idx=7  강민수 / 1401호
(1401,  7, 'Reserved',  '2026-06-13', '2026-06-15', NULL,                  NULL,                  2, NULL,   1, 'Success', 'Success', '2026-05-04 09:19:04'),
-- idx=8  윤소희 / 1203호
(1203,  8, 'Reserved',  '2026-06-14', '2026-06-17', NULL,                  NULL,                  2, NULL,   1, 'Success', 'Success', '2026-05-12 11:50:33'),
-- idx=9  임태양 / 1202호
(1202,  9, 'Reserved',  '2026-06-20', '2026-06-23', NULL,                  NULL,                  2, NULL,   1, 'Success', 'Success', '2026-05-20 14:00:00'),
-- idx=10 한지수 / 1302호
(1302, 10, 'Reserved',  '2026-06-25', '2026-06-28', NULL,                  NULL,                  4, NULL,   0, 'Waiting', 'Success', '2026-05-28 17:25:10'),
-- idx=11 이준혁 / 1304호
(1304,  3, 'Reserved',  '2026-07-01', '2026-07-03', NULL,                  NULL,                  2, NULL,   1, 'Success', 'Success', '2026-06-05 10:00:55'),
-- idx=12 최유진 / 1301호
(1301,  5, 'Cancelled', '2026-05-15', '2026-05-17', NULL,                  NULL,                  2, NULL,   0, 'Refund',  'Success', '2026-04-20 12:12:12'),
-- idx=13 홍길동 / 1101호
(1101,  1, 'Out',       '2026-02-10', '2026-02-12', '2026-02-10 15:00:00', '2026-02-12 11:00:00', 2, NULL,   0, 'Success', 'Success', '2026-01-20 15:30:00'),
-- idx=14 김민지 / 1104호
(1104,  2, 'Reserved',  '2026-07-05', '2026-07-07', NULL,                  NULL,                  2, NULL,   1, 'Success', 'Success', '2026-06-15 08:45:00'),
-- idx=15 임태양 / 1205호
(1205,  9, 'Out',       '2026-04-01', '2026-04-04', '2026-04-01 15:00:00', '2026-04-04 11:00:00', 3, NULL,   1, 'Success', 'Success', '2026-03-05 16:20:18');

-- 10. 예약 상세 옵션 (options_record)
INSERT INTO options_record (idx, option_id, quantity, option_charge)
VALUES
    ( 1, 1, 2,  50000),  -- idx=1  조식 뷔페 2인
    ( 1, 7, 1,  50000),  -- idx=1  스파 이용권
    ( 3, 1, 2,  50000),  -- idx=3  조식 뷔페 2인
    ( 3, 8, 2,  30000),  -- idx=3  어메니티 세트 2개
    ( 5, 2, 1,  12000),  -- idx=5  조식 뷔페 아동
    ( 5, 6, 1,  20000),  -- idx=5  수영 강습
    ( 7, 1, 2,  50000),  -- idx=7  조식 뷔페 2인
    ( 7, 3, 1,  50000),  -- idx=7  석식 (성인)
    ( 8, 3, 2,  70000),  -- idx=8  석식 뷔페 2인
    ( 8, 9, 1,  10000),  -- idx=8  유아 침대
    ( 9, 1, 2,  50000),  -- idx=9  조식 뷔페 2인
    ( 9, 7, 2, 100000),  -- idx=9  스파 이용권 2인
    (11, 1, 2,  50000),  -- idx=11 조식 뷔페 2인
    (11, 3, 1,  50000),  -- idx=11 석식 (성인)
    (15, 3, 2,  70000),  -- idx=15 석식 뷔페 2인
    (15, 8, 2,  30000);  -- idx=15 어메니티 세트 2개

-- 11. 결제 정보 기록 (payments)
INSERT INTO payments (idx, pay_method, approval_no, room_price, point_amount, option_charge, total_charge, sms_status, pay_status, status_at)
VALUES
    -- idx=1 (홍길동)
    ( 1, 'Card', 'CARD20260220001', 240000,      0, 100000, 340000, 'Success', 'Success', '2026-02-15 10:20:45'),
    -- idx=2 (김민지)
    ( 2, 'Card', 'CARD20260305001', 320000,  10000,      0, 310000, 'Success', 'Success', '2026-02-28 14:15:30'),
    -- idx=3 (이준혁)
    ( 3, 'Pay',  NULL,              160000,      0,  80000, 240000, 'Success', 'Success', '2026-03-12 11:05:12'),
    -- idx=4 (박서연)
    ( 4, 'Card', 'CARD20260410001', 460000,  15000,      0, 445000, 'Success', 'Success', '2026-04-02 09:30:00'),
    -- idx=5 (최유진)
    ( 5, 'Card', 'CARD20260425001', 250000,      0,  32000, 282000, 'Success', 'Success', '2026-04-15 16:40:22'),
    -- idx=6 (정하늘)
    ( 6, 'Card', 'CARD20260601001', 160000,      0,      0, 160000, 'Success', 'Success', '2026-05-01 13:10:05'),
    -- idx=7 (강민수)
    ( 7, 'Pay',  NULL,              500000,      0, 110000, 610000, 'Success', 'Success', '2026-05-04 09:19:04'),
    -- idx=8 (윤소희)
    ( 8, 'Card', 'CARD20260607001', 375000,   8000,  80000, 447000, 'Success', 'Success', '2026-05-12 11:50:33'),
    -- idx=9 (임태양)
    ( 9, 'Card', 'CARD20260610001', 360000,      0, 150000, 510000, 'Success', 'Success', '2026-05-20 14:00:00'),
    -- idx=10 (한지수)
    (10, 'Pay',  NULL,             1000000,  20000, 110000,1090000, 'Success', 'Success', '2026-05-28 17:25:10'),
    -- idx=11 (이준혁)
    (11, 'Card', 'CARD20260508001', 320000,      0,      0, 320000, 'Success', 'Refund',  '2026-06-05 10:00:55'),
    -- idx=12 (최유진 - 취소건)
    (12, 'Card', 'CARD20260203001', 160000,      0,      0, 160000, 'Success', 'Success', '2026-04-20 12:12:12'),
    -- idx=13 (홍길동)
    (13, 'Card', NULL,              230000,      0,  50000, 280000, 'Success', 'Success', '2026-01-20 15:30:00'),
    -- idx=14 (김민지)
    (14, 'Card', 'CARD20260320001', 840000,  30000, 100000, 910000, 'Success', 'Success', '2026-06-15 08:45:00');

-- 12. 고객 포인트 내역 (members_point)
INSERT INTO members_point (member_no, idx, earning, using_point)
VALUES
    (1,  1,  3400,     0),
    (2,  2,  3100, 10000),
    (3,  3,  2400,     0),
    (4,  4,  4450, 15000),
    (5,  5,  2820,     0),
    (6,  6,  1600,     0),
    (7,  7,  6100,     0),
    (8,  8,  4470,  8000),
    (9,  9,  5100,     0),
    (3, 11, 10900, 20000),
    (1, 13,  1600,     0),
    (9, 15,  9100, 30000);

-- 13. 요금 정책 적용한 객실가 (pricing_policy_room)
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
