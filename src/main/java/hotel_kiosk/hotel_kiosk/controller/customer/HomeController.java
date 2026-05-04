package hotel_kiosk.hotel_kiosk.controller.customer;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/JHotel")
public class HomeController {
    // 대기 화면
    @GetMapping("")
    public String standby() {
        return "customer/standby";
    }

    // 메인 화면
    @GetMapping("/main")
    public String mainBoard() {
        return "customer/main";
    }
}
