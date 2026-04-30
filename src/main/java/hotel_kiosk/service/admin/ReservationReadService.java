package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.RoomOptionsSummary;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationReadService {
    PageResponseDTO<ReservationsDTO> read(PageRequestDTO pageRequestDTO);
    List<ReservationsDTO> readAll();

    List<ReservationsDTO> readByCheckIn(String checkIn); // 체크인 날짜 검색
    List<ReservationsDTO> readByStatus(String status);   // 상태 검색
    ReservationsDTO readByNum(String reservationId);
    List<ReservationsDTO> readALLByNum(String reservationId);
    ReservationsDTO readByName(String name);
    List<ReservationsDTO> readAllByName(String name);

    void modify(ReservationsDTO reservationsDTO);
    void modifyStatus(String reservationId, String status);

    /* 결제 상태 수정 */
    void modifyPayStatus(String reservationId, String payStatus);

    /* 예약 Index 조회 */
    Long getReservationIdx(String reservationId);

    /* 포인트 환불 */
    void processRefund(String reservationId);

    List<Integer> getAllRoomNumbers();

    ReservationsDTO getRoomOptions(String reservationId);

    int calculatePreviewPrice(String reservationId, int roomNo, LocalDate checkin, LocalDate checkout);
}
