package hotel_kiosk.hotel_kiosk.dto.admin.statistic;

import hotel_kiosk.dto.admin.statistic.OptionRateDTO;
import hotel_kiosk.dto.admin.statistic.PaymentRevenueDTO;
import hotel_kiosk.dto.admin.statistic.RoomRateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatSummaryDTO {
    private Long totalRevenue;     // 총 매출
    private double avgOccupancy;   // 평균 이용률
    private Integer totalCheckIn;  // 체크인 수

    private double revenueGrowth;   //
    private double occupancyGoal;   //
    private double checkInGrowth;   //

    private List<PaymentRevenueDTO> paymentRevenues;
    private List<RoomRateDTO> roomRates;
    private List<OptionRateDTO> optionRates;
}
