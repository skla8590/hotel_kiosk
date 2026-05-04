package hotel_kiosk.hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.statistic.OptionRateDTO;
import hotel_kiosk.dto.admin.statistic.PaymentRevenueDTO;
import hotel_kiosk.dto.admin.statistic.RoomRateDTO;
import hotel_kiosk.dto.admin.statistic.StatSummaryDTO;
import hotel_kiosk.service.admin.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/statistic")
@Log4j2
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public String statistic(Model model) {
        log.info("statistic......");
        LocalDate today = LocalDate.now();

        // 현재 달의 1일
        LocalDate startDate = today.withDayOfMonth(1);

        // 현재 달의 마지막 날
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String start = startDate.format(formatter);
        String end = endDate.format(formatter);
        StatSummaryDTO statSummaryDTO = statisticService.totalSummary(start, end);
        log.info(statSummaryDTO);

        model.addAttribute("statSummary", statSummaryDTO);

        log.info(statisticService.getRevenueChart("DAY", start, end));
        log.info(statisticService.getRoomStatic(start, end));
        log.info(statisticService.getOptionStatic(start, end));
        model.addAttribute("revenueChart", statisticService.getRevenueChart("DAY", start, end));
        model.addAttribute("occupancyChart", statisticService.getRoomStatic(start, end));
        model.addAttribute("optionChart", statisticService.getOptionStatic(start, end));


        return "admin/management/report/statistic";
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<StatSummaryDTO> search(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String unit) {

        log.info("검색 startDate: {}", startDate);
        log.info("검색 endDate: {}", endDate);
        log.info("검색 unit: {}", unit);

        // 1. 기본값 처리 (값 없으면 이번달)
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {

            LocalDate today = LocalDate.now();

            LocalDate start = today.withDayOfMonth(1);
            LocalDate end = today.withDayOfMonth(today.lengthOfMonth());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            startDate = start.format(formatter);
            endDate = end.format(formatter);
        }

        // 2. 서비스 호출
        StatSummaryDTO result = statisticService.totalSummary(startDate, endDate);
        List<RoomRateDTO> roomRateDTOS = statisticService.getRoomStatic(startDate, endDate);
        List<OptionRateDTO> optionRateDTOList = statisticService.getOptionStatic(startDate, endDate);
        List<PaymentRevenueDTO> paymentRevenueDTOList = statisticService.getRevenueChart(unit, startDate, endDate);

        result.setRoomRates(roomRateDTOS);
        result.setOptionRates(optionRateDTOList);
        result.setPaymentRevenues(paymentRevenueDTOList);

        log.info("결과: {}", result);

        // 3. 응답
        return ResponseEntity.ok(result);
    }
}
