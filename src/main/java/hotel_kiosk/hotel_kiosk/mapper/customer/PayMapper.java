package hotel_kiosk.hotel_kiosk.mapper.customer;

import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.dto.admin.RoomMasterDTO;
import hotel_kiosk.dto.customer.MembersPointDTO;
import hotel_kiosk.dto.customer.PaymentsDTO;
import hotel_kiosk.dto.customer.TossLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PayMapper {
    /* 회원 조회 (사용 가능 포인트 조회) */
    Members selectMemberByPhoneAndBirth(@Param("memberPhone") String memberPhone,
                         @Param("memberBirth") LocalDate memberBirth);
    /* 예약 idx 조회 */
    Long selectIdxByReservationId(@Param("reservationId") String reservationId);

    /* 결제 정보 저장 -> payments 테이블 */
    void insertPayment(PaymentsDTO paymentsDTO);

    /* 토스 로그 저장 -> toss_log 테이블 */
    void insertTossLog(TossLogDTO tossLogDTO);

    /* 예약 결제 상태 업데이트 */
    void updateReservationStatus(@Param("idx") Long idx, @Param("payStatus") String payStatus);

    /* 예약 SMS 발송 상태 업데이트 */
    void updateReservationSmsStatus(@Param("idx") Long idx, @Param("smsStatus") String smsStatus);

    /* 회원 포인트 차감 */
    void updateMemberPoint(@Param("memberPhone") String memberPhone,
                           @Param("memberBirth") LocalDate memberBirth,
                           @Param("pointAmount") int pointAmount);

    /* 멤버 조회 */
    Long selectMemberNoByPhone(@Param("memberPhone") String memberPhone);

    /* 기존 회원 포인트 적립 update */
    void updateMemberPointEarning(@Param("memberPhone") String memberPhone,
                                   @Param("memberBirth") LocalDate memberBirth,
                                   @Param("earnPoint") int earnPoint);

    /* 신규 회원 insert (포인트 적립용) */
    void insertMemberForEarn(@Param("memberPhone") String memberPhone,
                              @Param("memberBirth") LocalDate memberBirth,
                              @Param("memberName") String memberName,
                              @Param("earnPoint") int earnPoint);

    /* 포인트 내역 */
    void insertMemberPoint(MembersPointDTO membersPointDTO);

    /* 객실호실, 객실명 조회 */
    RoomMasterDTO selectRoomNameByReservationId(@Param("reservationId") String reservationId);

    /* 만료된 Waiting 예약 삭제 */
    int deleteWaitingReservations();
}