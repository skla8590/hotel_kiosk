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
public class RoomSearchRequestDTO {
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int regPeople;
}
