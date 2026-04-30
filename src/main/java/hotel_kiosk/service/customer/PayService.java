package hotel_kiosk.service.customer;

import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.dto.admin.RoomMasterDTO;
import hotel_kiosk.dto.customer.MembersPointDTO;
import hotel_kiosk.dto.customer.PaymentsDTO;
import hotel_kiosk.dto.customer.TossLogDTO;
import hotel_kiosk.mapper.customer.PayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PayService {
    private final PayMapper payMapper;
    private final SmsService smsService;

    @Value("${my.toss.widgetSecretKey}")
    private String tossSecretKey;

    /* 회원 조회 */
    public Members getMemberByPhoneAndBirth(String memberPhone, LocalDate memberBirth) {
        return payMapper.selectMemberByPhoneAndBirth(memberPhone, memberBirth);
    }

    /* 토스 결제 */
    public void confirmTossPayment(String paymentKey, String orderId, Long amount, String reservationId, int roomPrice,
                                   int optionCharge, int pointAmount, String memberPhone,
                                   LocalDate memberBirth, String memberName) {
        String encoded = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm", new HttpEntity<>(body, headers), Map.class
            );
        } catch (Exception e) {
            log.error("토스 승인 실패: {}", e.getMessage());
            throw new RuntimeException("토스 결제 승인 실패: " + e.getMessage());
        }

        Map<String, Object> tossResult = response.getBody();
        log.info("토스 승인 응답: {}", tossResult);

        // 여러 reservationId 처리 (콤마로 구분된 문자열 split)
        String[] reservationIdArr = reservationId.split(",");
        Long firstIdx = null;

        for (String resId : reservationIdArr) {
            resId = resId.trim();

            // 룸별 정보 조회
            RoomMasterDTO roomInfo = payMapper.selectRoomNameByReservationId(resId);
            Integer roomNo = roomInfo.getRoomNo();
            String roomName = roomInfo.getRoomName();

            // 룸별 SMS 발송
            String smsStatus = sendPaymentSms(memberPhone, resId, roomName, "페이",
                    roomPrice, optionCharge, pointAmount, amount.intValue(), roomNo);

            PaymentsDTO paymentsDTO = PaymentsDTO.builder()
                    .reservationId(resId)
                    .payMethod("pay")
                    .roomPrice(roomPrice)
                    .pointAmount(pointAmount)
                    .optionCharge(optionCharge)
                    .totalCharge(amount.intValue())
                    .smsStatus(smsStatus)
                    .payStatus("Success")
                    .tossKey(paymentKey)
                    .build();

            payMapper.insertPayment(paymentsDTO);
            log.info("payments INSERT 완료, reservationId={}, paymentId={}", resId, paymentsDTO.getPaymentId());

            TossLogDTO tossLogDTO = TossLogDTO.builder()
                    .paymentId(paymentsDTO.getPaymentId())
                    .paymentKey(paymentKey)
                    .resultCode("Success")
                    .resultMessage("Success")
                    .logContent(tossResult != null ? tossResult.toString() : "")
                    .build();

            payMapper.insertTossLog(tossLogDTO);

            Long idx = payMapper.selectIdxByReservationId(resId);
            payMapper.updateReservationStatus(idx, "Success");
            payMapper.updateReservationSmsStatus(idx, smsStatus);
            log.info("예약 idx={} pay_status 업데이트 완료", idx);

            if (firstIdx == null) firstIdx = idx;
        }

        if (pointAmount > 0 && memberPhone != null && !memberPhone.isBlank() && memberBirth != null) {
            payMapper.updateMemberPoint(memberPhone, memberBirth, pointAmount);
            log.info("포인트 차감 완료 - memberPhone={}, pointAmount={}", memberPhone, pointAmount);

            Members member = payMapper.selectMemberByPhoneAndBirth(memberPhone, memberBirth);
            Long memberNo = member != null ? member.getMemberNo() : null;
            MembersPointDTO membersPointDTO = MembersPointDTO.builder()
                    .memberNo(memberNo)
                    .idx(firstIdx)
                    .earning(0)
                    .usingPoint(pointAmount)
                    .build();
            payMapper.insertMemberPoint(membersPointDTO);
        }

        // 포인트 적립 처리
        int earnPoint = (int)(amount.intValue() * 0.01);
        earnMemberPoint(memberPhone, memberBirth, memberName, earnPoint, firstIdx);
    }

    /* 카드 결제 */
    public void cardPayment(String reservationId, int roomPrice, int optionCharge,
                            int pointAmount, int totalCharge, String memberPhone,
                            LocalDate memberBirth, String memberName) {
        // 여러 reservationId 처리 (콤마로 구분된 문자열 split)
        String[] reservationIdArr = reservationId.split(",");
        Long firstIdx = null;

        for (String resId : reservationIdArr) {
            resId = resId.trim();

            // 룸별 정보 조회
            RoomMasterDTO roomInfo = payMapper.selectRoomNameByReservationId(resId);
            Integer roomNo = roomInfo.getRoomNo();
            String roomName = roomInfo.getRoomName();

            // 룸별 SMS 발송
            String smsStatus = sendPaymentSms(memberPhone, resId, roomName, "카드",
                    roomPrice, optionCharge, pointAmount, totalCharge, roomNo);

            PaymentsDTO paymentsDTO = PaymentsDTO.builder()
                    .reservationId(resId)
                    .payMethod("card")
                    .approvalNo(resId)
                    .roomPrice(roomPrice)
                    .optionCharge(optionCharge)
                    .pointAmount(pointAmount)
                    .totalCharge(totalCharge)
                    .smsStatus(smsStatus)
                    .payStatus("Success")
                    .tossKey(null)
                    .build();

            payMapper.insertPayment(paymentsDTO);

            Long idx = payMapper.selectIdxByReservationId(resId);
            payMapper.updateReservationStatus(idx, "Success");
            payMapper.updateReservationSmsStatus(idx, smsStatus);

            if (firstIdx == null) firstIdx = idx;
        }

        if (pointAmount > 0 && memberPhone != null && !memberPhone.isBlank() && memberBirth != null) {
            payMapper.updateMemberPoint(memberPhone, memberBirth, pointAmount);

            Members member = payMapper.selectMemberByPhoneAndBirth(memberPhone, memberBirth);
            Long memberNo = member != null ? member.getMemberNo() : null;
            MembersPointDTO membersPointDTO = MembersPointDTO.builder()
                    .memberNo(memberNo)
                    .idx(firstIdx)
                    .earning(0)
                    .usingPoint(pointAmount)
                    .build();
            payMapper.insertMemberPoint(membersPointDTO);
        }

        // 포인트 적립 처리
        int earnPoint = (int)(totalCharge * 0.01);
        earnMemberPoint(memberPhone, memberBirth, memberName, earnPoint, firstIdx);
    }

    /* 포인트 적립 처리 (전화번호+생년월일 일치 -> update, 없으면 -> insert) */
    private void earnMemberPoint(String memberPhone, LocalDate memberBirth,
                                  String memberName, int earnPoint, Long idx) {
        if (memberPhone == null || memberPhone.isBlank() || memberBirth == null) {
            log.info("전화번호 또는 생년월일 없음");
            return;
        }

        // 기존 메서드 재활용
        Members existing = payMapper.selectMemberByPhoneAndBirth(memberPhone, memberBirth);

        Long memberNo;
        if (existing != null) {
            // 기존 회원 -> 포인트 update
            payMapper.updateMemberPointEarning(memberPhone, memberBirth, earnPoint);
            memberNo = existing.getMemberNo();
            log.info("포인트 적립 UPDATE - memberPhone={}, earnPoint={}", memberPhone, earnPoint);
        } else {
            // 신규 -> members insert
            payMapper.insertMemberForEarn(memberPhone, memberBirth, memberName, earnPoint);
            // insert 후 정확한 memberNo를 phone+birth로 재조회
            Members inserted = payMapper.selectMemberByPhoneAndBirth(memberPhone, memberBirth);
            memberNo = inserted != null ? inserted.getMemberNo() : null;
            log.info("신규 회원 INSERT + 포인트 적립 - memberPhone={}, earnPoint={}", memberPhone, earnPoint);
        }

        // 포인트 내역 기록 -> members_point 테이블에 insert
        MembersPointDTO membersPointDTO = MembersPointDTO.builder()
                .memberNo(memberNo)
                .idx(idx)
                .earning(earnPoint)
                .usingPoint(0)
                .build();
        payMapper.insertMemberPoint(membersPointDTO);
    }

    /* SMS 발송 메서드 */
    private String sendPaymentSms(String memberPhone, String reservationId, String roomName, String payMethod, int roomPrice, int optionCharge,
                                   int pointAmount, int totalCharge, int roomNo) {
        if (memberPhone == null || memberPhone.isBlank()) {
            log.info("SMS 발송 스킵 - 전화번호 없음 (비회원)");
            return "Failed";
        }
        String content = String.format(
                "[J호텔 예약 완료]\n" +
                        "예약번호: %s\n" +
                        "객실: %S호\n" +
//                        "%s\n" +
//                        "결제 수단: %s\n" +
//                        "객실 요금: %s\n" +
//                        "옵션 요금: %s\n" +
//                        "할인 금액: %s\n" +
//                        "결제금액: %,d원\n" +
                        "이용해 주셔서 감사합니다.",
                reservationId,  roomNo /*, roomName, payMethod, roomPrice, optionCharge, pointAmount,  totalCharge */
        );
        boolean success = smsService.sendSms(memberPhone, content);
        log.info("SMS 발송 결과 - to: {}, success: {}", memberPhone, success);
        return success ? "Success" : "Failed";
    }
}
