package hotel_kiosk.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsDTO {
    /*
    - 포인트 사용 후
    1. 일반 카드 결제
    2. 토스 페이 결제
     */
    private Long paymentId;          // 결제번호 -> payments 테이블
    private String reservationId;    // 예약번호 -> payments 테이블
    private String payMethod;        // 결제 방식 -> payments 테이블
    private String approvalNo;       // 카드 승인 번호 -> payments 테이블
    private int roomPrice;           // 객실 총 부과 요금 -> payments 테이블
    private int pointAmount;         // 포인트 사용 금액 -> payments 테이블
    private int optionCharge;        // 옵션 총 부과 요금 -> payments 테이블
    private int totalCharge;         // 최종 결제 금액 -> payments 테이블
    private String smsStatus;        // 결제 완료 문자 발송 여부 -> payments 테이블
    private String payStatus;        // 결제 여부 -> payments 테이블

    private String roomNo;           // 객실호수 -> reservations 테이블
    private String memberName;       // 예약자명 -> members 테이블
    private String memberPhone;      // 예약자 연락처 -> members 테이블
    private String tossKey;          // 토스 결제 키 -> 자동 입력
    private LocalDateTime createdAt; // 생성일시 -> LocalDateTime.now()
}