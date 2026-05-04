package hotel_kiosk.hotel_kiosk.config;

import hotel_kiosk.service.customer.ai.ETLService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EtlRunner implements ApplicationRunner {
    private final ETLService etlService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (etlService.isAlreadyLoaded("hotel_policy.pdf")) {
            System.out.println("이미 벡터 데이터 존재 → ETL 생략");
            return;
        }

        etlService.etlFromPath(
                "data/hotel_policy.pdf",
                "호텔 정책",
                "관리자"
        );
    }
}
