package hotel_kiosk.hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.OptionsMasterDTO;
import hotel_kiosk.dto.admin.PricingPolicyDTO;
import hotel_kiosk.service.admin.OptionService;
import hotel_kiosk.service.admin.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/admin/policy")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;
    private final OptionService optionService;

    /* 정책 설정 페이지 - 시즌/요일/옵션 데이터를 Model에 담아 Thymeleaf로 렌더링 */
    @GetMapping("")
    public String policy(Model model) {

        // 시즌 정책 목록
        List<PricingPolicyDTO> seasons = policyService.getAll();
        model.addAttribute("seasons", seasons);

        // 요일 정책 목록
        List<PricingPolicyDTO> weekly = policyService.getWeeklyPolicies();
        Map<Integer, Double> weekdayMap = new HashMap<>();
        for (int i = 1; i <= 7; i++) weekdayMap.put(i, 0.0);
        for (PricingPolicyDTO p : weekly) {
            if (p.getRepeatValue() == null) continue;
            for (String d : p.getRepeatValue().split(",")) {
                int day = Integer.parseInt(d);
                if (weekdayMap.get(day) != 0.0) continue;
                weekdayMap.put(day, p.getDiscountRate());
            }
        }
        model.addAttribute("weekdayMap", weekdayMap);

        // 옵션 목록
        List<OptionsMasterDTO> options = optionService.getAll();
        model.addAttribute("options", options);

        return "admin/operation/policy/policy";
    }

    /* 옵션 등록 */
    @PostMapping("/options")
    @ResponseBody
    public ResponseEntity<Void> addOption(@RequestBody OptionsMasterDTO optionsMasterDTO) {
        log.info("options add post...");
        optionService.add(optionsMasterDTO);
        return ResponseEntity.status(201).build();
    }

    /* 옵션 수정 */
    @PutMapping("/options/{optionId}")
    @ResponseBody
    public ResponseEntity<Void> modifyOption(
            @PathVariable Long optionId,
            @RequestBody OptionsMasterDTO optionsMasterDTO
    ) {
        log.info("options modify put...");
        optionsMasterDTO.setOptionId(optionId);
        optionService.modify(optionsMasterDTO);
        return ResponseEntity.noContent().build();
    }

    /* 시즌 등록 */
    @PostMapping("/season")
    @ResponseBody
    public ResponseEntity<Void> addSeason(@RequestBody PricingPolicyDTO pricingPolicyDTO) {
        log.info("policy add post...");
        policyService.add(pricingPolicyDTO);
        return ResponseEntity.status(201).build();
    }

    /* 시즌 수정 */
    @PutMapping("/season/{policyId}")
    @ResponseBody
    public ResponseEntity<Void> modifySeason(
            @PathVariable Long policyId,
            @RequestBody PricingPolicyDTO pricingPolicyDTO
    ) {
        log.info("policy modify put...");
        pricingPolicyDTO.setPolicyId(policyId);
        policyService.modify(pricingPolicyDTO);
        return ResponseEntity.noContent().build();
    }

    /* 요일 정책 저장 */
    @PutMapping("/weekly")
    @ResponseBody
    public ResponseEntity<Void> updateWeekly(@RequestBody List<PricingPolicyDTO> list) {
        policyService.addWeeklyPolicies(list);
        return ResponseEntity.noContent().build();
    }
}