package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.statistic.OptionRate;
import hotel_kiosk.domain.admin.statistic.PaymentRevenue;
import hotel_kiosk.domain.admin.statistic.RoomRate;
import hotel_kiosk.domain.admin.statistic.StatSummary;
import hotel_kiosk.dto.admin.statistic.OptionRateDTO;
import hotel_kiosk.dto.admin.statistic.PaymentRevenueDTO;
import hotel_kiosk.dto.admin.statistic.RoomRateDTO;
import hotel_kiosk.dto.admin.statistic.StatSummaryDTO;
import hotel_kiosk.mapper.admin.StatisticMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticMapper statisticMapper;
    private final ModelMapper modelMapper;

    @Override
    public StatSummaryDTO totalSummary(String startDate, String endDate) {

        // 현재
        StatSummary current = statisticMapper.summaryStatic(startDate, endDate);
        // 전월
        StatSummary before = statisticMapper.beforeSummaryStatic(startDate, endDate);

        log.info("current: {}", current);
        log.info("before: {}", before);

        // 성장률 계산
        Double revenueGrowth = calculateGrowth(
                (double) current.getTotalRevenue(),
                (double) before.getTotalRevenue()
        );
        log.info(current.getTotalCheckIn());
        log.info(before.getTotalCheckIn());

        Double checkInGrowth = calculateGrowth(
                (double) current.getTotalCheckIn(),
                (double) before.getTotalCheckIn()
        );
        log.info("revenueGrowth: {}", revenueGrowth);
        log.info("checkInGrowth: {}", checkInGrowth);

        // DTO에 추가 세팅
        StatSummaryDTO statSummaryDTO = modelMapper.map(current, StatSummaryDTO.class);

        statSummaryDTO.setRevenueGrowth(revenueGrowth);
        statSummaryDTO.setCheckInGrowth(checkInGrowth);

        log.info(statSummaryDTO);

        return statSummaryDTO;
    }

    @Override
    public Double calculateGrowth(Double current, Double previous) {
        if (previous == null || previous == 0) {
            return 0.0;
        }

        return Math.round(((current - previous) / previous * 100) * 100.0) / 100.0;
    }

    @Override
    public List<PaymentRevenueDTO> getRevenueChart(String unit, String startDate, String endDate) {
        List<PaymentRevenue> list = statisticMapper.paymentStatic(unit, startDate, endDate);

        List<PaymentRevenueDTO> paymentRevenueDTOList =
                modelMapper.map(list, new TypeToken<List<PaymentRevenueDTO>>() {}.getType());

        Long max = paymentRevenueDTOList.stream()
                .mapToLong(PaymentRevenueDTO::getRevenue)
                .max()
                .orElse(1);

        for (PaymentRevenueDTO dto : paymentRevenueDTOList) {
            double height = (dto.getRevenue() * 100.0) / max;
            log.info("height: {}", height);
            dto.setHeight(height);
        }

        log.info(paymentRevenueDTOList);

        return paymentRevenueDTOList;
    }

    @Override
    public List<RoomRateDTO> getRoomStatic(String startDate, String endDate) {
        List<RoomRate> list = statisticMapper.roomStatic(startDate, endDate);

        // 안전한 매핑 (중요)
        List<RoomRateDTO> roomRateDTOS = list.stream()
                .map(entity -> modelMapper.map(entity, RoomRateDTO.class))
                .toList();
        // 색상 세팅 (프론트용)
        String[] colors = {"#4F46E5", "#22C55E", "#F59E0B", "#EF4444", "#06B6D4"};

        int i = 0;
        for (RoomRateDTO dto : roomRateDTOS) {
            // null 방어
            if (dto.getRate() == null || dto.getRate().isNaN()) {
                dto.setRate(0.0);
            }
            // 색상 순환 적용
            dto.setColor(colors[i % colors.length]);
            i++;
        }
        log.info(roomRateDTOS);
        return roomRateDTOS;
    }

    @Override
    public List<OptionRateDTO> getOptionStatic(String startDate, String endDate) {
        List<OptionRate> list = statisticMapper.optionStatic(startDate, endDate);
        // 안전한 매핑
        List<OptionRateDTO> optionRateDTOList = list.stream()
                .map(entity -> modelMapper.map(entity, OptionRateDTO.class))
                .toList();
        // null 및 NaN 방어
        for (OptionRateDTO dto : optionRateDTOList) {
            if (dto.getRate() == null || dto.getRate().isNaN()) {
                dto.setRate(0.0);
            }
        }
        log.info(optionRateDTOList);
        return optionRateDTOList;
    }
}
