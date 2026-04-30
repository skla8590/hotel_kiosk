USE `hotel_kiosk`;

-- =====================================================
-- 체크인 14시 활성화 테스트용 더미 데이터
-- 기준일: 2026-04-05 (오늘)
-- reservation_id 형식: YYMMDD(예약등록일)-랜덤6자리
-- =====================================================

INSERT INTO reservations (reservation_id, room_no, member_no, status, checkin_date, checkout_date, check_in_at, check_out_at, reg_people, parking_num, add_option, pay_status, sms_status)
VALUES
-- ✅ 케이스 1: 오늘(04-05) 체크인 → 14시 이후면 버튼 활성화
('260401-111111', 101, 1, 'Reserved', '2026-04-05', '2026-04-07', NULL, NULL, 2, NULL, 0, 'Success', 'Success'),

-- ❌ 케이스 2: 내일(04-06) 체크인 → 버튼 비활성화
('260401-222222', 201, 2, 'Reserved', '2026-04-06', '2026-04-08', NULL, NULL, 2, NULL, 0, 'Success', 'Success'),

-- ❌ 케이스 3: 어제(04-04) 체크인 → 이미 지난 날짜, 버튼 비활성화
('260401-333333', 301, 3, 'Out', '2026-04-04', '2026-04-06', NULL, NULL, 2, NULL, 0, 'Success', 'Success');
