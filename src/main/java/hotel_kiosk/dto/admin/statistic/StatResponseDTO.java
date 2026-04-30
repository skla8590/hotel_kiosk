package hotel_kiosk.dto.admin.statistic;

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
