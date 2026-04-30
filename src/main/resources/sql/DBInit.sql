-- 스키마 생성
CREATE DATABASE IF NOT EXISTS `hotel_kiosk`;

-- 사용자 생성, 권한 부여
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY '0220';

GRANT ALL PRIVILEGES ON `hotel_kiosk`.* TO 'admin'@'localhost';

-- 권한 다시 로드, 즉시 적용하기 위해 넣음
FLUSH PRIVILEGES;

-- 스키마 사용
USE `hotel_kiosk`;

-- 1. 관리자 정보
CREATE TABLE admin
(
    admin_id      VARCHAR(20) PRIMARY KEY COMMENT '관리자 아이디',
    password      VARCHAR(60)               NOT NULL COMMENT '관리자 비밀번호',
    admin_name    VARCHAR(20)               NOT NULL COMMENT '관리자 이름',
    admin_email   VARCHAR(50)               NOT NULL UNIQUE COMMENT '관리자 이메일',
    admin_phone   VARCHAR(13)               NOT NULL COMMENT '관리자 연락처',
    admin_grade   ENUM ('SUPER', 'GENERAL') NOT NULL COMMENT '관리자 등급',
    last_login    DATETIME                                       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 로그인 날짜',
    last_login_ip VARCHAR(45) COMMENT '마지막 로그인 IP',
    created_at    DATETIME                                       DEFAULT CURRENT_TIMESTAMP COMMENT '관리자 계정 생성일',
    statement     ENUM ('Working', 'Absence', 'Leave', 'Locked') DEFAULT 'Working' COMMENT '관리자 계정 상태',
    fail_count    INT                                            DEFAULT 0 COMMENT '로그인 실패 횟수'
);

-- 2. 고객 정보
CREATE TABLE members
(
    member_no         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고객 번호',
    member_name       VARCHAR(20) NOT NULL COMMENT '고객명',
    member_phone      VARCHAR(11) NOT NULL COMMENT '고객 연락처',
    member_birth      DATE        NOT NULL COMMENT '고객 생년월일',
    reg_date          DATE        NOT NULL COMMENT '고객 등록일',
    reservation_count INT         NOT NULL COMMENT '예약 횟수',
    member_point      INT DEFAULT 0 COMMENT '고객 보유 포인트',
    CHECK (reservation_count >= 1),
    CHECK (member_point >= 0)
);

-- 3. 객실 정보
CREATE TABLE room_master
(
    room_no     INT PRIMARY KEY COMMENT '객실 호수',
    room_name   VARCHAR(100) NOT NULL COMMENT '객실명',
    room_type   VARCHAR(100) NOT NULL COMMENT '객실 종류',
    room_view   VARCHAR(20)  NOT NULL COMMENT '객실 조망',
    room_floor  INT          NOT NULL COMMENT '객실 층수',
    base_price  INT          NOT NULL COMMENT '객실 기본 숙박 요금',
    max_people  INT          NOT NULL COMMENT '객실 제한 인원',
    bed_type    VARCHAR(50)  NOT NULL COMMENT '침대 종류',
    area        DOUBLE       NOT NULL COMMENT '객실 면적',
    rating      DOUBLE COMMENT '객실 평점',
    image_url   VARCHAR(2000) COMMENT '객실 이미지 경로',
    updated_at  DATETIME                    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '객실 정보 마지막 수정 일시',
    room_status ENUM ('active', 'deactive') DEFAULT 'active' COMMENT '객실 활성화/비활성화 여부',
    CHECK (rating >= 0.0 AND rating <= 5.0)
);

-- 4. 부가 서비스 정보(옵션)
CREATE TABLE option_master
(
    option_id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '옵션 ID',
    option_name     VARCHAR(50)                            NOT NULL COMMENT '옵션명',
    option_category ENUM ('Meal', 'Leisure', 'Consumable') NOT NULL COMMENT '옵션 종류',
    option_target   ENUM ('Adult', 'Child', 'Common')      NOT NULL COMMENT '옵션 적용 대상',
    option_price    INT                                    NOT NULL COMMENT '옵션별 요금'
);

-- 5. 요금 정책
CREATE TABLE pricing_policy
(
    policy_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '요금 정책 ID',
    policy_name   VARCHAR(50) NOT NULL COMMENT '요금 정책 이름',
    repeat_type   ENUM ('None', 'Weekly', 'Monthly') DEFAULT 'None' COMMENT '반복 주기',
    repeat_value  VARCHAR(20) COMMENT '반복 조건',
    start_date    DATE        NOT NULL COMMENT '요금 적용 시작일',
    end_date      DATE        NOT NULL COMMENT '요금 적용 종료일',
    discount_rate DOUBLE      NOT NULL COMMENT '할인율'
);

-- 6. 재고
CREATE TABLE stocks
(
    stock_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '재고 ID',
    stock_name   VARCHAR(20) NOT NULL COMMENT '재고명',
    stock_count  INT         NOT NULL COMMENT '재고 수량',
    min_stock    INT         NOT NULL COMMENT '재고 최소 보유 수량',
    stock_status ENUM ('Clear', 'Shortage') COMMENT '재고 보유 상태'
);

-- 7. 호텔 공지사항
CREATE TABLE inform_board
(
    inform_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 ID',
    title     VARCHAR(500) NOT NULL COMMENT '게시글 제목',
    content   TEXT         NOT NULL COMMENT '게시글 내용',
    writer    VARCHAR(20) DEFAULT '호텔 관리자' COMMENT '작성자',
    reg_date  DATE        DEFAULT CURRENT_DATE COMMENT '작성일',
    mod_date  DATE COMMENT '수정일'
);

-- 8. 실시간 객실 상태 (동적 데이터)
CREATE TABLE room_status
(
    room_no        INT PRIMARY KEY COMMENT '객실 호수',
    current_status ENUM ('Available', 'Occupied', 'Cleaning', 'Cleaning Required', 'Maintenance') NOT NULL COMMENT '객실 상태',
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '객실 상태 마지막 수정 일시',
    FOREIGN KEY (room_no) REFERENCES room_master (room_no)
);

-- 9. 예약 // Sending -> Pending으로 ???, created_at 추가 !!!
CREATE TABLE reservations
(
    idx            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK용 인덱스',
    reservation_id VARCHAR(13) COMMENT '예약 번호',
    room_no        INT                                         NOT NULL COMMENT '객실 호수',
    member_no      BIGINT                                      NOT NULL COMMENT '고객 번호',
    status         ENUM ('Reserved', 'In', 'Out', 'Cancelled') NOT NULL COMMENT '예약 상태',
    checkin_date   DATE                                        NOT NULL COMMENT '입실 예정 날짜',
    checkout_date  DATE                                        NOT NULL COMMENT '퇴실 예정 날짜',
    check_in_at    DATETIME COMMENT '실제 입실 일시',
    check_out_at   DATETIME COMMENT '실제 퇴실 일시',
    reg_people     INT                                         NOT NULL COMMENT '예약 인원',
    parking_num    VARCHAR(4) COMMENT '주차 등록 번호',
    add_option     TINYINT                                     NOT NULL COMMENT '옵션 추가 여부',
    pay_status     ENUM ('Success', 'Waiting', 'Refund', 'Failed') COMMENT '결제 여부',
    sms_status     ENUM ('Success', 'Pending', 'Failed') COMMENT '예약 완료 문자 발송 여부',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '예약 생성 일시',
    FOREIGN KEY (room_no) REFERENCES room_master (room_no),
    FOREIGN KEY (member_no) REFERENCES members (member_no)
);

--  reservation_id 트리거 지정
DELIMITER $$

CREATE TRIGGER before_insert_reservation
    BEFORE INSERT
    ON reservations
    FOR EACH ROW
BEGIN
    DECLARE new_id VARCHAR(13);
    DECLARE cnt INT;

    REPEAT
        SET new_id = CONCAT(
                DATE_FORMAT(NEW.checkin_date, '%y%m%d'),
                '-',
                LPAD(FLOOR(RAND() * 1000000), 6, '0')
                     );
        SELECT COUNT(*) INTO cnt FROM reservations WHERE reservation_id = new_id;
    UNTIL cnt = 0 END REPEAT;

    SET NEW.reservation_id = new_id;
END$$

DELIMITER ;


-- 10. 예약번호별 옵션 내역
CREATE TABLE options_record
(
    option_record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '예약별 옵션 기록 ID',
    idx              BIGINT NOT NULL COMMENT '예약 번호',
    option_id        BIGINT NOT NULL COMMENT '옵션 ID',
    quantity         INT    NOT NULL COMMENT '옵션 수량',
    option_charge    INT    NOT NULL COMMENT '옵션 총 부과 금액',
    FOREIGN KEY (idx) REFERENCES reservations (idx),
    FOREIGN KEY (option_id) REFERENCES option_master (option_id)
);

-- 11. 결제
CREATE TABLE payments
(
    payment_id    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결제 번호',
    idx           BIGINT               NOT NULL COMMENT '예약 번호',
    pay_method    ENUM ('Card', 'Pay') NOT NULL COMMENT '결제 방식',
    approval_no   VARCHAR(50) COMMENT '카드 승인 번호',
    room_price    INT                  NOT NULL COMMENT '객실 총 부과 요금',
    point_amount  INT COMMENT '포인트 사용 금액',
    option_charge INT                  NOT NULL COMMENT '옵션 총 부과 요금',
    total_charge  INT                  NOT NULL COMMENT '최종 결제 금액',
    sms_status    ENUM ('Success', 'Pending', 'Failed') COMMENT '결제 완료 문자 발송 여부',
    pay_status    ENUM ('Success', 'Waiting', 'Refund', 'Failed') COMMENT '결제 여부',
    toss_key   VARCHAR(200)         COMMENT '토스 결제 키',
    status_at     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '결제 상태 처리 일시',
    FOREIGN KEY (idx) REFERENCES reservations (idx)
);

-- 12. 포인트 내역
CREATE TABLE members_point
(
    point_id    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '포인트 ID',
    member_no   BIGINT NOT NULL COMMENT '고객 번호',
    idx         BIGINT NOT NULL COMMENT '예약 번호',
    change_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '포인트 변동 일시',
    earning     INT      DEFAULT 0 COMMENT '적립 포인트 (+)',
    using_point INT      DEFAULT 0 COMMENT '사용 포인트 (-)',
    FOREIGN KEY (member_no) REFERENCES members (member_no),
    FOREIGN KEY (idx) REFERENCES reservations (idx)
);

-- 13. 정책별 객실 가격
CREATE TABLE pricing_policy_room
(
    policy_id  BIGINT NOT NULL COMMENT '요금 정책 ID',
    room_no    INT    NOT NULL COMMENT '객실 호수',
    room_price INT COMMENT '요금 정책에 따라 산정된 객실 요금',
    CONSTRAINT pk_pricing_policy_room
        PRIMARY KEY (policy_id, room_no),
    FOREIGN KEY (policy_id) REFERENCES pricing_policy (policy_id),
    FOREIGN KEY (room_no) REFERENCES room_master (room_no)
);

-- 14. 결제 로그 테이블
CREATE TABLE toss_log
(
    lno            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '프라이머리 키',
    payment_id     BIGINT      NOT NULL COMMENT '결제번호',
    toss_key    VARCHAR(200)  COMMENT '토스 결제 키',
    result_code    VARCHAR(20) COMMENT '결과코드 (성공/실패)',
    result_message VARCHAR(500) COMMENT '결과 메시지',
    log_content    TEXT COMMENT '로그 기록',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (payment_id) REFERENCES payments (payment_id)
);