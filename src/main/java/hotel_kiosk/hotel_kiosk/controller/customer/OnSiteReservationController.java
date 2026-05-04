package hotel_kiosk.hotel_kiosk.controller.customer;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Payments;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.PreReserveRequestDTO;
import hotel_kiosk.dto.customer.RoomSearchRequestDTO;
import hotel_kiosk.mapper.customer.ExtendedReservationMapper;
import hotel_kiosk.service.customer.ExtendedReservationService;
import hotel_kiosk.service.customer.OnSiteReservationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/JHotel/onsite")
@RequiredArgsConstructor
public class OnSiteReservationController {
    private final OnSiteReservationService onSiteReservationService;
    private final ExtendedReservationService extendedReservationService;
    private final ExtendedReservationMapper extendedReservationMapper;

    @GetMapping("/search")
    public String searchByCondition() {
        return "customer/reservation/on_site/room_search";
    }

    @GetMapping("/rooms")
    public String getAvailableRooms(@RequestParam LocalDate checkinDate,
                                              @RequestParam LocalDate checkoutDate,
                                              @RequestParam int regPeople,
                                              Model model) {
        List<RoomMaster> rooms = onSiteReservationService.getAvailableRooms(checkinDate, checkoutDate, regPeople);
        model.addAttribute("rooms", rooms);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("regPeople", regPeople);
        return "customer/reservation/on_site/room_selection";
    }

    /* 객실 목록 JSON 전달용 */
    @PostMapping("/rooms")
    @ResponseBody
    public List<RoomMaster> getAvailableRooms(@RequestBody RoomSearchRequestDTO requestDTO) {
        return onSiteReservationService.getAvailableRooms(
                requestDTO.getCheckinDate(),
                requestDTO.getCheckoutDate(),
                requestDTO.getRegPeople());
    }

    @GetMapping("/selection")
    public String roomSelection() {
        return "customer/reservation/on_site/room_selection";
    }

    @GetMapping("/option")
    public String optionSelection() {
        return "customer/reservation/on_site/room_option";
    }

    @GetMapping("/api/options")
    @ResponseBody
    public List<OptionsMaster> getOptions() {
        return onSiteReservationService.getAllOptions();
    }

    @GetMapping("/checking")
    public String checkingRoom() {
        return "customer/reservation/on_site/check";
    }

    @GetMapping("/checking_customer")
    public String checkingCustomer() {
        return "customer/reservation/on_site/check_customer";
    }

    @GetMapping("/final_check")
    public String finalCheck(HttpSession httpSession, Model model) {
        log.info("예약 정보 최종 확인 페이지 ...");

        // 1. 기존 예약 정보 가져오기 (/extended에서 넘어오는 정보)
        Reservations reservation = (Reservations) httpSession.getAttribute("reservation");
        int prevPaidAmount = 0;

        // 2. 기존 예약 정보가 존재한다면 이전 결제 금액 불러오기
        if (reservation != null && reservation.getReservationId() != null) {
            int amount = extendedReservationService.getPaymentAmount(reservation.getReservationId());
            if (amount != 0) {
                prevPaidAmount = amount;
            }
        }

        // 3. 이전 결제 금액 저장
        model.addAttribute("prevPaidAmount", prevPaidAmount);

        return "customer/reservation/on_site/final_check";
    }

    @PostMapping("/process")
    public void processOnSiteReservation(Members members, Reservations reservations, Payments payments) {
        onSiteReservationService.processOnSiteReservation(members, reservations, payments);
    }

    /* 결제 전 예약 임시 등록 → reservationId 리스트 반환 */
    @PostMapping("/pre-reserve")
    @ResponseBody
    public Map<String, Object> preReserve(@RequestBody PreReserveRequestDTO dto) {
        log.info("pre-reserve 요청: {}", dto);
        Members members = Members.builder()
                .memberName(dto.getMemberName())
                .memberPhone(dto.getMemberPhone())
                .memberBirth(LocalDate.parse(dto.getMemberBirth()))
                .build();

        Reservations reservations = new Reservations();
        reservations.setCheckinDate(LocalDate.parse(dto.getCheckinDate()));
        reservations.setCheckoutDate(LocalDate.parse(dto.getCheckoutDate()));
        reservations.setRegPeople(dto.getRegPeople());
        reservations.setAddOption(dto.getAddOption());

        List<String> reservationIds = onSiteReservationService.preRegisterReservation(
                members, reservations, dto.getRoomNos()
        );

        return Map.of("reservationIds", reservationIds);
    }
}