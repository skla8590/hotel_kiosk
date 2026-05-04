package hotel_kiosk.hotel_kiosk.dto.admin.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRevenueDTO {
    private String label;   // 날짜/주/월
    private Long revenue;   // 매출
    private double height;  //
    private boolean isGold;
}
