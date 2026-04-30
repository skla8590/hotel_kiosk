package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.PricingPolicy;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class PolicyMapperTest {
    @Autowired
    private PolicyMapper policyMapper;

    @Test
    void insert() {
        PricingPolicy pricingPolicy = PricingPolicy.builder()
                .policyName("평일 요금 정책")
                .repeatType("Weekly")
                .repeatValue("2,3,4,5,6") // DayOfWeek 함수 사용 가능. Java와 MariaDB의 기준이 다름 (Java -> 월=1 ~ 일=7 / MariaDB -> 일=1 ~ 토=7)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .discountRate(0.8)
                .build();

        policyMapper.insert(pricingPolicy);
    }

    @Test
    void selectAll() {
        List<PricingPolicy> pricingPolicies = policyMapper.selectAll();
        for (PricingPolicy pricingPolicy : pricingPolicies) {
            log.info(pricingPolicy.toString());
        }
    }

    @Test
    void selectOne() {
        Long policyId = 1L;
        PricingPolicy pricingPolicy = policyMapper.selectOne(policyId);
        log.info(pricingPolicy.toString());
    }

    @Test
    void deleteOne() {
        Long policyId = 4L;
        policyMapper.deleteOne(policyId);
        Assertions.assertNotNull(policyId);
    }

    @Test
    void update() {
        Long policyId = 3L;
        PricingPolicy pricingPolicy = PricingPolicy.builder()
                .policyId(policyId)
                .discountRate(0.6)
                .build();
        policyMapper.update(pricingPolicy);

        log.info(policyMapper.selectOne(policyId).toString());
    }
}