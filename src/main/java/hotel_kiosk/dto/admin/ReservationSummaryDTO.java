package hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationSummaryDTO {
    private int success;
    private int waiting;
    private int cancelled;

    private double succeedRate;
    private double waitedRate;
    private double cancelledRate;
}
