package hotel_kiosk.hotel_kiosk.controller.admin;

import hotel_kiosk.domain.admin.RoomOptionsSummary;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.admin.PricePreviewDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.mapper.admin.ReservationReadMapper;
import hotel_kiosk.service.admin.ReservationReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin/reservation")
@Log4j2
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationReadService reservationReadService;
    private final ReservationReadMapper reservationReadMapper;

    @GetMapping
    public String reservation(PageRequestDTO pageRequestDTO, Model model) {
        log.info("reservation...");
        PageResponseDTO<ReservationsDTO> pageResponseDTO = reservationReadService.read(pageRequestDTO);

        model.addAttribute("reservations", pageResponseDTO);
        return "admin/operation/reservation/reservation";
    }

    @GetMapping("/{reservationId}")
    @ResponseBody
    public ReservationsDTO readOne(@PathVariable(required = false) String reservationId) {
        log.info("readOne...");

        ReservationsDTO reservationsDTO = reservationReadService.readByNum(reservationId);
        RoomOptionsSummary roomOptionsSummary = reservationReadMapper.findRoomOptions(reservationId);
        log.info(roomOptionsSummary);
        reservationsDTO.setOptions(roomOptionsSummary);
        log.info(reservationsDTO);
        return reservationsDTO;
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<ReservationsDTO>> searchCustomer(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String payStatus) {

        log.info("검색 keyword: " + keyword);
        log.info("검색 date: " + date);
        log.info("검색 status: " + payStatus);

        List<ReservationsDTO> result = reservationReadService.readAll();
        if (keyword != null && !keyword.isBlank()) {
            List<ReservationsDTO> byName = reservationReadService.readAllByName(keyword);
            List<ReservationsDTO> byId = reservationReadService.readALLByNum(keyword);

            result = Stream.concat(byName.stream(), byId.stream())
                    .distinct()
                    .toList();
        }

        // 4. 날짜 검색
        if (date != null && !date.isBlank()) {
            List<ReservationsDTO> byDate = reservationReadService.readByCheckIn(date);

            result = result.stream()
                    .filter(byDate::contains)
                    .toList();
        }

        // 5. 상태 검색
        if (payStatus != null && !payStatus.isBlank()) {
            List<ReservationsDTO> byPayStatus = reservationReadService.readByStatus(payStatus);

            result = result.stream()
                    .filter(byPayStatus::contains)
                    .toList();
        }

        int total = result.size();

        int start = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
        int end = Math.min(start + pageRequestDTO.getSize(), total);

        List<ReservationsDTO> pageList =
                (start >= total) ? List.of() : result.subList(start, end);

        PageResponseDTO<ReservationsDTO> finalResponse =
                PageResponseDTO.<ReservationsDTO>withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total(total)
                        .dtoList(pageList)
                        .build();

        return ResponseEntity.ok(finalResponse);
    }

    @PutMapping(value = "/{reservationId}")
    @ResponseBody
    public ReservationsDTO modify(@PathVariable String reservationId,
                                 @RequestBody ReservationsDTO reservationDTO) {
        log.info("Put modify....");
        log.info(reservationId);
        log.info(reservationDTO);
        log.info(reservationDTO.getPayStatus());
        if (Objects.equals(reservationDTO.getPayStatus(), "Refund")) {
            reservationDTO.setStatus("Cancelled");
        } else {
            reservationDTO.setStatus("Reserved");
        }

        log.info(reservationDTO.getStatus());

        reservationDTO.setReservationId(reservationId);

        reservationReadService.modify(reservationDTO);
        reservationReadService.modifyStatus(reservationId, reservationDTO.getStatus());
        reservationReadService.modifyPayStatus(reservationId, reservationDTO.getPayStatus());

        return reservationDTO;
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<Integer> getRooms() {
        return reservationReadService.getAllRoomNumbers();
    }

    @GetMapping("/list")
    @ResponseBody
    public PageResponseDTO<ReservationsDTO> getReservationList(PageRequestDTO pageRequestDTO) {
        return reservationReadService.read(pageRequestDTO);
    }

    /* 결제 취소 및 환불 승인 */
    @PutMapping("/{reservationId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String reservationId) {
        log.info("환불 처리 요청 - reservationId={}", reservationId);

        try {
            reservationReadService.processRefund(reservationId);

            log.info("환불 처리 완료 - reservationId={}", reservationId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "환불이 완료되었습니다."
            ));
        } catch (IllegalStateException e) {
            if ("ALREADY_CANCELED".equals(e.getMessage())) {
                log.warn("이미 취소된 예약 환불 요청 - reservationId={}", reservationId);
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "이미 취소된 예약입니다."
                ));
            }

            if ("INVALID_STATUS".equals(e.getMessage())) {
                log.warn("환불 불가 상태 - {}", reservationId);
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "체크인/체크아웃 상태는 환불할 수 없습니다."
                ));
            }

            log.error("환불 처리 중 비즈니스 오류 발생 - reservationId={}", reservationId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "환불 처리 중 오류가 발생했습니다."
            ));
        }
    }

    @PostMapping("/preview-price")
    @ResponseBody
    public int previewPrice(@RequestBody PricePreviewDTO dto) {

        int total = reservationReadService.calculatePreviewPrice(
                dto.getReservationId(),
                dto.getRoomNo(),
                dto.getCheckinDate(),
                dto.getCheckoutDate()
        );

        log.info(total);

        return total;
    }
}
