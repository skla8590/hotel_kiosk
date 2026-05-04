package hotel_kiosk.hotel_kiosk.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicyDTO {
    private Long policyId; // '요금 정책 ID'

    @NotNull
    private String policyName; // '요금 정책 이름'

    private String repeatType; // '반복 주기'
    private String repeatValue; // '반복 조건'

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // '요금 적용 시작일'

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // '요금 적용 종료일'
    private Double discountRate; // '할인율'
}
