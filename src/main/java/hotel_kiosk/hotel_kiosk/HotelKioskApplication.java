package hotel_kiosk.hotel_kiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HotelKioskApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelKioskApplication.class, args);
	}

}
