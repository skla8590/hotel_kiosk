package hotel_kiosk.hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.PricingPolicy;
import hotel_kiosk.dto.admin.PricingPolicyDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PolicyMapper {
    /* 요금 정책 등록 */
    void insert (PricingPolicy pricingPolicy);

    /* 요금 정책 목록 */
    List<PricingPolicy> selectAll();

    /* 시즌 요금 정책 목록 */
    List<PricingPolicyDTO> selectSeasonPolicies();

    /* 요일 요금 정책 목록 */
    List<PricingPolicyDTO> selectWeeklyPolicies();

    /* 요금 정책 조회 */
    PricingPolicy selectOne (Long policyId);

    /* 요금 정책 삭제 */
    void deleteOne (Long policyId);

    /* 요금 정책 수정 */
    void update (PricingPolicy pricingPolicy);

    /* 시즌 기간 조회 */
    PricingPolicy findSeasonByDate (LocalDate date);

    /* 요일 기간 조회 */
    PricingPolicy findWeekly (int date);
}
