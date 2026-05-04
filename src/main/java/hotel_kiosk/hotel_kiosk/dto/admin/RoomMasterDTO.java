package hotel_kiosk.hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMasterDTO {
    private Integer roomNo; // '객실 호수'
    private String roomName; // '객실명'
    private String roomType; // '객실 종류'
    private String roomView; // '객실 조망'
    private Integer roomFloor; // '객실 층수'
    private Integer basePrice; // '객실 기본 숙박 요금'
    private Integer maxPeople; // '객실 제한 인원'
    private String bedType; // '침대 종류'
    private Double area; // '객실 면적'
    private Double rating; // '객실 평점'
    private String imageUrl; // '객실 이미지 경로'
    private LocalDateTime updatedAt; // '객실 정보 마지막 수정 일시'
    private String roomStatus; // '객실 활성화/비활성화 여부'   ------------------***
}