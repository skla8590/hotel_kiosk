package hotel_kiosk.domain.admin;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicyRoom {
    private Integer roomPrice; // '요금 정책에 따라 산정된 객실 요금'
}
