package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.RoomOptionsSummary;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.mapper.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationReadServiceImpl implements ReservationReadService {
    private final ReservationReadMapper reservationReadMapper;
    private final MembersPointMapper membersPointMapper;
    private final MemberReadMapper membersMapper;
    private final ModelMapper modelMapper;
    private final RoomMapper roomMapper;
    private final OptionMapper optionMapper;

    @Override
    public PageResponseDTO<ReservationsDTO> read(PageRequestDTO pageRequestDTO) {
        int offset = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
        List<Reservations> reservationsList = reservationReadMapper.findAllReservation(pageRequestDTO.getSize(), offset);
        int total = reservationReadMapper.countReservation();
        List<ReservationsDTO> dtoList = reservationsList.stream()
                .map(res -> modelMapper.map(res, ReservationsDTO.class)).toList();
        return PageResponseDTO.<ReservationsDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .dtoList(dtoList)
                .build();
    }

    @Override
    public List<ReservationsDTO> readAll() {
        List<Reservations> reservationsList = reservationReadMapper.findReservation();
        List<ReservationsDTO> dtoList = new ArrayList<>();
        for (Reservations reservations : reservationsList) {
            dtoList.add(modelMapper.map(reservations, ReservationsDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<ReservationsDTO> readByCheckIn(String checkIn) {
        List<Reservations> reservationsList = reservationReadMapper.findReservationByCheckin(checkIn);
        List<ReservationsDTO> dtoList = new ArrayList<>();
        for (Reservations reservations : reservationsList) {
            dtoList.add(modelMapper.map(reservations, ReservationsDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<ReservationsDTO> readByStatus(String status) {
        List<Reservations> reservationsList = reservationReadMapper.findReservationByStatus(status);
        List<ReservationsDTO> dtoList = new ArrayList<>();
        for (Reservations reservations : reservationsList) {
            dtoList.add(modelMapper.map(reservations, ReservationsDTO.class));
        }
        return dtoList;
    }

    @Override
    public ReservationsDTO readByNum(String reservationId) {
        Reservations reservations = reservationReadMapper.findReservationByNum(reservationId);

        if (reservations == null) {
            throw new RuntimeException("예약이 존재하지 않습니다. reservationId=" + reservationId);
        }

        ReservationsDTO reservationsDTO = modelMapper.map(reservations, ReservationsDTO.class);
        return reservationsDTO;
    }

    @Override
    public List<ReservationsDTO> readALLByNum(String reservationId) {
        List<Reservations> reservationsList = reservationReadMapper.findAllReservationByNum(reservationId);
        List<ReservationsDTO> dtoList = new ArrayList<>();
        for (Reservations reservations : reservationsList) {
            dtoList.add(modelMapper.map(reservations, ReservationsDTO.class));
        }
        return dtoList;
    }

    @Override
    public ReservationsDTO readByName(String name) {
        Reservations reservations = reservationReadMapper.findReservationByName(name);
        ReservationsDTO reservationsDTO = modelMapper.map(reservations, ReservationsDTO.class);
        return reservationsDTO;
    }

    @Override
    public List<ReservationsDTO> readAllByName(String name) {
        List<Reservations> reservationsList = reservationReadMapper.findAllReservationByName(name);
        List<ReservationsDTO> dtoList = new ArrayList<>();
        for (Reservations reservations : reservationsList) {
            dtoList.add(modelMapper.map(reservations, ReservationsDTO.class));
        }
        return dtoList;
    }

    @Override
    public void modify(ReservationsDTO reservationsDTO) {
        log.info(reservationsDTO);

        Reservations reservations = modelMapper.map(reservationsDTO, Reservations.class);
        log.info(reservations);
        reservationReadMapper.updateReservation(reservations);
    }

    /* 예약 상태 수정 */
    @Override
    public void modifyStatus(
            @Param("reservationId") String reservationId,
            @Param("status") String status) {
        reservationReadMapper.updateReservationStatus(reservationId, status);
    }

    /* 결제 상태 수정 */
    @Override
    public void modifyPayStatus(String reservationId, String payStatus) {
        reservationReadMapper.updatePayStatus(reservationId, payStatus);
    }

    /* 예약 Index 조회 */
    @Override
    public Long getReservationIdx(String reservationId) {
        Long idx = reservationReadMapper.findReservationIdx(reservationId);
        if (idx != null) {
            return idx;
        }
        return null;
    }

    /* 포인트 환불 */
    @Override
    public void processRefund(String reservationId) {

        // 1. 예약 조회
        ReservationsDTO reservationsDTO = readByNum(reservationId);

        if (reservationsDTO == null) {
            throw new RuntimeException("NOT_FOUND");
        }

        // 이미 취소된 예약 체크
        if ("Cancelled".equals(reservationsDTO.getStatus())) {
            throw new IllegalStateException("ALREADY_CANCELED");
        }

        // Reserved 아니면 전부 차단
        if (!"Reserved".equals(reservationsDTO.getStatus())) {
            throw new IllegalStateException("INVALID_STATUS");
        }

        Long memberNo = reservationsDTO.getMemberNo();
        Long idx = reservationReadMapper.findReservationIdx(reservationId);

        // 2. 포인트 조회
        int used = membersPointMapper.selectUsedPoint(idx);     // 음수
        int earned = membersPointMapper.selectEarnedPoint(idx); // 양수

        // 3. 사용 포인트 복구
        if (used < 0) {
            int restore = Math.abs(used);

            membersMapper.addPoint(memberNo, restore);

            membersPointMapper.insertPointHistory(
                    memberNo, idx, restore, 0   // earning
            );
        }

        // 4. 적립 포인트 회수
        if (earned > 0) {
            membersMapper.usePoint(memberNo, earned);

            membersPointMapper.insertPointHistory(
                    memberNo, idx, 0, -earned   // using_point
            );
        }

        // 5. 예약 상태 변경
        modifyStatus(reservationId, "Cancelled");

        // 6. 결제 상태 변경
        modifyPayStatus(reservationId, "Refund");
    }

    @Override
    public List<Integer> getAllRoomNumbers() {
        return reservationReadMapper.findAllRoomNos();
    }

    @Override
    public ReservationsDTO getRoomOptions(String reservationId) {
        Reservations reservations = reservationReadMapper.findReservationByNum(reservationId);

        if (reservations == null) {
            throw new RuntimeException("예약이 존재하지 않습니다. reservationId=" + reservationId);
        }

        ReservationsDTO reservationsDTO = modelMapper.map(reservations, ReservationsDTO.class);
        RoomOptionsSummary roomOptionsSummary = reservationReadMapper.findRoomOptions(reservationId);
        reservationsDTO.setOptions(roomOptionsSummary);
        log.info(reservationsDTO);

        return reservationsDTO;
    }

    @Override
    public int calculatePreviewPrice(String reservationId, int roomNo, LocalDate checkin, LocalDate checkout) {

        // 1. 숙박 일수
        long nights = ChronoUnit.DAYS.between(checkin, checkout);
        // 2. 객실 가격
        int basePrice = roomMapper.findPrice(roomNo);
        int roomTotal = (int) nights * basePrice;
        // 3. 기존 옵션 가격 유지
        int optionTotal = optionMapper.sumOptionCharge(reservationId);
        return roomTotal + optionTotal;
    }
}
