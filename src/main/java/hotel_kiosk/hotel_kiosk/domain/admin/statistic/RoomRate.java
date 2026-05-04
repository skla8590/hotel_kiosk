package hotel_kiosk.hotel_kiosk.domain.admin.statistic;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRate {
    private String label;  // room_type
    private Double rate;   // 이용률
}
