package hotel_kiosk.hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.RoomOptionsSummary;
import hotel_kiosk.domain.customer.Reservations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationReadMapper {
    /* 예약 목록 */
    List<Reservations> findAllReservation(@Param("size") int size, @Param("offset") int offset);
    List<Reservations> findReservation();
    int countReservation();

    /* 예약 조회 */
    List<Reservations> findReservationByCheckin(String checkIn);    // 체크인 날짜
    List<Reservations> findReservationByStatus(String payStatus);     // 상태
    Reservations findReservationByNum(String reservationId);        // 예약번호
    List<Reservations> findAllReservationByNum(String reservationId);        // 예약번호
    Reservations findReservationByName(String memberName);       // 고객명
    List<Reservations> findAllReservationByName(String memberName);       // 고객명

    /* 예약 수정 */
    void updateReservation(Reservations reservations);
    /* 예약 상태 수정 */
    void updateReservationStatus(String reservationId, String status);

    /* 결제 상태 수정 */
    void updatePayStatus(String reservationId, String payStatus);

    /* 예약 Index 조회 */
    Long findReservationIdx(String reservationId);

    List<Integer> findAllRoomNos();

    RoomOptionsSummary findRoomOptions(String reservationId);
}
