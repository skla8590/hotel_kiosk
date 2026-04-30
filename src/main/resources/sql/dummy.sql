USE `hotel_kiosk`;

-- 1. 관리자 데이터
INSERT INTO admin (admin_id, password, admin_name, admin_email, admin_phone, admin_grade, last_login_ip, statement)
VALUES
('superadmin', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWXYZ01234', '김슈퍼', 'super@hotel.com', '010-1111-1111', 'SUPER', '192.168.0.1', 'Working'),
('admin01', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWXYZ01234', '이관리', 'admin01@hotel.com', '010-2222-2222', 'GENERAL', '192.168.0.2', 'Working'),
('admin02', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWXYZ01234', '박총무', 'admin02@hotel.com', '010-3333-3333', 'GENERAL', '192.168.0.3', 'Working'),
('admin03', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWXYZ01234', '최프론트', 'admin03@hotel.com', '010-4444-4444', 'GENERAL', '192.168.0.4', 'Absence'),
('admin04', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWXYZ01234', '정매니저', 'admin04@hotel.com', '010-5555-5555', 'GENERAL', '192.168.0.5', 'Leave');

-- 2. 고객 데이터
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

-- 3. 객실 데이터
INSERT INTO room_master (room_no, room_name, room_type, room_view, room_floor, base_price, max_people, bed_type, area, rating, image_url, room_status)
VALUES
(101, '스탠다드 트윈 A', 'Standard', 'City', 1, 80000, 2, 'Twin', 25.5, 4.2, '/images/rooms/101.jpg', 'active'),
(102, '스탠다드 트윈 B', 'Standard', 'City', 1, 80000, 2, 'Twin', 25.5, 4.0, '/images/rooms/102.jpg', 'active'),
(201, '디럭스 더블 A', 'Deluxe', 'Ocean', 2, 120000, 2, 'Double', 35.0, 4.5, '/images/rooms/201.jpg', 'active'),
(202, '디럭스 더블 B', 'Deluxe', 'Garden', 2, 115000, 2, 'Double', 35.0, 4.3, '/images/rooms/202.jpg', 'active'),
(203, '디럭스 트윈', 'Deluxe', 'Ocean', 2, 125000, 3, 'Twin', 38.0, 4.6, '/images/rooms/203.jpg', 'active'),
(301, '슈페리어 킹', 'Superior', 'Ocean', 3, 160000, 2, 'King', 45.0, 4.7, '/images/rooms/301.jpg', 'active'),
(302, '슈페리어 패밀리', 'Superior', 'Garden', 3, 180000, 4, 'Twin', 55.0, 4.4, '/images/rooms/302.jpg', 'active'),
(401, '디럭스 스위트', 'Suite', 'Ocean', 4, 250000, 2, 'King', 70.0, 4.8, '/images/rooms/401.jpg', 'active'),
(402, '패밀리 스위트', 'Suite', 'City', 4, 280000, 5, 'Twin', 85.0, 4.6, '/images/rooms/402.jpg', 'active'),
(501, '프레지덴셜 스위트', 'Presidential', 'Ocean', 5, 500000, 4, 'King', 120.0, 5.0, '/images/rooms/501.jpg', 'active');

-- 4. 부가 서비스 옵션 데이터
INSERT INTO option_master (option_name, option_category, option_target, option_price)
VALUES
('조식 뷔페', 'Meal', 'Adult', 25000),
('조식 뷔페 (아동)', 'Meal', 'Child', 12000),
('석식 뷔페', 'Meal', 'Adult', 35000),
('석식 뷔페 (아동)', 'Meal', 'Child', 18000),
('카약 체험', 'Leisure', 'Adult', 40000),
('수영 강습', 'Leisure', 'Child', 20000),
('스파 이용권', 'Leisure', 'Adult', 50000),
('어메니티 세트', 'Consumable', 'Adult', 15000),
('유아 침대', 'Consumable', 'Child', 10000),
('와인 패키지', 'Consumable', 'Adult', 60000);

-- 5. 요금 정책 데이터
INSERT INTO pricing_policy (policy_name, repeat_type, repeat_value, start_date, end_date, discount_rate)
VALUES
('성수기 요금', 'None', NULL, '2026-07-01', '2026-08-31', -0.2),
('비수기 할인', 'None', NULL, '2026-11-01', '2027-02-28', 0.15),
('주말 할증', 'Weekly', 'SAT,SUN', '2026-01-01', '2026-12-31', -0.1),
('명절 특별 요금', 'None', NULL, '2026-09-16', '2026-09-18', -0.25),
('얼리버드 할인', 'Monthly', '1', '2026-01-01', '2026-12-31', 0.1);

-- 6. 재고 데이터
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

-- 7. 공지사항 데이터
INSERT INTO inform_board (title, content, writer, reg_date)
VALUES
('호텔 리모델링 안내', '2026년 3월 1일부터 3층 복도 리모델링 공사가 진행됩니다. 불편을 드려 죄송합니다.', '호텔 관리자', '2026-02-15'),
('수영장 운영 시간 변경 안내', '하절기(6~8월) 수영장 운영 시간이 오전 7시~오후 10시로 연장됩니다.', '호텔 관리자', '2026-05-20'),
('조식 뷔페 메뉴 업데이트', '6월부터 조식 뷔페에 새로운 메뉴가 추가됩니다. 많은 이용 부탁드립니다.', '호텔 관리자', '2026-05-28'),
('주차장 이용 안내', '지하 주차장 2구역이 임시 폐쇄됩니다. 지상 주차장을 이용해 주세요.', '호텔 관리자', '2026-06-01'),
('여름 패키지 이벤트 안내', '7~8월 성수기 특별 패키지를 출시합니다. 조식+석식+스파 이용권 포함 특가 제공.', '호텔 관리자', '2026-06-10');

-- 8. 실시간 객실 상태 데이터
INSERT INTO room_status (room_no, current_status)
VALUES
(101, 'Available'),
(102, 'Occupied'),
(201, 'Available'),
(202, 'Cleaning'),
(203, 'Occupied'),
(301, 'Available'),
(302, 'Maintenance'),
(401, 'Occupied'),
(402, 'Available'),
(501, 'Cleaning Required');

-- 9. 예약 데이터
INSERT INTO reservations (room_no, member_no, status, checkin_date, checkout_date, check_in_at, check_out_at, reg_people, parking_num, add_option, pay_status, sms_status)
VALUES
-- idx=1  (구 260220-184520) 홍길동 / 201호
(201,  1, 'Out',       '2026-03-01', '2026-03-03', '2026-03-01 15:10:00', '2026-03-03 11:05:00', 2, '1234', 1, 'Success', 'Success'),
-- idx=2  (구 260305-073841) 김민지 / 301호
(301,  2, 'Out',       '2026-03-10', '2026-03-12', '2026-03-10 14:50:00', '2026-03-12 11:20:00', 2, NULL,   0, 'Success', 'Success'),
-- idx=3  (구 260328-392017) 이준혁 / 101호
(101,  3, 'Out',       '2026-04-05', '2026-04-07', '2026-04-05 15:30:00', '2026-04-07 10:55:00', 2, '5678', 1, 'Success', 'Success'),
-- idx=4  (구 260410-654308) 박서연 / 402호
(402,  4, 'Out',       '2026-04-20', '2026-04-22', '2026-04-20 16:00:00', '2026-04-22 11:00:00', 3, NULL,   0, 'Success', 'Success'),
-- idx=5  (구 260425-219476) 최유진 / 203호
(203,  5, 'Out',       '2026-05-03', '2026-05-05', '2026-05-03 15:00:00', '2026-05-05 10:30:00', 2, '9012', 1, 'Success', 'Success'),
-- idx=6  (구 260601-408253) 정하늘 / 102호
(102,  6, 'In',        '2026-06-14', '2026-06-16', '2026-06-14 15:20:00', NULL,                  2, NULL,   0, 'Success', 'Success'),
-- idx=7  (구 260530-763094) 강민수 / 401호
(401,  7, 'In',        '2026-06-13', '2026-06-15', '2026-06-13 14:40:00', NULL,                  2, '3456', 1, 'Success', 'Success'),
-- idx=8  (구 260607-531870) 윤소희 / 203호
(203,  8, 'In',        '2026-06-14', '2026-06-17', '2026-06-14 15:50:00', NULL,                  2, NULL,   1, 'Success', 'Success'),
-- idx=9  (구 260610-847162) 임태양 / 201호
(201,  9, 'Reserved',  '2026-06-20', '2026-06-23', NULL,                  NULL,                  2, '7890', 1, 'Success', 'Success'),
-- idx=10 (구 260615-093485) 한지수 / 302호
(302, 10, 'Reserved',  '2026-06-25', '2026-06-28', NULL,                  NULL,                  4, NULL,   0, 'Waiting', 'Success'),
-- idx=11 (구 260620-325609) 이준혁 / 501호
(501,  3, 'Reserved',  '2026-07-01', '2026-07-03', NULL,                  NULL,                  2, '1111', 1, 'Success', 'Success'),
-- idx=12 (구 260508-710243) 최유진 / 301호
(301,  5, 'Cancelled', '2026-05-15', '2026-05-17', NULL,                  NULL,                  2, NULL,   0, 'Refund',  'Success'),
-- idx=13 (구 260203-468031) 홍길동 / 101호
(101,  1, 'Out',       '2026-02-10', '2026-02-12', '2026-02-10 15:00:00', '2026-02-12 11:00:00', 2, NULL,   0, 'Success', 'Success'),
-- idx=14 (구 260625-982714) 김민지 / 202호
(202,  2, 'Reserved',  '2026-07-05', '2026-07-07', NULL,                  NULL,                  2, '2222', 1, 'Success', 'Success'),
-- idx=15 (구 260320-156928) 임태양 / 402호
(402,  9, 'Out',       '2026-04-01', '2026-04-04', '2026-04-01 15:00:00', '2026-04-04 11:00:00', 3, NULL,   1, 'Success', 'Success');

-- 10. 예약 옵션 기록 데이터
-- [수정] reservation_id → idx (reservations 테이블의 PK, BIGINT FK)
INSERT INTO options_record (idx, option_id, quantity, option_charge)
VALUES
( 1, 1, 2,  50000),  -- idx=1  조식 뷔페 2인
( 1, 7, 1,  50000),  -- idx=1  스파 이용권
( 3, 1, 2,  50000),  -- idx=3  조식 뷔페 2인
( 3, 8, 2,  30000),  -- idx=3  어메니티 세트 2개
( 5, 2, 1,  12000),  -- idx=5  조식 뷔페 아동
( 5, 6, 1,  20000),  -- idx=5  수영 강습
( 7, 1, 2,  50000),  -- idx=7  조식 뷔페 2인
( 7,10, 1,  60000),  -- idx=7  와인 패키지
( 8, 3, 2,  70000),  -- idx=8  석식 뷔페 2인
( 8, 9, 1,  10000),  -- idx=8  유아 침대
( 9, 1, 2,  50000),  -- idx=9  조식 뷔페 2인
( 9, 7, 2, 100000),  -- idx=9  스파 이용권 2인
(11, 1, 2,  50000),  -- idx=11 조식 뷔페 2인
(11,10, 1,  60000),  -- idx=11 와인 패키지
(15, 3, 2,  70000),  -- idx=15 석식 뷔페 2인
(15, 8, 2,  30000);  -- idx=15 어메니티 세트 2개

-- 11. 결제 데이터
-- [수정] reservation_id → idx (reservations 테이블의 PK, BIGINT FK)
INSERT INTO payments (idx, pay_method, approval_no, room_price, point_amount, option_charge, total_charge, sms_status, pay_status)
VALUES
( 1, 'Card', 'CARD20260220001', 240000,      0, 100000, 340000, 'Success', 'Success'),
( 2, 'Card', 'CARD20260305001', 320000,  10000,      0, 310000, 'Success', 'Success'),
( 3, 'Pay',  NULL,              160000,      0,  80000, 240000, 'Success', 'Success'),
( 4, 'Card', 'CARD20260410001', 460000,  15000,      0, 445000, 'Success', 'Success'),
( 5, 'Card', 'CARD20260425001', 250000,      0,  32000, 282000, 'Success', 'Success'),
( 6, 'Card', 'CARD20260601001', 160000,      0,      0, 160000, 'Success', 'Success'),
( 7, 'Pay',  NULL,              500000,      0, 110000, 610000, 'Success', 'Success'),
( 8, 'Card', 'CARD20260607001', 375000,   8000,  80000, 447000, 'Success', 'Success'),
( 9, 'Card', 'CARD20260610001', 360000,      0, 150000, 510000, 'Success', 'Success'),
(10, 'Card', NULL,              540000,      0,      0, 540000, 'Success', 'Waiting'),
(11, 'Pay',  NULL,             1000000,  20000, 110000,1090000, 'Success', 'Success'),
(12, 'Card', 'CARD20260508001', 320000,      0,      0, 320000, 'Success', 'Refund'),
(13, 'Card', 'CARD20260203001', 160000,      0,      0, 160000, 'Success', 'Success'),
(14, 'Card', NULL,              230000,      0,  50000, 280000, 'Success', 'Success'),
(15, 'Card', 'CARD20260320001', 840000,  30000, 100000, 910000, 'Success', 'Success');

-- 12. 포인트 내역 데이터
-- [수정] reservation_id → idx (reservations 테이블의 PK, BIGINT FK)
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

-- 13. 요금 정책별 객실 요금 데이터
INSERT INTO pricing_policy_room (policy_id, room_no, room_price)
VALUES
(1, 101, 96000),
(1, 102, 96000),
(1, 201, 144000),
(1, 202, 138000),
(1, 203, 150000),
(1, 301, 192000),
(1, 302, 216000),
(1, 401, 300000),
(1, 402, 336000),
(1, 501, 600000),
(2, 101, 68000),
(2, 102, 68000),
(2, 201, 102000),
(2, 202, 97750),
(2, 203, 106250),
(3, 101, 88000),
(3, 201, 132000),
(3, 301, 176000),
(3, 401, 275000),
(3, 501, 550000),
(5, 101, 72000),
(5, 201, 108000),
(5, 301, 144000),
(5, 401, 225000),
(5, 501, 450000);
