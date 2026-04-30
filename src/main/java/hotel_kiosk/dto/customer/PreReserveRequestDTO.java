package hotel_kiosk.dto.customer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PreReserveRequestDTO {
    private String memberName;
    private String memberPhone;
    private String memberBirth;
    private List<Integer> roomNos;
    private String checkinDate;
    private String checkoutDate;
    private int regPeople;
    private int addOption;
}
