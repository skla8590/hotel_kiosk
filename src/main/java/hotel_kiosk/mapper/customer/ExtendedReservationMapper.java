package hotel_kiosk.mapper.customer;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.customer.AvailableRoomForExtendedDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExtendedReservationMapper {
    /* 예약번호 마지막 6자리로 예약 조회 */
    Reservations selectReservationById(@Param("reservationId") String reservationId);

    /* '예약 정보의 객실번호 = 객실 테이블의 객실번호'인 객실 정보 들고 오기 */
    List<AvailableRoomForExtendedDTO> selectReservationDetail(@Param("reservationId") String reservationId);

    /* 해당 객실이 체크인, 체크아웃 날짜에 숙박이 가능한지 판단 */
    int selectAvailableRoom(@Param("reservations") Reservations reservations);

    /* 고객이 예약한 객실의 객실 정보(room_master) 들고 오기 */
    RoomMaster selectRoomInfo(@Param("roomNo") int roomNo);

    /* 예약 고객 정보(members) 불러오기 */
    Members selectMemberInfo(@Param("memberNo") Long memberNo);

    /* 추가 예약금 결제용 기존 결제 금액 불러오기 */
    int selectPaymentAmount(@Param("reservationId") String reservationId);

    /* 연장 처리된 예약 정보 update */
    void updateCheckoutDate(@Param("reservationId") String reservationId,
                            @Param("newCheckoutDate") LocalDate newCheckoutDate);
}
