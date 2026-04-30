package hotel_kiosk.service.customer;

import hotel_kiosk.dto.admin.InformBoardDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class InformBoardServiceImplTest {
    @Autowired
    private InformBoardService informBoardService;

    @Test
    void getAllInformBoardTest() {
//        List<InformBoardDTO> informBoardDTOS = informBoardService.getAllInformBoard();
//        for (InformBoardDTO informBoardDTO : informBoardDTOS) {
//            log.info(informBoardDTO);
//    }
        informBoardService.getAllInformBoard().forEach(System.out::println);

    }
}