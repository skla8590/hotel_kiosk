package hotel_kiosk.domain.admin;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionsRecord {
    private Long optionRecordId; // '예약별 옵션 기록 ID'
    private Integer quantity; // '옵션 수량'
    private Integer optionCharge; // '옵션 총 부과 금액'
}
