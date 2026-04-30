package hotel_kiosk.service.customer;

import hotel_kiosk.mapper.customer.PayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class WaitingCleanupScheduler {
    private final PayMapper payMapper;

    // 1분마다 실행
    @Scheduled(fixedDelay = 60000)
    public void deleteWaitingReservations() {
        int deleted = payMapper.deleteWaitingReservations();
        if (deleted > 0) {
            log.info("스케줄러 만료된 Waiting 예약 {}건 삭제 완료", deleted);
        }
    }
}
