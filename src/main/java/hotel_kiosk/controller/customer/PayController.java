package hotel_kiosk.controller.customer;

import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.service.customer.ExtendedReservationService;
import hotel_kiosk.service.customer.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/JHotel/pay")
@RequiredArgsConstructor
public class PayController {
    // pay로 넘어올 예정인 페이지들 : /onsite/final_check, /checkin (미정), /extended_stay (미정)

    private final PayService payService;
    private final ExtendedReservationService extendedReservationService;

    @GetMapping
    public String pay(@RequestParam(required = false) String payResult,
                      @RequestParam(required = false) String orderId,
                      @RequestParam(required = false) Long amount,
                      @RequestParam(required = false) String code,
                      @RequestParam(required = false) String message,
                      Model model) {
        log.info("pay get... payResult={}", payResult);
        if (payResult != null) {
            model.addAttribute("payResult", payResult);
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", amount);
            model.addAttribute("code", code);
            model.addAttribute("message", message);
        }
        return "customer/pay";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Members> getMember(@RequestBody Map<String, String> request) {
        log.info("pay post...");
        String memberPhone = request.get("memberPhone");
        LocalDate memberBirth = LocalDate.parse(request.get("memberBirth"));
        Members member = payService.getMemberByPhoneAndBirth(memberPhone, memberBirth);

        if (member == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(member);
    }

    // 토스 결제(성공) - pay.html로 리다이렉트 후 완료 모달
    @GetMapping("/toss/success")
    public String tossSuccess(@RequestParam String paymentKey, @RequestParam String orderId,
                              @RequestParam Long amount, @RequestParam String reservationId,
                              @RequestParam int roomPrice, @RequestParam int optionCharge,
                              @RequestParam int pointAmount,
                              @RequestParam(required = false, defaultValue = "") String memberPhone,
                              @RequestParam(required = false, defaultValue = "") String memberBirth,
                              @RequestParam(required = false, defaultValue = "") String memberName,
                              @RequestParam(required = false) String paySource,
                              @RequestParam(required = false) String newCheckoutDate,
                              RedirectAttributes redirectAttributes) {
        log.info("toss success - paymentKey={}, orderId={}, amount={}, memberBirth={}, memberName={}", paymentKey, orderId, amount, memberBirth, memberName);

        try {
            LocalDate parsedMemberBirth = parseBirth(memberBirth);
            LocalDate parsedNewCheckoutDate = parseBirth(newCheckoutDate);

            // 1. 결제 정보 저장
            payService.confirmTossPayment(paymentKey, orderId, amount, reservationId, roomPrice, optionCharge, pointAmount, memberPhone, parsedMemberBirth, memberName);

            // 2. 결제 경로가 연장일 경우, 숙박 날짜 업데이트
            if ("extended".equals(paySource) && parsedNewCheckoutDate != null) {
                extendedReservationService.reviseCheckoutDate(reservationId, parsedNewCheckoutDate);
                log.info("숙박 연장 처리 완료: reservationId={}, newDate={}", reservationId, parsedNewCheckoutDate);
            }

            redirectAttributes.addAttribute("payResult", "complete");
            redirectAttributes.addAttribute("orderId", orderId);
            redirectAttributes.addAttribute("amount", amount);
            return "redirect:/JHotel/pay";
        } catch (Exception e) {
            log.error("결제 승인 중 오류: {}", e.getMessage());
            redirectAttributes.addAttribute("payResult", "fail");
            redirectAttributes.addAttribute("code", "CONFIRM_ERROR");
            redirectAttributes.addAttribute("message", e.getMessage() != null ? e.getMessage() : "알 수 없는 오류");
            return "redirect:/JHotel/pay";
        }
    }

    // 토스 결제(실패) - pay.html로 리다이렉트 후 실패 모달
    @GetMapping("/toss/fail")
    public String tossFail(@RequestParam(required = false) String code,
                           @RequestParam(required = false) String message,
                           RedirectAttributes redirectAttributes) {
        log.info("toss fail - code={}, message={}", code, message);
        redirectAttributes.addAttribute("payResult", "fail");
        redirectAttributes.addAttribute("code", code);
        redirectAttributes.addAttribute("message", message);
        return "redirect:/JHotel/pay";
    }

    // 카드 결제
    @PostMapping("/card")
    @ResponseBody
    public ResponseEntity<String> cardPay(@RequestBody Map<String, Object> request) {
        String reservationId = (String) request.get("reservationId");
        int roomPrice    = ((Number) request.get("roomPrice")).intValue();
        int optionCharge = ((Number) request.get("optionCharge")).intValue();
        int pointAmount  = ((Number) request.get("pointAmount")).intValue();
        int totalCharge  = ((Number) request.get("totalCharge")).intValue();
        String memberPhone = (String) request.getOrDefault("memberPhone", "");
        String memberName = (String) request.getOrDefault("memberName", "");
        String birthStr = (String) request.get("memberBirth");
        LocalDate memberBirth = parseBirth(birthStr);
        String paySource = (String) request.get("paySource");
        String dateStr = (String) request.get("newCheckoutDate");
        LocalDate newCheckoutDate = parseBirth(dateStr);

        payService.cardPayment(reservationId, roomPrice, optionCharge, pointAmount, totalCharge, memberPhone, memberBirth, memberName);

        // 2. 결제 경로가 연장일 경우, 숙박 날짜 업데이트
        if ("extended".equals(paySource) && newCheckoutDate != null) {
            extendedReservationService.reviseCheckoutDate(reservationId, newCheckoutDate);
            log.info("숙박 연장 처리 완료: reservationId={}, newDate={}", reservationId, newCheckoutDate);
        }
        return ResponseEntity.ok("success");
    }

    /* "YYYY-M-D" 또는 "YYYY-MM-DD" 둘 다 사용 가능 */
    private LocalDate parseBirth(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.YEAR, 4)
                    .appendLiteral('-')
                    .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
                    .appendLiteral('-')
                    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
                    .toFormatter();
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }
}
