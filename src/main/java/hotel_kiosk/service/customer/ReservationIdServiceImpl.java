package hotel_kiosk.service.customer;

import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.mapper.customer.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationIdServiceImpl implements ReservationIdService {
    private final CustomerMapper customerMapper;
    private final ModelMapper modelMapper;

    @Override
    public ReservationsDTO getOneReservation(String reservationId) {
        Reservations reservations = customerMapper.selectOneReservation(reservationId);
        log.info("예약 조회 결과: {}", reservations);

        if (reservations == null) {
            log.info("예약 없음: {}", reservationId);
            return null;
        }

        Members members = customerMapper.selectOneMember(reservations.getMemberNo());
        log.info("회원 조회 결과: {}", members);

        Reservations build = Reservations.builder()
                .reservationId(reservations.getReservationId())
                .roomNo(reservations.getRoomNo())
                .memberNo(reservations.getMemberNo())
                .status(reservations.getStatus())
                .checkinDate(reservations.getCheckinDate())
                .checkoutDate(reservations.getCheckoutDate())
                .checkInAt(reservations.getCheckInAt())
                .checkOutAt(reservations.getCheckOutAt())
                .regPeople(reservations.getRegPeople())
                .parkingNum(reservations.getParkingNum())
                .addOption(reservations.getAddOption())
                .payStatus(reservations.getPayStatus())
                .smsStatus(reservations.getSmsStatus())
                .memberName(members.getMemberName())
                .memberPhone(members.getMemberPhone())
                .roomName(reservations.getRoomName())
                .build();

        return modelMapper.map(build, ReservationsDTO.class);
    }

    @Override
    public void checkIn(String reservationId, String parkingNum) {
        customerMapper.checkIn(reservationId, parkingNum);
    }

    @Override
    public Map<String, Object> getRepayInfo(String reservationId) {
        Reservations reservation = customerMapper.selectOneReservation(reservationId);
        Members member = customerMapper.selectOneMember(reservation.getMemberNo());

        int roomPrice    = customerMapper.selectRoomPriceByReservationId(reservationId);
        int optionCharge = customerMapper.selectOptionChargeByReservationId(reservationId);

        long nights = ChronoUnit.DAYS.between(reservation.getCheckinDate(), reservation.getCheckoutDate());
        int basePrice = nights > 0 ? roomPrice / (int) nights : roomPrice;

        Map<String, Object> roomObj = new HashMap<>();
        roomObj.put("basePrice", basePrice);

        // optionCharge가 있으면 qty=1, optionPrice=optionCharge 로 가상 옵션 구성
        // pay.html renderPayRoomInfo()에서 optionsQty[id].qty * optionPrice 로 계산하기 때문
        Map<String, Object> optionsQty = new HashMap<>();
        if (optionCharge > 0) {
            Map<String, Object> optionEntry = new HashMap<>();
            optionEntry.put("qty",         1);
            optionEntry.put("optionPrice", optionCharge);
            optionEntry.put("optionName",  "옵션 합계");
            optionsQty.put("0", optionEntry);
        }

        Map<String, Object> selectedRoom = new HashMap<>();
        selectedRoom.put("room",       roomObj);
        selectedRoom.put("optionsQty", optionsQty);

        Map<String, Object> guestInfo = new HashMap<>();
        guestInfo.put("guestName",  member.getMemberName());
        guestInfo.put("guestPhone", member.getMemberPhone());
        guestInfo.put("guestBirth", member.getMemberBirth().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("selectedRooms", List.of(selectedRoom));
        result.put("roomPrice",     roomPrice);
        result.put("optionCharge",  optionCharge);
        result.put("checkinDate",   reservation.getCheckinDate().toString());
        result.put("checkoutDate",  reservation.getCheckoutDate().toString());
        result.put("guestInfo",     guestInfo);

        log.info("재결제 정보 조회 완료 - reservationId={}, roomPrice={}, optionCharge={}",
                reservationId, roomPrice, optionCharge);

        return result;
    }
}