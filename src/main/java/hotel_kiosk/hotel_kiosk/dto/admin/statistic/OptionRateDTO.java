package hotel_kiosk.hotel_kiosk.dto.admin.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRateDTO {
    private String label;  // option_name
    private Double rate;   // 이용률
    private double height;  //
}
