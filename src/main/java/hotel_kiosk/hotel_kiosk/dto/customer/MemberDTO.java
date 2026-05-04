package hotel_kiosk.hotel_kiosk.dto.customer;

import lombok.*;

import java.time.LocalDate;


@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private int memberNo; // 고객 번호
    private String memberName; // 고객명
    private String memberPhone; // 고객 연락처
    private LocalDate memberBirth; // 고객 생년월일
    private LocalDate regDate; // 고객 등록일
    private int reservationCount; // 예약 횟수
    private int memberPoint; // 고객 보유 포인트

    private int totalPayment; // 누적 금액
    private LocalDate lastVisit;    // 최근 방문일
}
