package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.PricingPolicyDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Log4j2
@SpringBootTest
class PolicyServiceImplTest {
    @Autowired
    PolicyService policyService;

    /* 요금 정책 등록 */
    @Test
    void add() {
        PricingPolicyDTO pricingPolicyDTO = PricingPolicyDTO.builder()
                .policyName("주말 정책")
                .repeatType("Weekly")
                .repeatValue("1,7")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .discountRate(1.5)
                .build();

        policyService.add(pricingPolicyDTO);
    }

    /* 요금 정책 목록 */
    @Test
    void getAll() {
        policyService.getAll();
    }

    /* 요금 정책 조회 */
    @Test
    void getOne() {
        Long policyId = 1L;
        policyService.getOne(policyId);
    }

    /* 요금 정책 삭제 */
    @Test
    void removeOne() {
        Long policyId = 6L;
        policyService.removeOne(policyId);
    }

    /* 요금 정책 수정 */
    @Test
    void modify() {
        PricingPolicyDTO pricingPolicyDTO = PricingPolicyDTO.builder()
                .policyId(1L)
                .policyName("주말 정책 수정")
                .build();

        policyService.modify(pricingPolicyDTO);
    }
}