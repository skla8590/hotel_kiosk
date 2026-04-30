package hotel_kiosk.domain.admin;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionsMaster {
    private Long optionId; // '옵션 ID'
    private String optionName; // '옵션명'
    private String optionCategory; // '옵션 종류'
    private String optionTarget; // '옵션 적용 대상'
    private Integer optionPrice; // '옵션별 요금'
}
