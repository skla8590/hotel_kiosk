package hotel_kiosk.service.customer;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Payments;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.mapper.customer.OnSiteReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.ResourceResolver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnSiteReservationService {
    private final OnSiteReservationMapper onSiteReservationMapper;

    /* 날짜, 인원 따른 예약 가능 객실 */
    public List<RoomMaster> getAvailableRooms(LocalDate checkinDate, LocalDate checkoutDate, int regPeople) {
        return onSiteReservationMapper.selectAvailableRooms(checkinDate, checkoutDate, regPeople);
    }

    /* 옵션 조회 (객실 옵션 설정 페이지에 필요) */
    public List<OptionsMaster> getAllOptions() {
        return onSiteReservationMapper.selectAllOptions();
    }

    /* 결제 전 예약 임시 등록 (여러 룸, pay_status = 'Waiting') → reservationId 리스트 반환 */
    @Transactional
    public List<String> preRegisterReservation(Members members, Reservations reservations, List<Integer> roomNos) {
        // 1. 회원 등록 시도 (이미 존재하면 INSERT 무시됨)
        onSiteReservationMapper.insertNewMember(members);

        // 2. memberNo 확보
        Long memberNo = members.getMemberNo();
        if (memberNo == null) {
            Members existing = onSiteReservationMapper.selectMemberByPhone(members.getMemberPhone());
            if (existing != null) memberNo = existing.getMemberNo();
        }

        // 3. 룸마다 예약 임시 등록
        List<String> reservationIds = new ArrayList<>();

        for (Integer roomNo : roomNos) {
            Reservations res = new Reservations();
            res.setMemberNo(memberNo);
            res.setRoomNo(roomNo);
            res.setCheckinDate(reservations.getCheckinDate());
            res.setCheckoutDate(reservations.getCheckoutDate());
            res.setRegPeople(reservations.getRegPeople());
            res.setAddOption(reservations.getAddOption());

            // 4. 룸별 INSERT
            onSiteReservationMapper.insertReservationsPending(res);

            // 5. 트리거로 생성된 reservation_id를 idx로 조회해서 수집
            String reservationId = onSiteReservationMapper.selectReservationIdByIdx(res.getIdx());
            reservationIds.add(reservationId);
        }

        return reservationIds;
    }

    /******* 주의!!! 결제가 완료된 후에 실행되어야 함 *******/
    @Transactional
    public void processOnSiteReservation(Members members, Reservations reservations, Payments payments) {
        // **** 순서 고정 : 결제 완료 -> 회원이 아닐 경우 회원 등록 -> 예약 등록 -> 결제 등록 ****
        // 1. 회원이 아닐 경우 회원 등록
        onSiteReservationMapper.insertNewMember(members);

        // 2. 예약 내역 등록
        reservations.setMemberNo(members.getMemberNo());
        onSiteReservationMapper.insertReservations(reservations);

        // 3. 결제 내역 등록
        // 1) totalCharge = roomPrice + optionCharge - pointAmount
        int roomPrice = onSiteReservationMapper.selectRoomPrice(reservations.getCheckinDate(), reservations.getRoomNo());
        int optionCharge = payments.getOptionCharge();
        int pointAmount = payments.getPointAmount() != null ? payments.getPointAmount() : 0;
        int totalCharge = roomPrice + optionCharge - pointAmount;

        // 2) payments에 예약 번호, 객실 가격 정보 등록
        payments.setReservationId(reservations.getReservationId());
        payments.setRoomPrice(roomPrice);
        payments.setTotalCharge(totalCharge);

        // 3) 결제 내역 등록
        onSiteReservationMapper.insertPayments(payments);
    }
}
