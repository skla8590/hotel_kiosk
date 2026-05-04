package hotel_kiosk.hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDTO {
    private int totalRooms ;
    private int currentGuests;
    private int todayCheckIn;
    private int stockAlert;
    private int upcomingCheckIn;

    private double occupancyRate;
}
