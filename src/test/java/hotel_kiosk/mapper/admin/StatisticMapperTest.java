package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.statistic.RoomRate;
import hotel_kiosk.domain.admin.statistic.OptionRate;
import hotel_kiosk.domain.admin.statistic.PaymentRevenue;
import hotel_kiosk.domain.admin.statistic.StatSummary;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
class StatisticMapperTest {
    @Autowired
    private StatisticMapper statisticMapper;

    @Test
    void summaryStaticTest() {
        String startDate = "20260101";
        String endDate = "20260520";
        log.info("----------------------");
        StatSummary summaryStatic = statisticMapper.summaryStatic(startDate, endDate);
        log.info(summaryStatic);
    }

    @Test
    void paymentStaticTest() {
        String startDate = "20260101";
        String endDate = "20260520";
        String days = "MONTH";
        log.info("----------------------");
        List<PaymentRevenue> paymentStatic = statisticMapper.paymentStatic(days, startDate, endDate);
        log.info(paymentStatic);
    }

    @Test
    void roomStaticTest() {
        String startDate = "20260101";
        String endDate = "20260520";
        log.info("----------------------");
        List<RoomRate> roomStatic = statisticMapper.roomStatic(startDate, endDate);
        log.info(roomStatic);
    }

    @Test
    void optionStaticTest() {
        String startDate = "20260101";
        String endDate = "20260520";
        log.info("----------------------");
        List<OptionRate> optionStatic = statisticMapper.optionStatic(startDate, endDate);
        log.info(optionStatic);
    }
}