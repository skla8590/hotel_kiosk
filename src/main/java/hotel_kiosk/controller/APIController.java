package hotel_kiosk.controller;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.AvailableRoomForExtendedDTO;
import hotel_kiosk.dto.customer.PreReserveRequestDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.dto.customer.RoomSearchRequestDTO;
import hotel_kiosk.service.customer.ExtendedReservationService;
import hotel_kiosk.service.customer.OnSiteReservationService;
import hotel_kiosk.service.customer.PayService;
import hotel_kiosk.service.customer.ReservationIdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 고객 키오스크 화면 전용 REST API 명세서 (Swagger)

 * 실제 로직은 customer 패키지의 각 Controller에 구현되어 있으며,
 * 본 파일은 Swagger API 문서화를 위한 명세 전용 Controller입니다.

 * - CheckInController          → /JHotel/checkin
 * - OnSiteReservationController → /JHotel/onsite
 * - ExtendedReservationController → /JHotel/extended
 * - PayController              → /JHotel/pay
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kiosk")
@Tag(name = "고객 키오스크 API", description = "고객 키오스크 화면에서 사용하는 REST API 명세서")
public class APIController {
    private final ReservationIdService reservationIdService;
    private final OnSiteReservationService onSiteReservationService;
    private final ExtendedReservationService extendedReservationService;
    private final PayService payService;

    /* ══════════════════════════════════════════════════
       1. 체크인 - CheckInController (/JHotel/checkin)
    ══════════════════════════════════════════════════ */
    @Operation(
            summary = "예약 조회",
            description = "예약번호(reservationId)를 입력해 예약 정보를 조회합니다.\n\n" +
                    "실제 처리: `GET /JHotel/checkin/search?reservationId={reservationId}`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationsDTO.class))),
            @ApiResponse(responseCode = "404", description = "예약 정보 없음")
    })
    @GetMapping("/checkin/search")
    public ResponseEntity<ReservationsDTO> searchReservation(
            @Parameter(description = "예약번호", required = true, example = "RES20250001")
            @RequestParam String reservationId) {

        log.info("[API명세] 예약 조회 - reservationId={}", reservationId);
        ReservationsDTO result = reservationIdService.getOneReservation(reservationId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "재결제 정보 조회",
            description = "미결제 상태의 예약에 대해 재결제에 필요한 금액 정보를 조회합니다.\n\n" +
                    "실제 처리: `GET /JHotel/checkin/repay-info?reservationId={reservationId}`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재결제 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "예약 정보 없음")
    })
    @GetMapping("/checkin/repay-info")
    public ResponseEntity<Map<String, Object>> getRepayInfo(
            @Parameter(description = "예약번호", required = true, example = "RES20250001")
            @RequestParam String reservationId) {

        log.info("[API명세] 재결제 정보 조회 - reservationId={}", reservationId);
        Map<String, Object> data = reservationIdService.getRepayInfo(reservationId);
        return ResponseEntity.ok(data);
    }

    @Operation(
            summary = "체크인 처리",
            description = "예약번호와 주차 번호를 받아 체크인을 완료 처리합니다.\n\n" +
                    "실제 처리: `POST /JHotel/checkin/do`\n\n" +
                    "| 파라미터 | 필수 | 설명 |\n|---|---|---|\n" +
                    "| reservationId | ✅ | 예약번호 |\n" +
                    "| parkingNum | ❌ | 주차 번호 (없으면 빈 문자열) |"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "체크인 성공 (\"success\" 반환)"),
            @ApiResponse(responseCode = "400", description = "체크인 불가 (날짜 불일치, 이미 체크인 등)")
    })
    @PostMapping("/checkin/do")
    public ResponseEntity<String> doCheckIn(
            @Parameter(description = "예약번호", required = true, example = "RES20250001")
            @RequestParam String reservationId,
            @Parameter(description = "주차 번호 (없으면 빈 문자열)", example = "A-12")
            @RequestParam(required = false, defaultValue = "") String parkingNum) {

        log.info("[API명세] 체크인 처리 - reservationId={}, parkingNum={}", reservationId, parkingNum);
        reservationIdService.checkIn(reservationId, parkingNum);
        return ResponseEntity.ok("success");
    }


    /* ══════════════════════════════════════════════════
       2. 현장 예약 - OnSiteReservationController (/JHotel/onsite)
    ══════════════════════════════════════════════════ */

    @Operation(
            summary = "예약 가능 객실 조회 (현장 예약)",
            description = "체크인 날짜, 체크아웃 날짜, 인원수 조건으로 예약 가능한 객실 목록을 조회합니다.\n\n" +
                    "실제 처리: `POST /JHotel/onsite/rooms`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoomMaster.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜 또는 인원 입력")
    })
    @PostMapping("/onsite/rooms")
    public ResponseEntity<List<RoomMaster>> getAvailableRooms(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "객실 검색 조건",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RoomSearchRequestDTO.class))
            )
            @RequestBody RoomSearchRequestDTO requestDTO) {

        log.info("[API명세] 예약 가능 객실 조회 - checkin={}, checkout={}, people={}",
                requestDTO.getCheckinDate(), requestDTO.getCheckoutDate(), requestDTO.getRegPeople());
        List<RoomMaster> rooms = onSiteReservationService.getAvailableRooms(
                requestDTO.getCheckinDate(),
                requestDTO.getCheckoutDate(),
                requestDTO.getRegPeople());
        return ResponseEntity.ok(rooms);
    }

    @Operation(
            summary = "객실 옵션 목록 조회",
            description = "객실 예약 시 추가할 수 있는 옵션 목록 전체를 조회합니다.\n\n" +
                    "실제 처리: `GET /JHotel/onsite/api/options`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OptionsMaster.class))))
    })
    @GetMapping("/onsite/options")
    public ResponseEntity<List<OptionsMaster>> getOptions() {
        log.info("[API명세] 객실 옵션 목록 조회");
        return ResponseEntity.ok(onSiteReservationService.getAllOptions());
    }

    @Operation(
            summary = "예약 임시 등록 (결제 전)",
            description = "Toss 결제 전, 예약 정보를 임시로 저장하고 reservationId를 반환합니다.\n" +
                    "반환된 reservationId를 결제 요청 시 함께 전송해야 합니다.\n\n" +
                    "실제 처리: `POST /JHotel/onsite/pre-reserve`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 예약 성공 - reservationId 반환",
                    content = @Content(schema = @Schema(example = "{\"reservationId\": \"RES20250001\"}")))
    })
    @PostMapping("/onsite/pre-reserve")
    public ResponseEntity<Map<String, List<String>>> preReserve(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "임시 예약 요청 정보 (고객 정보 + 객실 정보)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PreReserveRequestDTO.class))
            )
            @RequestBody PreReserveRequestDTO dto) {

        log.info("[API명세] 임시 예약 등록 - memberPhone={}, roomNo={}", dto.getMemberPhone(), dto.getRoomNos());

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

        List<String> reservationId = onSiteReservationService.preRegisterReservation(members, reservations, dto.getRoomNos());
        return ResponseEntity.ok(Map.of("reservationId", reservationId));
    }


    /* ══════════════════════════════════════════════════
       3. 숙박 연장 - ExtendedReservationController (/JHotel/extended)
    ══════════════════════════════════════════════════ */

    @Operation(
            summary = "기존 예약 정보 조회 (숙박 연장)",
            description = "예약번호로 기존 체크인 예약 정보를 조회합니다. 숙박 연장 시 사용합니다.\n\n" +
                    "실제 처리: `POST /JHotel/extended/check_info`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = Reservations.class))),
            @ApiResponse(responseCode = "200", description = "예약 정보 없음 (빈 응답)")
    })
    @PostMapping("/extended/check-info")
    public ResponseEntity<Reservations> getExtendedCheckInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약번호",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"reservationId\": \"RES20250001\"}"))
            )
            @RequestBody Map<String, String> request) {

        log.info("[API명세] 숙박 연장 - 예약 정보 조회 - reservationId={}", request.get("reservationId"));
        Reservations reservation = extendedReservationService.getReservationById(request.get("reservationId"));
        if (reservation == null) return ResponseEntity.ok().build();
        return ResponseEntity.ok(reservation);
    }

    @Operation(
            summary = "연장 가능 객실 상세 조회",
            description = "기존 예약의 연장 가능 여부와 사용 가능한 객실 목록을 조회합니다.\n\n" +
                    "실제 처리: `POST /JHotel/extended/available`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연장 가능 객실 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AvailableRoomForExtendedDTO.class)))),
            @ApiResponse(responseCode = "200", description = "연장 가능 객실 없음 (빈 응답)")
    })
    @PostMapping("/extended/available")
    public ResponseEntity<List<AvailableRoomForExtendedDTO>> getExtendedAvailable(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약번호",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"reservationId\": \"RES20250001\"}"))
            )
            @RequestBody Map<String, String> request) {

        log.info("[API명세] 연장 가능 객실 조회 - reservationId={}", request.get("reservationId"));
        List<AvailableRoomForExtendedDTO> detail = extendedReservationService.getReservationDetail(request.get("reservationId"));
        if (detail == null || detail.isEmpty()) return ResponseEntity.ok().build();
        return ResponseEntity.ok(detail);
    }

    @Operation(
            summary = "숙박 연장 가능 여부 확인 및 정보 조회",
            description = "체크아웃 날짜 변경 시, 해당 객실의 연장 가능 여부를 확인하고 회원 및 객실 정보를 함께 반환합니다.\n\n" +
                    "실제 처리: `POST /JHotel/extended/set_checkoutDate`\n\n" +
                    "| 파라미터 | 타입 | 설명 |\n|---|---|---|\n" +
                    "| reservationId | String | 예약번호 |\n" +
                    "| roomNo | Integer | 객실 번호 |\n" +
                    "| checkinDate | String | 기존 체크인 날짜 (yyyy-MM-dd) |\n" +
                    "| checkoutDate | String | 변경할 체크아웃 날짜 (yyyy-MM-dd) |\n" +
                    "| status | String | 예약 상태 |\n" +
                    "| memberNo | Long | 회원 번호 |"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연장 가능 여부 확인 완료",
                    content = @Content(schema = @Schema(example = "{\"count\": 0, \"roomInfo\": {}, \"memberInfo\": {}}")))
    })
    @PostMapping("/extended/set-checkout-date")
    public ResponseEntity<Map<String, Object>> checkExtendAvailability(
            @RequestBody Map<String, Object> params) {

        log.info("[API명세] 연장 가능 여부 확인 - reservationId={}", params.get("reservationId"));

        String reservationId = (String) params.get("reservationId");
        Integer roomNo = (Integer) params.get("roomNo");
        LocalDate checkinDate = LocalDate.parse((String) params.get("checkinDate"));
        LocalDate checkoutDate = LocalDate.parse((String) params.get("checkoutDate"));
        String status = (String) params.get("status");
        Long memberNo = Long.parseLong(params.get("memberNo").toString());

        Reservations reservation = Reservations.builder()
                .reservationId(reservationId)
                .roomNo(roomNo)
                .memberNo(memberNo)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .status(status)
                .build();

        int count = extendedReservationService.getAvailableRoom(reservation);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("count", count);
        response.put("memberInfo", extendedReservationService.getMemberInfo(memberNo));
        if (count == 0) {
            response.put("roomInfo", extendedReservationService.getRoomInfo(roomNo));
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "예약 가능 객실 조회 (숙박 연장 - 객실 변경)",
            description = "숙박 연장 시 다른 객실로 변경하는 경우, 기존 체크아웃 날짜부터 새 체크아웃 날짜 사이에 예약 가능한 객실 목록을 조회합니다.\n\n" +
                    "실제 처리: `POST /JHotel/extended/rooms`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 가능 객실 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoomMaster.class))))
    })
    @PostMapping("/extended/rooms")
    public ResponseEntity<List<RoomMaster>> getExtendedAvailableRooms(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "기존 체크아웃 날짜 ~ 신규 체크아웃 날짜 + 인원",
                    content = @Content(schema = @Schema(
                            example = "{\"checkinDate\": \"2025-05-03\", \"checkoutDate\": \"2025-05-05\", \"regPeople\": 2}"))
            )
            @RequestBody Map<String, Object> params) {

        LocalDate checkinDate = LocalDate.parse(params.get("checkinDate").toString());
        LocalDate checkoutDate = LocalDate.parse(params.get("checkoutDate").toString());
        int regPeople = Integer.parseInt(params.get("regPeople").toString());

        List<RoomMaster> rooms = onSiteReservationService.getAvailableRooms(checkinDate, checkoutDate, regPeople);
        return ResponseEntity.ok(rooms);
    }


    /* ══════════════════════════════════════════════════
       4. 결제 - PayController (/JHotel/pay)
    ══════════════════════════════════════════════════ */

    @Operation(
            summary = "회원 정보 조회 (포인트 사용 확인)",
            description = "결제 화면에서 휴대폰 번호 + 생년월일로 회원을 조회합니다.\n" +
                    "조회 성공 시 회원의 포인트 잔액 등을 확인할 수 있습니다.\n\n" +
                    "실제 처리: `POST /JHotel/pay`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 조회 성공",
                    content = @Content(schema = @Schema(implementation = Members.class))),
            @ApiResponse(responseCode = "200", description = "미등록 회원 (빈 응답)")
    })
    @PostMapping("/pay/member")
    public ResponseEntity<Members> getMemberForPay(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 조회 정보",
                    required = true,
                    content = @Content(schema = @Schema(
                            example = "{\"memberPhone\": \"01012345678\", \"memberBirth\": \"1990-01-01\"}"))
            )
            @RequestBody Map<String, String> request) {

        log.info("[API명세] 결제 - 회원 조회 - phone={}", request.get("memberPhone"));
        String memberPhone = request.get("memberPhone");
        LocalDate memberBirth = LocalDate.parse(request.get("memberBirth"));
        Members member = payService.getMemberByPhoneAndBirth(memberPhone, memberBirth);
        if (member == null) return ResponseEntity.ok().build();
        return ResponseEntity.ok(member);
    }

    @Operation(
            summary = "Toss 결제 성공 처리",
            description = "Toss Payments 결제 성공 후 서버에서 결제를 최종 승인하고 예약을 확정합니다.\n" +
                    "숙박 연장(paySource=extended)인 경우 체크아웃 날짜도 함께 업데이트됩니다.\n" +
                    "처리 완료 후 `/JHotel/pay?payResult=complete`로 리다이렉트됩니다.\n\n" +
                    "실제 처리: `GET /JHotel/pay/toss/success`\n\n" +
                    "| 파라미터 | 필수 | 설명 |\n|---|---|---|\n" +
                    "| paymentKey | ✅ | Toss 결제 키 |\n" +
                    "| orderId | ✅ | 주문 ID |\n" +
                    "| amount | ✅ | 결제 금액 |\n" +
                    "| reservationId | ✅ | 예약번호 |\n" +
                    "| roomPrice | ✅ | 객실 금액 |\n" +
                    "| optionCharge | ✅ | 옵션 추가 금액 |\n" +
                    "| pointAmount | ✅ | 포인트 사용 금액 |\n" +
                    "| memberPhone | ❌ | 회원 휴대폰 번호 |\n" +
                    "| paySource | ❌ | 결제 경로 (onsite / extended) |\n" +
                    "| newCheckoutDate | ❌ | 연장 체크아웃 날짜 (연장 시 필수) |"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "결제 완료 → /JHotel/pay?payResult=complete 리다이렉트"),
            @ApiResponse(responseCode = "302", description = "결제 실패 → /JHotel/pay?payResult=fail 리다이렉트")
    })
    @GetMapping("/pay/toss/success")
    public ResponseEntity<Void> tossSuccess(
            @Parameter(description = "Toss 결제 키", required = true) @RequestParam String paymentKey,
            @Parameter(description = "주문 ID", required = true) @RequestParam String orderId,
            @Parameter(description = "결제 금액", required = true) @RequestParam Long amount,
            @Parameter(description = "예약번호", required = true) @RequestParam String reservationId,
            @Parameter(description = "객실 금액", required = true) @RequestParam int roomPrice,
            @Parameter(description = "옵션 추가 금액", required = true) @RequestParam int optionCharge,
            @Parameter(description = "포인트 사용 금액", required = true) @RequestParam int pointAmount,
            @Parameter(description = "회원 휴대폰 번호") @RequestParam(required = false, defaultValue = "") String memberPhone,
            @Parameter(description = "결제 경로 (onsite / extended)") @RequestParam(required = false) String paySource,
            @Parameter(description = "연장 체크아웃 날짜 (연장 시 필수, yyyy-MM-dd)") @RequestParam(required = false) LocalDate newCheckoutDate) {

        log.info("[API명세] Toss 결제 성공 - orderId={}, amount={}", orderId, amount);
        // 실제 처리는 PayController.tossSuccess()에서 수행
        return ResponseEntity.status(302).build();
    }

    @Operation(
            summary = "Toss 결제 실패 처리",
            description = "Toss Payments 결제 실패 시 호출됩니다.\n" +
                    "처리 완료 후 `/JHotel/pay?payResult=fail`로 리다이렉트됩니다.\n\n" +
                    "실제 처리: `GET /JHotel/pay/toss/fail`"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "결제 실패 → /JHotel/pay?payResult=fail 리다이렉트")
    })
    @GetMapping("/pay/toss/fail")
    public ResponseEntity<Void> tossFail(
            @Parameter(description = "Toss 오류 코드") @RequestParam(required = false) String code,
            @Parameter(description = "Toss 오류 메시지") @RequestParam(required = false) String message) {

        log.info("[API명세] Toss 결제 실패 - code={}, message={}", code, message);
        return ResponseEntity.status(302).build();
    }

    @Operation(
            summary = "카드 결제 처리 (현장 카드)",
            description = "Toss 외 현장 카드 단말기 결제를 처리합니다.\n" +
                    "숙박 연장(paySource=extended)인 경우 체크아웃 날짜도 함께 업데이트됩니다.\n\n" +
                    "실제 처리: `POST /JHotel/pay/card`\n\n" +
                    "| 파라미터 | 필수 | 설명 |\n|---|---|---|\n" +
                    "| reservationId | ✅ | 예약번호 |\n" +
                    "| roomPrice | ✅ | 객실 금액 |\n" +
                    "| optionCharge | ✅ | 옵션 추가 금액 |\n" +
                    "| pointAmount | ✅ | 포인트 사용 금액 |\n" +
                    "| totalCharge | ✅ | 최종 결제 금액 |\n" +
                    "| memberPhone | ❌ | 회원 휴대폰 번호 |\n" +
                    "| paySource | ❌ | 결제 경로 (onsite / extended) |\n" +
                    "| newCheckoutDate | ❌ | 연장 체크아웃 날짜 |"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카드 결제 성공 (\"success\" 반환)"),
            @ApiResponse(responseCode = "500", description = "결제 처리 오류")
    })
    @PostMapping("/pay/card")
    public ResponseEntity<String> cardPay(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카드 결제 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(example =
                            "{\"reservationId\": \"RES20250001\", \"roomPrice\": 100000, " +
                                    "\"optionCharge\": 10000, \"pointAmount\": 5000, \"totalCharge\": 105000, " +
                                    "\"memberPhone\": \"01012345678\", \"paySource\": \"onsite\"}"))
            )
            @RequestBody Map<String, Object> request) {

        log.info("[API명세] 카드 결제 - reservationId={}", request.get("reservationId"));
        // 실제 처리는 PayController.cardPay()에서 수행
        return ResponseEntity.ok("success");
    }
}
