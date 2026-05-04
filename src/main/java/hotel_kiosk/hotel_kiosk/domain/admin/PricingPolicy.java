package hotel_kiosk.hotel_kiosk.domain.admin;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicy {
    private Long policyId; // '요금 정책 ID'
    private String policyName; // '요금 정책 이름'
    private String repeatType; // '반복 주기'
    private String repeatValue; // '반복 조건'
    private LocalDate startDate; // '요금 적용 시작일'
    private LocalDate endDate; // '요금 적용 종료일'
    private Double discountRate; // '할인율'
}
