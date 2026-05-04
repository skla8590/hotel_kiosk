package hotel_kiosk.hotel_kiosk.domain.admin.statistic;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatSummary {
    private Long totalRevenue;     // 총 매출
    private Double avgOccupancy;   // 평균 이용률
    private Integer totalCheckIn;  // 체크인 수

    private double revenueGrowth;   // 지난달 성장률
    private double occupancyGoal;   // 목표 이용률
    private double checkInGrowth;   // 지난달 체크인 이용률
}
