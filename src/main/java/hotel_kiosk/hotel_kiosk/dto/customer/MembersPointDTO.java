package hotel_kiosk.hotel_kiosk.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembersPointDTO {
    private Long pointId; // 포인트 ID
    private Long memberNo; // 고객 번호
    private Long idx; // reservation에 idx
    private LocalDate changeDate; // 포인트 변동 일시
    private int earning; // 적립 포인트 (+)
    private int usingPoint; // 사용 포인트 (-)
}
