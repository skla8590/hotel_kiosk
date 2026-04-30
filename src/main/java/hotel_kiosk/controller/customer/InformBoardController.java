package hotel_kiosk.controller.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hotel_kiosk.dto.admin.InformBoardDTO;
import hotel_kiosk.service.customer.InformBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/JHotel")
public class InformBoardController {
    private final InformBoardService informBoardService;

    // 호텔 안내 게시판
    @GetMapping("/hotel_info")
    public String list(Model model) throws JsonProcessingException {
        List<InformBoardDTO> informBoards = informBoardService.getAllInformBoard();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String informBoardsJson = objectMapper.writeValueAsString(informBoards);
        model.addAttribute("informBoardsJson", informBoardsJson);
        return "customer/hotel_info";
    }
}
