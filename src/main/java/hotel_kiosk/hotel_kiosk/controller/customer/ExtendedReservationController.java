package hotel_kiosk.hotel_kiosk.controller.customer;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.AvailableRoomForExtendedDTO;
import hotel_kiosk.service.customer.ExtendedReservationService;
import hotel_kiosk.service.customer.OnSiteReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/JHotel/extended")
@RequiredArgsConstructor
public class ExtendedReservationController {
    private final ExtendedReservationService extendedReservationService;
    private final OnSiteReservationService onSiteReservationService;

    @GetMapping("/check_info")
    public String checkInfo() {
        log.info("============ get checkInfo... ============");
        return "/customer/reservation/extended_stay/extended";
    }

    @PostMapping("/check_info")
    @ResponseBody
    public ResponseEntity<Reservations> getInfo(@RequestBody Map<String, String> request) {
        log.info("============ post getInfo... ============");

        String reservationId = request.get("reservationId");
        Reservations reservation = extendedReservationService.getReservationById(reservationId);

        if (reservation == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/available")
    public String checkAvailable() {
        log.info("============ get checkAvailable... ============");
        return "/customer/reservation/extended_stay/check_extended";
    }

    @PostMapping("/available")
    @ResponseBody
    public ResponseEntity<List<AvailableRoomForExtendedDTO>> getDetail(@RequestBody Map<String, String> request) {
        log.info("============ post getDetail... ============");

        String reservationId = request.get("reservationId");
        List<AvailableRoomForExtendedDTO> reservationDetail = extendedReservationService.getReservationDetail(reservationId);

        if (reservationDetail == null || reservationDetail.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(reservationDetail);
    }

    @GetMapping("/set_checkoutDate")
    public String setCheckoutDate() {
        return "/customer/reservation/extended_stay/set_checkout";
    }

    @PostMapping("/set_checkoutDate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAvailability(@RequestBody Map<String, Object> params) {
        log.info("연장 가능 여부 확인");

        String reservationId = (String) params.get("reservationId");
        Integer roomNo = (Integer) params.get("roomNo");
        LocalDate checkinDate = LocalDate.parse((String) params.get("checkinDate"));
        LocalDate checkoutDate = LocalDate.parse((String) params.get("checkoutDate"));
        String status = (String) params.get("status");
        Long memberNo = Long.parseLong(params.get("memberNo").toString());

        // 연장 가능 여부 확인
        Reservations reservation = Reservations.builder()
                .reservationId(reservationId)
                .roomNo(roomNo)
                .memberNo(memberNo)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .status(status).build();
        int count = extendedReservationService.getAvailableRoom(reservation);

        // 2. 응답 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);

        Members memberInfo = extendedReservationService.getMemberInfo(memberNo);
        response.put("memberInfo", memberInfo);

        if (count == 0) { // 연장 가능한 객실인 경우, 객실 정보, 회원 정보 넘겨주기
            RoomMaster roomInfo = extendedReservationService.getRoomInfo(roomNo);
            response.put("roomInfo", roomInfo);
            response.put("memberInfo", memberInfo);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/option")
    public String setOption() {
        log.info("연장할 객실 옵션 설정 페이지 접근");
        return "/customer/reservation/extended_stay/room_option";
    }

    @GetMapping("/rooms")
    public String getAnotherRoom() {
        log.info("예약 가능한 다른 객실 찾기");
        return "/customer/reservation/extended_stay/room_selection";
    }

    /* 객실 목록 JSON 전달용 */
    @PostMapping("/rooms")
    @ResponseBody
    public List<RoomMaster> getAvailableRooms(@RequestBody Map<String, Object> params) {
        // onSiteReservationService랑 다른 데이터타입의 매개변수를 받는 이유는 숙박 연장에서 넘어갈 때 같이 넘어가는 데이터들이 달라서 입니당
        // onSite~에서는 ReservationDTO (고정적), extended~에서는 기존 체크아웃 ~ 신규 체크아웃 날짜, 기존 예약 번호
        LocalDate checkinDate = LocalDate.parse(params.get("checkinDate").toString());
        LocalDate checkoutDate = LocalDate.parse(params.get("checkoutDate").toString());
        int regPeople = Integer.parseInt(params.get("regPeople").toString());

        return onSiteReservationService.getAvailableRooms(checkinDate, checkoutDate, regPeople);
    }
}