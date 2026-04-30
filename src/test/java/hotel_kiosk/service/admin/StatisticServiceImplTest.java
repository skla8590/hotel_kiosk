package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.statistic.StatSummary;
import hotel_kiosk.dto.admin.statistic.PaymentRevenueDTO;
import hotel_kiosk.dto.admin.statistic.StatSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
class StatisticServiceImplTest {
    @Autowired(required = false)
    private StatisticService statisticService;

    @Test
    void testStatusSummary() {
        String startDate = "20260101";
        String endDate = "20260520";
        log.info("----------------------");
        StatSummaryDTO summaryStatic = statisticService.totalSummary(startDate, endDate);
        log.info(summaryStatic.getTotalRevenue());
        log.info(summaryStatic.getAvgOccupancy());
        log.info(summaryStatic.getTotalCheckIn());
    }

    @Test
    void testRevenueChart() {
        String startDate = "20260101";
        String endDate = "20260520";
        String unit = "DAY";
        log.info("-----------------------");
        List<PaymentRevenueDTO> paymentRevenueDTOS = statisticService.getRevenueChart(unit, startDate, endDate);
        log.info(paymentRevenueDTOS);
    }
}