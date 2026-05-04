package hotel_kiosk.hotel_kiosk.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String reservationId) {
        super("예약번호를 찾을 수 없습니다: " + reservationId);
    }
}
