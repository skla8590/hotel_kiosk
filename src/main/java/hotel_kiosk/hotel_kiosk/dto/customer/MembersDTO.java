package hotel_kiosk.hotel_kiosk.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembersDTO {
    private int memberNo; // 고객 번호
    private String memberName; // 고객명
    private String memberPhone; // 고객 연락처
    private LocalDate memberBirth; // 고객 생년월일
    private LocalDate regDate; // 고객 등록일
    private int reservationCount; // 예약 횟수
    private int memberPoint; // 고객 보유 포인트
}
