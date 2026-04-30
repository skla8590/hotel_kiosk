package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class DashboardServiceTest {
    @Autowired
    private DashboardService dashboardService;

    @Test
    void countRoom() {
        int result = dashboardService.countAllRoom();
        log.info(result);
    }

    @Test
    void countCheckinRoom() {
        int result = dashboardService.countCheckinRoom();
        log.info(result);
    }

    @Test
    void countTodayCheckin() {
        int result = dashboardService.countTodayCheckin();
        log.info(result);
    }

    @Test
    void countTodayReservation() {
        int result = dashboardService.countTodayReservation();
        log.info(result);
    }

    @Test
    void countWaringStock() {
        int result = dashboardService.countWaringStock();
        log.info(result);
    }

    @Test
    void countReservationTest() {
        ReservationSummaryDTO integerList = dashboardService.countReservation();
        log.info(integerList);
    }
}