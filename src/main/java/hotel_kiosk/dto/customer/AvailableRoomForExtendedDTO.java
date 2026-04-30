package hotel_kiosk.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRoomForExtendedDTO {
    // reservations
    private String reservationId; // 예약 번호
    private LocalDate checkinDate;    // 입실 예정 날짜
    private LocalDate checkoutDate;   // 퇴실 예정 날짜
    private int regPeople;            // 예약 인원

    // members
    private String memberName; // 고객명
    private String memberPhone; // 고객 연락처

    // room_master
    private String roomNo; // 객실번호
    private String roomName; // 객실명
    private String imageUrl; // 객실 이미지 경로
    private String roomView; // 객실 조망
}
