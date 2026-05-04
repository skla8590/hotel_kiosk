package hotel_kiosk.hotel_kiosk.mapper.customer;

import hotel_kiosk.domain.admin.InformBoard;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    List<InformBoard> selectAllInformBoard();

    /* 예약 조회 */
    Reservations selectOneReservation(String reservationId);
    Members selectOneMember(Long memberNo);

    /* 체크인 처리 */
    void checkIn(@Param("reservationId") String reservationId,
                 @Param("parkingNum") String parkingNum);

    /* 재결제 - 객실 요금 조회 */
    int selectRoomPriceByReservationId(@Param("reservationId") String reservationId);

    /* 재결제 - 옵션 요금 합계 조회 */
    int selectOptionChargeByReservationId(@Param("reservationId") String reservationId);
}