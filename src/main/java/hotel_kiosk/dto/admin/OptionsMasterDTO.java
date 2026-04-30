package hotel_kiosk.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionsMasterDTO {
    private Long optionId; // '옵션 ID'

    @NotNull
    private String optionName; // '옵션명'
    private String optionCategory; // '옵션 종류'
    private String optionTarget; // '옵션 적용 대상'
    private Integer optionPrice; // '옵션별 요금'
}
