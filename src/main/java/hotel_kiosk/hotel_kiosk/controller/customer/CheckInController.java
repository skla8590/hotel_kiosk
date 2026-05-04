package hotel_kiosk.hotel_kiosk.controller.customer;

import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.service.customer.ReservationIdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/JHotel/checkin")
public class CheckInController {

    private final ReservationIdService reservationIdService;

    // 체크인 입력 페이지
    @GetMapping("")
    public String checkInPage() {
        return "customer/checkin/checkin";
    }

    // 예약 조회 API (fetch 요청)
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<ReservationsDTO> searchReservation(@RequestParam String reservationId) {
        ReservationsDTO result = reservationIdService.getOneReservation(reservationId);
        return ResponseEntity.ok().body(result);
    }

    // 예약 상세 페이지
    @GetMapping("/reservation_detail")
    public String reservationDetail(@ModelAttribute("reservationId") String reservationId, Model model) {
        ReservationsDTO result = reservationIdService.getOneReservation(reservationId);
        model.addAttribute("reservation", result);
        return "customer/checkin/reservation_detail";
    }

    @PostMapping("/reservation_detail")
    public String reservationDetail(@RequestParam String reservationId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("reservationId",  reservationId);
        return "redirect:/JHotel/checkin/reservation_detail";
    }

    // 재결제 정보 조회
    @GetMapping("/repay-info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRepayInfo(@RequestParam String reservationId) {
        Map<String, Object> data = reservationIdService.getRepayInfo(reservationId);
        return ResponseEntity.ok(data);
    }

    // 체크인 처리 ("/checkin/do")
    @PostMapping("/do")
    @ResponseBody
    public ResponseEntity<String> doCheckIn(
            @RequestParam String reservationId,
            @RequestParam(required = false, defaultValue = "") String parkingNum) {
        reservationIdService.checkIn(reservationId, parkingNum);
        return ResponseEntity.ok("success");
    }
}
