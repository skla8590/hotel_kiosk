package hotel_kiosk.hotel_kiosk.dto.admin.statistic;

import hotel_kiosk.dto.admin.statistic.OptionRateDTO;
import hotel_kiosk.dto.admin.statistic.RoomRateDTO;
import hotel_kiosk.dto.admin.statistic.StatSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResponseDTO {
    private StatSummaryDTO summary;
    private List<RoomRateDTO> roomRates;
    private List<OptionRateDTO> optionRates;
}
