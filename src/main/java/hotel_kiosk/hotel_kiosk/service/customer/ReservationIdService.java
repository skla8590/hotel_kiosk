package hotel_kiosk.hotel_kiosk.service.customer;

import hotel_kiosk.dto.customer.ReservationsDTO;

import java.util.Map;

public interface ReservationIdService {
    ReservationsDTO getOneReservation(String reservationId);

    /* 체크인 처리 */
    void checkIn(String reservationId, String parkingNum);

    /* 재결제 정보 조회 */
    Map<String, Object> getRepayInfo(String reservationId);
}