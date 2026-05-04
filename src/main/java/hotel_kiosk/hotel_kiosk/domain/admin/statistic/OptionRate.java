package hotel_kiosk.hotel_kiosk.domain.admin.statistic;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRate {
    private String label;  // option_name
    private Double rate;   // 이용률
}
