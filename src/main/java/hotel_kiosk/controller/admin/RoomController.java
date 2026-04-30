package hotel_kiosk.controller.admin;

import hotel_kiosk.dto.admin.RoomDTO;
import hotel_kiosk.service.admin.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/room")
@Log4j2
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public String roomFloor(Model model) {
        log.info("room...");
        List<RoomDTO> roomMasterDTOList = roomService.read();

        // 층 목록 추출 (중복 제거 + 정렬)
        List<Integer> floors = roomMasterDTOList.stream()
                .map(dto -> dto.getRoomNo() / 100) // 예: 1101 → 11층
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("floors", floors);
        return "admin/operation/room/room";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public List<RoomDTO> getRoom(@PathVariable int id) {
        log.info("room Floor...");
        log.info(id);
        log.info(roomService.readByFloor(id));
        return roomService.readByFloor(id);
    }

    @GetMapping("/{roomFloor}/{roomNo}")
    @ResponseBody
    public RoomDTO getOneRoom(
            @PathVariable int roomFloor,
            @PathVariable int roomNo
    ) {
        log.info("room No...");
        log.info(roomNo);
        log.info(roomService.readOneByNo(roomNo, roomFloor));
        return roomService.readOneByNo(roomNo, roomFloor);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> searchRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer floor) {

        log.info("검색 keyword: " + keyword);
        log.info("검색 type: " + type);
        log.info("검색 floor: " + floor);

        // 1. 층 기준으로 먼저 조회
        List<RoomDTO> result;

        if (floor != null) {
            result = roomService.readByFloor(floor);
        } else {
            result = roomService.read();
        }

        if (keyword != null && !keyword.isBlank()) {
            List<RoomDTO> byName = roomService.readByName(keyword, floor);
            if (byName != null && !byName.isEmpty()) {
                Set<Integer> nameIds = byName.stream()
                        .map(RoomDTO::getRoomNo)
                        .collect(Collectors.toSet());

                result = result.stream()
                        .filter(r -> nameIds.contains(r.getRoomNo()))
                        .toList();
            }
        }

        // 3. 예약번호 검색 (숫자일 때)
        if (keyword != null && !keyword.isBlank()) {
            try {
                int no = Integer.parseInt(keyword);

                List<RoomDTO> byNo = roomService.readByNo(no, floor);
                if (byNo != null && !byNo.isEmpty()) {
                    Set<Integer> nameIds = byNo.stream()
                            .map(RoomDTO::getRoomNo)
                            .collect(Collectors.toSet());

                    result = result.stream()
                            .filter(r -> nameIds.contains(r.getRoomNo()))
                            .toList();
                }
            } catch (NumberFormatException ignored) {}
        }

        // 5. 상태 검색
        if (type != null && !type.isBlank()) {
            List<RoomDTO> byType = roomService.readByType(type, floor);

            Set<Integer> typeIds = byType.stream()
                    .map(RoomDTO::getRoomNo)
                    .collect(Collectors.toSet());

            result = result.stream()
                    .filter(r -> typeIds.contains(r.getRoomNo()))
                    .toList();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RoomDTO registerRoom(@Valid @RequestBody RoomDTO roomDTO) {
        log.info("registerRoom...");
        log.info(roomDTO);

        roomService.add(roomDTO);

        return roomDTO;
    }

    @GetMapping("/list/{id}")
    @ResponseBody
    public List<RoomDTO> getStocksList(@PathVariable int id) {
        return roomService.readByFloor(id);
    }



    @PutMapping(value = "/{id}/{no}")
    @ResponseBody
    public RoomDTO modify(@PathVariable("id") Integer id,
                            @PathVariable("no") Integer no,
                            @RequestBody RoomDTO roomDTO) {
        log.info("Put modify....");
        log.info(id);
        log.info(roomDTO);

        roomDTO.setRoomNo(no);

        roomService.modify(roomDTO);

        return roomDTO;
    }

    @DeleteMapping("/{no}")
    @ResponseBody
    public Map<String, Integer> remove(@PathVariable Integer no) {
        roomService.remove(no);
        Map<String, Integer> map = new HashMap<>();
        map.put("no", no);
        return map;
    }
}
