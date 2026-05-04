package hotel_kiosk.hotel_kiosk.service.customer;

import hotel_kiosk.domain.admin.InformBoard;
import hotel_kiosk.dto.admin.InformBoardDTO;
import hotel_kiosk.mapper.customer.CustomerMapper;
import hotel_kiosk.service.customer.InformBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class InformBoardServiceImpl implements InformBoardService {
    private final CustomerMapper customerMapper;
    private final ModelMapper modelMapper;

    @Override
    public List<InformBoardDTO> getAllInformBoard() {
        List<InformBoardDTO> informBoardDTOS = new ArrayList<>();
        List<InformBoard> informBoards = customerMapper.selectAllInformBoard();
        for (InformBoard informBoard : informBoards) {
            informBoardDTOS.add(modelMapper.map(informBoard, InformBoardDTO.class));
        }
        return informBoardDTOS;
    }
}
