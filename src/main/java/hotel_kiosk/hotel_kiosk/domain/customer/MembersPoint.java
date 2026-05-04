package hotel_kiosk.hotel_kiosk.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembersPoint {
    private Long pointId; // 포인트 ID
    private Long memberNo; // 고객 번호
    private String reservationId; // 예약 번호
    private LocalDate changeDate; // 포인트 변동 일시
    private int earning; // 적립 포인트 (+)
    private int usingPoint; // 사용 포인트 (-)
}
