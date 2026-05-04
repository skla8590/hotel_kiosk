package hotel_kiosk.hotel_kiosk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RootController {
    @GetMapping("")
    public String root() {
        return "redirect:/JHotel";
    }
}
