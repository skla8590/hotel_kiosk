package hotel_kiosk.hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.statistic.OptionRate;
import hotel_kiosk.domain.admin.statistic.PaymentRevenue;
import hotel_kiosk.domain.admin.statistic.RoomRate;
import hotel_kiosk.domain.admin.statistic.StatSummary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticMapper {
    StatSummary summaryStatic(String startDate, String endDate);

    StatSummary beforeSummaryStatic(String startDate, String endDate);

    List<PaymentRevenue> paymentStatic(String unit, String startDate, String endDate);

    List<RoomRate> roomStatic(String startDate, String endDate);

    List<OptionRate> optionStatic(String startDate, String endDate);
}
