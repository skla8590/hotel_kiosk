package hotel_kiosk.hotel_kiosk.mapper.customer;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Payments;
import hotel_kiosk.domain.customer.Reservations;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OnSiteReservationMapper {
    /* 날짜 따른 예약 가능 객실 */
    /* ****주의!! 인원 조건은 자바스크립트에서 최종 체크 */
    List<RoomMaster> selectAvailableRooms(@Param("checkinDate") LocalDate checkinDate,
                                          @Param("checkoutDate") LocalDate checkoutDate,
                                          @Param("regPeople") int regPeople);

    /* 옵션 목록 조회 */
    List<OptionsMaster> selectAllOptions();

    /* 신규 회원 등록 */
    void insertNewMember(Members members);

    /* 전화번호로 회원 조회 */
    Members selectMemberByPhone(@Param("memberPhone") String memberPhone);

    /* 예약 내역 등록 */
    void insertReservations(Reservations reservations);

    /* 예약 임시 등록 (결제 전, pay_status = 'Waiting') */
    void insertReservationPending(Reservations reservations);

    /* idx로 reservation_id 조회 */
    String selectReservationIdByIdx(@Param("idx") Long idx);

    /* 결제 내역 등록 */
    void insertPayments(Payments payments);

    /* 예약 기간 객실 가격(payments > room_price) 조회 */
    int selectRoomPrice(@Param("checkinDate") LocalDate checkinDate,
                        @Param("roomNo") int roomNo);

    /* 예약 임식 등록 (여러 룸 선택, 결제 전) */
    void insertReservationsPending(Reservations reservations);
}
