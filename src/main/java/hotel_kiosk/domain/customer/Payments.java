package hotel_kiosk.domain.customer;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payments {
    private Long paymentId;         // 결제 번호 (PK)
    private String reservationId;   // 예약 번호 (FK)
    private String payMethod;       // 결제 방식 (Card, Pay)
    private String approvalNo;      // 카드 승인 번호
    private int roomPrice;          // 객실 총 부과 요금
    private Integer pointAmount;    // 포인트 사용 금액
    private int optionCharge;       // 옵션 총 부과 요금
    private int totalCharge;        // 최종 결제 금액
    private String smsStatus;       // 결제 완료 문자 발송 여부' ('Success', 'Pending', 'Failed')
    private String payStatus;       // 결제 여부 ('Success', 'Waiting', 'Refund', 'Failed')
    private LocalDateTime statusAt; // 결제 상태 처리 일시
}
