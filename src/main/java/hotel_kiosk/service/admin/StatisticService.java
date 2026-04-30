package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.statistic.OptionRate;
import hotel_kiosk.domain.admin.statistic.RoomRate;
import hotel_kiosk.domain.admin.statistic.StatSummary;
import hotel_kiosk.dto.admin.statistic.OptionRateDTO;
import hotel_kiosk.dto.admin.statistic.PaymentRevenueDTO;
import hotel_kiosk.dto.admin.statistic.RoomRateDTO;
import hotel_kiosk.dto.admin.statistic.StatSummaryDTO;

import java.util.List;

public interface StatisticService {
    // 통계 요약
    StatSummaryDTO totalSummary(String startDate, String endDate);
    // 퍼센트 계산용
    Double calculateGrowth(Double current, Double previous);
    // 날짜별 매출 통계
    List<PaymentRevenueDTO> getRevenueChart(String unit, String startDate, String endDate);
    //
    List<RoomRateDTO> getRoomStatic(String startDate, String endDate);
    //
    List<OptionRateDTO> getOptionStatic(String startDate, String endDate);
}
