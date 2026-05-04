package hotel_kiosk.hotel_kiosk.service.customer;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.AvailableRoomForExtendedDTO;
import hotel_kiosk.mapper.customer.ExtendedReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtendedReservationService {
    private final ExtendedReservationMapper extendedReservationMapper;

    public Reservations getReservationById(String reservationId) {
        return extendedReservationMapper.selectReservationById(reservationId);
    }

    public List<AvailableRoomForExtendedDTO> getReservationDetail(String reservationId) {
        return extendedReservationMapper.selectReservationDetail(reservationId);
    }

    public int getAvailableRoom(Reservations reservations) {
        return extendedReservationMapper.selectAvailableRoom(reservations);
    }

    public RoomMaster getRoomInfo(int roomNo) {
        return extendedReservationMapper.selectRoomInfo(roomNo);
    }

    public Members getMemberInfo(Long memberNo) {
        return extendedReservationMapper.selectMemberInfo(memberNo);
    }

    public int getPaymentAmount(String reservationId) { return extendedReservationMapper.selectPaymentAmount(reservationId); }

    @Transactional
    public void reviseCheckoutDate(String reservationId, LocalDate newCheckoutDate) {
        extendedReservationMapper.updateCheckoutDate(reservationId, newCheckoutDate);
    }
}
