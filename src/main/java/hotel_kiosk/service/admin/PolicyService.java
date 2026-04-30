package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.PricingPolicyDTO;

import java.time.LocalDate;
import java.util.List;

public interface PolicyService {
    /* 요금 정책 등록 */
    void add(PricingPolicyDTO pricingPolicyDTO);

    /* 요일 요금 정책 저장 */
    void addWeeklyPolicies(List<PricingPolicyDTO> list);

    /* 요금 정책 목록 */
    List<PricingPolicyDTO> getAll();

    /* 시즌 요금 정책 목록 */
    List<PricingPolicyDTO> getSeasonPolicies();

    /* 요일 요금 정책 목록 */
    List<PricingPolicyDTO> getWeeklyPolicies();

    /* 시즌 요금 정책 조회 */
    PricingPolicyDTO getOne(Long policyId);

    /* 요금 정책 삭제 */
    void removeOne(Long policyId);

    /* 요금 정책 수정 */
    void modify(PricingPolicyDTO pricingPolicyDTO);

    /* 최종 요금 책정 */
    int calculatePrice(int basePrice, LocalDate date);
}
