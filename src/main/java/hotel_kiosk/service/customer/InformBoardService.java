package hotel_kiosk.service.customer;

import hotel_kiosk.domain.admin.InformBoard;
import hotel_kiosk.dto.admin.InformBoardDTO;

import java.util.List;

public interface InformBoardService {
    List<InformBoardDTO> getAllInformBoard();
}
