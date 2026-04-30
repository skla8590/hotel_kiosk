package hotel_kiosk.dto.admin.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateDTO {
    private String label;  // room_type
    private Double rate;   // 이용률
    private String color;  // 프론트용
}
