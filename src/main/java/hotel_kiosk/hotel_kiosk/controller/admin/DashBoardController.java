package hotel_kiosk.hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.DashboardDTO;
import hotel_kiosk.dto.admin.ReservationSummaryDTO;
import hotel_kiosk.dto.admin.RoomSummaryDTO;
import hotel_kiosk.dto.admin.StocksDTO;
import hotel_kiosk.service.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.round;

@Controller
@RequestMapping("/admin/dashboard")
@Log4j2
@RequiredArgsConstructor
public class DashBoardController {
    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model) {
        log.info("dashboard...");

        ReservationSummaryDTO resSummary = dashboardService.countReservation();
        RoomSummaryDTO roomSummary = dashboardService.readRoom();
        DashboardDTO dashboardDTO = new DashboardDTO();
        List<StocksDTO> stocksDTOList = dashboardService.readStockByWarning();

        log.info(resSummary);
        log.info(roomSummary);
        log.info(stocksDTOList);

        // 총합 계산
        int resTotal = resSummary.getSuccess()
                + resSummary.getWaiting()
                + resSummary.getCancelled();
        int roomTotal = roomSummary.getAvailable()
                + roomSummary.getOccupied()
                + roomSummary.getCleaning()
                + roomSummary.getCleaningRequired()
                + roomSummary.getMaintenance();

        int max = Stream.of(
                roomSummary.getOccupied(),
                roomSummary.getCleaningRequired(),
                roomSummary.getCleaning(),
                roomSummary.getMaintenance(),
                roomSummary.getAvailable()
        ).max(Integer::compareTo).orElse(1);

        log.info(resTotal);
        log.info(roomTotal);
        log.info(max);


        //
        int totalRooms = dashboardService.countAllRoom();
        dashboardDTO.setTotalRooms(totalRooms);
        int currentGuests = dashboardService.countCheckinRoom();
        dashboardDTO.setCurrentGuests(currentGuests);
        int todayCheckin = dashboardService.countTodayCheckin();
        dashboardDTO.setTodayCheckIn(todayCheckin);
        int stockAlert = dashboardService.countWaringStock();
        dashboardDTO.setStockAlert(stockAlert);
        int upcomingCheckIn = dashboardService.countTodayCheckin();
        dashboardDTO.setUpcomingCheckIn(upcomingCheckIn);

        // 퍼센트 계산
        double succeedRate = resTotal == 0 ? 0 : (resSummary.getSuccess() * 100.0 / resTotal);
        double waitedRate  = resTotal == 0 ? 0 : (resSummary.getWaiting() * 100.0 / resTotal);
        double cancelRate  = resTotal == 0 ? 0 : (resSummary.getCancelled() * 100.0 / resTotal);

        double occupancyRate = round((currentGuests == 0 ? 0 : (currentGuests * 100.0 / roomTotal)));

        // DTO에 세팅 (또는 별도 DTO 사용 가능)
        resSummary.setSucceedRate(succeedRate);
        resSummary.setWaitedRate(waitedRate);
        resSummary.setCancelledRate(cancelRate);

        roomSummary.setOccupiedRate(roomSummary.getOccupied() * 100.0 / max);
        roomSummary.setCleaningRequiredRate(roomSummary.getCleaningRequired() * 100.0 / max);
        roomSummary.setCleaningRate(roomSummary.getCleaning() * 100.0 / max);
        roomSummary.setMaintenanceRate(roomSummary.getMaintenance() * 100.0 / max);
        roomSummary.setAvailableRate(roomSummary.getAvailable() * 100.0 / max);

        dashboardDTO.setOccupancyRate(occupancyRate);

        log.info(resSummary);
        log.info(roomSummary);
        log.info(dashboardDTO);

        model.addAttribute("resSummary", resSummary);

        model.addAttribute("roomSummary", roomSummary);

        model.addAttribute("kpi", dashboardDTO);

        model.addAttribute("stockAlerts", stocksDTOList);

        return "admin/main/dashboard";
    }

    @GetMapping("/stock/alerts")
    @ResponseBody
    public List<StocksDTO> getStockAlerts() {
        return dashboardService.readStockByWarning();
    }
}
