package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.PricingPolicy;
import hotel_kiosk.dto.admin.PricingPolicyDTO;
import hotel_kiosk.mapper.admin.PolicyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
    private final PolicyMapper policyMapper;
    private final ModelMapper modelMapper;

    /* 요금 정책 등록 */
    @Override
    public void add(PricingPolicyDTO pricingPolicyDTO) {
        log.info("PricingPolicyDTO : {}", pricingPolicyDTO);

        // DTO -> VO
        PricingPolicy pricingPolicy = modelMapper.map(pricingPolicyDTO, PricingPolicy.class);
        log.info("PricingPolicy : {}", pricingPolicy);

        policyMapper.insert(pricingPolicy);
    }

    /* 요일 요금 정책 저장 */
    @Override
    public void addWeeklyPolicies(List<PricingPolicyDTO> list) {
        for (PricingPolicyDTO dto : list) {

            dto.setPolicyName("요일 정책");
            dto.setRepeatType("Weekly");

            PricingPolicy policy = modelMapper.map(dto, PricingPolicy.class);

            policyMapper.insert(policy);
        }
    }

    /* 요금 정책 목록 */
    @Override
    public List<PricingPolicyDTO> getAll() {
        List<PricingPolicy> pricingPolicyList = policyMapper.selectAll();
        List<PricingPolicyDTO> pricingPolicyDTOList = new ArrayList<>();

        for (PricingPolicy pricingPolicy : pricingPolicyList) {
            PricingPolicyDTO pricingPolicyDTO = modelMapper.map(pricingPolicy, PricingPolicyDTO.class);
            pricingPolicyDTOList.add(pricingPolicyDTO);
        }

        for (PricingPolicyDTO pricingPolicyDTO : pricingPolicyDTOList) {
            log.info("PricingPolicyDTO : {}", pricingPolicyDTO);
        }

        return pricingPolicyDTOList;
    }

    /* 요일 요금 정책 목록 */
    @Override
    public List<PricingPolicyDTO> getSeasonPolicies() {
        List<PricingPolicyDTO> pricingPolicyDTOList = policyMapper.selectSeasonPolicies();

        log.info("PricingPolicyDTO : {}", pricingPolicyDTOList);

        return pricingPolicyDTOList;
    }

    /* 요일 요금 정책 목록 */
    @Override
    public List<PricingPolicyDTO> getWeeklyPolicies() {
        List<PricingPolicyDTO> pricingPolicyDTOList = policyMapper.selectWeeklyPolicies();

        log.info("PricingPolicyDTO : {}", pricingPolicyDTOList);

        return pricingPolicyDTOList;
    }

    /* 요금 정책 조회 */
    @Override
    public PricingPolicyDTO getOne(Long policyId) {
        PricingPolicy pricingPolicy = policyMapper.selectOne(policyId);

        PricingPolicyDTO pricingPolicyDTO = modelMapper.map(pricingPolicy, PricingPolicyDTO.class);

        log.info(pricingPolicyDTO);

        return pricingPolicyDTO;
    }

    /* 요금 정책 삭제 */
    @Override
    public void removeOne(Long policyId) {
        policyMapper.deleteOne(policyId);
    }

    /* 요금 정책 수정 */
    @Override
    public void modify(PricingPolicyDTO pricingPolicyDTO) {
        if (pricingPolicyDTO.getPolicyId() == null) {
            throw new IllegalArgumentException("policyId 없음");
        }

        // DTO -> VO
        PricingPolicy pricingPolicy = modelMapper.map(pricingPolicyDTO, PricingPolicy.class);
        log.info("PricingPolicy : {}", pricingPolicy);

        policyMapper.update(pricingPolicy);
    }

    /* 최종 요금 책정 */
    @Override
    public int calculatePrice(int basePrice, LocalDate date) {
        double result = basePrice;

        // 1. 시즌 정책 적용
        PricingPolicy season = policyMapper.findSeasonByDate(date);

        if (season != null) {
            log.info("시즌 적용: {}", season.getPolicyName());

            result += result * (season.getDiscountRate() / 100);
        }

        // 2. 요일 정책 적용
        int day = date.getDayOfWeek().getValue();
        day = (day % 7) + 1;

        PricingPolicy weekly = policyMapper.findWeekly(day);

        if (weekly != null) {
            log.info("요일 적용: {}", day);

            result += result * (weekly.getDiscountRate() / 100);
        }

        return (int) result;
    }
}
