-- room_status 더미 데이터 (코드 구현 테스트용)
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

-- members 더미 (reservations FK 때문에 필요)
INSERT INTO members (member_name, member_phone, member_birth, reg_date, reservation_count, member_point)
VALUES ('테스트', '01011111111', '1990-01-01', CURDATE(), 1, 100);
-- member_no = 1 로 가정

-- reservations 더미 (신규 요청: 3/30 ~ 4/1)
INSERT INTO reservations (room_no, member_no, status, checkin_date, checkout_date, reg_people, add_option, pay_status)
VALUES (1101, 1, 'Reserved', '2026-03-30', '2026-04-01', 2, 0, 'Success'),
       (1102, 1, 'Reserved', '2026-03-28', '2026-03-31', 2, 0, 'Success'),
       (1103, 1, 'Reserved', '2026-03-31', '2026-04-02', 2, 0, 'Success'),
       (1104, 1, 'Reserved', '2026-03-29', '2026-04-02', 2, 0, 'Success'),
       (1105, 1, 'Reserved', '2026-03-28', '2026-03-30', 2, 0, 'Success'),
       (1201, 1, 'Reserved', '2026-04-01', '2026-04-03', 2, 0, 'Success'),
       (1202, 1, 'Cancelled', '2026-03-30', '2026-04-01', 3, 0, 'Refund');


-- pricing_policy 더미 데이터
INSERT INTO pricing_policy (policy_name, repeat_type, repeat_value, start_date, end_date, discount_rate)
VALUES ('주말 할증', 'Weekly', '6,7', '2026-01-01', '2026-12-31', 1.2), -- 토/일 20% 할증
       ('봄 성수기', 'None', NULL, '2026-03-28', '2026-04-05', 1.15),   -- 봄 성수기 15% 할증
       ('비수기 할인', 'None', NULL, '2026-05-01', '2026-06-30', 0.9);
-- 비수기 10% 할인

-- pricing_policy_room 더미 데이터
INSERT INTO pricing_policy (policy_name, repeat_type, repeat_value, start_date, end_date, discount_rate)
VALUES ('봄 성수기', 'None', NULL, '2026-03-28', '2026-04-05', 1.15),
       ('주말 할증', 'Weekly', '6,7', '2026-01-01', '2026-12-31', 1.2),
       ('비수기 할인', 'None', NULL, '2026-05-01', '2026-06-30', 0.9);

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
