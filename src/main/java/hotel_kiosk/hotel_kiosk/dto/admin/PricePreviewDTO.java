package hotel_kiosk.hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePreviewDTO {
    private String reservationId;
    private int roomNo;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
}
