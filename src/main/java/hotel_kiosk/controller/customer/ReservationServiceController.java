package hotel_kiosk.controller.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/JHotel/reservation")
@RequiredArgsConstructor
public class ReservationServiceController {
    @GetMapping("")
    public String reservation() {
        return "customer/reservation/reservation_service";
    }
}
