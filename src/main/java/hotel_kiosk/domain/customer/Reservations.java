package hotel_kiosk.domain.customer;

import hotel_kiosk.domain.admin.RoomOptionsSummary;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reservations {
    private Long idx;                 // 인덱스
    private String reservationId;     // 예약 번호
    private int roomNo;               // 객실 호수 (FK),
    private Long memberNo;            // 고객 번호 (FK),
    private String status;            // 예약 상태 ('Reserved', 'In', 'Out', 'Cancelled')
    private LocalDate checkinDate;    // 입실 예정 날짜
    private LocalDate checkoutDate;   // 퇴실 예정 날짜
    private LocalDateTime checkInAt;  // 실제 입실 일시
    private LocalDateTime checkOutAt; // 실제 퇴실 일시
    private Integer regPeople;        // 예약 인원
    private String parkingNum;        // 주차 등록 번호
    private Integer addOption;        // 옵션 추가 여부 (0 = false, 1 = true)
    private String payStatus;         // 결제 여부 ('Success', 'Waiting', 'Refund', 'Failed')
    private String smsStatus;         // 예약 완료 문자 발송 여부 ('Success', 'Pending', 'Failed')
    private LocalDateTime createdAt;  // 예약 일시

    // 예약자 정보
    private String memberName;        // 고객명
    private String memberPhone;       // 고객 연락처

    // 객실명
    private String roomName;

    // 옵션 정보
    private RoomOptionsSummary options;
}
