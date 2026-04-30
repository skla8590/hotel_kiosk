package hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.CustomerPageRequestDTO;
import hotel_kiosk.dto.admin.CustomerPageResponseDTO;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.customer.MemberDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.service.admin.MemberReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/customer")
@Log4j2
@RequiredArgsConstructor
public class CustomerController {
    private final MemberReadService memberReadService;

    @GetMapping
    public String member(CustomerPageRequestDTO pageRequestDTO, Model model) {
        log.info("member...");
        CustomerPageResponseDTO<MemberDTO> memberDTOList = memberReadService.read(pageRequestDTO);
        log.info(memberDTOList);
        model.addAttribute("customers", memberDTOList);
        return "admin/operation/customer/customer";
    }

    @GetMapping("/{memberNo}")
    @ResponseBody
    public ResponseEntity<MemberDTO> getMember(@PathVariable int memberNo) {
        log.info("memberGet......");
        log.info(memberNo);
        MemberDTO memberDTO = memberReadService.find(memberNo);
        memberDTO.setTotalPayment(memberReadService.getAllSumPayment(memberNo));
        memberDTO.setLastVisit(memberReadService.getRecentDate(memberNo));
        return ResponseEntity.ok(memberDTO);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<CustomerPageResponseDTO<MemberDTO>> searchCustomer(
            CustomerPageRequestDTO pageRequestDTO,
            @RequestParam String keyword) {

        log.info("검색 keyword: {}", keyword);

        List<MemberDTO> result = memberReadService.readAll();


        Map<Integer, MemberDTO> uniqueMap = new LinkedHashMap<>();

        for (MemberDTO dto : result) {
            uniqueMap.putIfAbsent(dto.getMemberNo(), dto);
        }

        List<MemberDTO> uniqueList = new ArrayList<>(uniqueMap.values());

        String normalized = keyword.replaceAll("[^0-9]", "");

        // 전화번호 검색 (숫자가 포함된 경우)
        if (!normalized.isEmpty()) {
            List<MemberDTO> byPhone = memberReadService.readByPhone(normalized);
            if (byPhone != null && !byPhone.isEmpty()) {
                result.addAll(byPhone);
                log.info("phone: {}", byPhone);
            }
        }

        // 이름 검색 (숫자가 아닌 경우)
        if (normalized.isEmpty()) {
            List<MemberDTO> byName = memberReadService.readByName(keyword);
            if (byName != null && !byName.isEmpty()) {
                result.addAll(byName);
                log.info("name: {}", byName);
            }
        }

        int total = uniqueList.size();

        int start = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
        int end = Math.min(start + pageRequestDTO.getSize(), total);

        List<MemberDTO> pageList =
                (start >= total) ? List.of() : uniqueList.subList(start, end);

// 3. 반환
        CustomerPageResponseDTO<MemberDTO> response =
                CustomerPageResponseDTO.<MemberDTO>withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total(total)
                        .dtoList(pageList)
                        .build();

        return ResponseEntity.ok(response);
    }
}
