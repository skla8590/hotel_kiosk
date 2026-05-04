package hotel_kiosk.hotel_kiosk.domain.admin.statistic;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRevenue {
    private String label;   // 날짜/주/월
    private Long revenue;   // 매출
    private double height;  //
}
