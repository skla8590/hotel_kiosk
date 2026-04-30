package hotel_kiosk.domain.admin;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomOptionsSummary {
    int breakfast;  // 조식
    int dinner;     // 석식
    int facility;   //
    int amenity;    //

}
