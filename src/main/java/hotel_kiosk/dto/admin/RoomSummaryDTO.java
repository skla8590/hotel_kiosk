package hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomSummaryDTO {
    private int available;
    private int occupied;
    private int cleaning;
    private int cleaningRequired;
    private int maintenance;

    private double availableRate;
    private double occupiedRate;
    private double cleaningRate;
    private double cleaningRequiredRate;
    private double maintenanceRate;

    private int maxHeight;
}
