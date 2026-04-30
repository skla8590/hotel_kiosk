package hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.RoomDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
class RoomServiceTest {
    @Autowired(required = false)
    private RoomService roomService;

    @Test
    void add() {
        RoomDTO roomMasterDTO = RoomDTO.builder()
                .roomNo(200)
                .roomName("서비스 테스트")
                .roomType("서비스 테스트타입")
                .roomView("서비스 테스트뷰")
                .roomFloor(20)
                .basePrice(2000)
                .maxPeople(4)
                .bedType("서비스 테스트")
                .area(20.0)
                .rating(4.4)
                .imageUrl("service test")
                .build();
        log.info(roomMasterDTO);
        roomService.add(roomMasterDTO);
    }

    @Test
    void read() {
        List<RoomDTO> roomMasterDTOS = roomService.read();
        for (RoomDTO roomMasterDTO : roomMasterDTOS) {
            log.info(roomMasterDTO);
        }
    }

    @Test
    void readByName() {
        int floor = 10;
        String name = "로얄 스위트 오션";
        List<RoomDTO> roomMasterDTOS = roomService.readByName(name, floor);
        for (RoomDTO roomMasterDTO : roomMasterDTOS) {
            log.info(roomMasterDTO);
        }
    }

    @Test
    void readByType() {
        int floor = 10;
        String type = "Suite";
        List<RoomDTO> roomMasterDTOS = roomService.readByType(type, floor);
        for (RoomDTO roomMasterDTO : roomMasterDTOS) {
            log.info(roomMasterDTO);
        }
    }

    @Test
    void modify() {
        int roomNum = 200;
        RoomDTO roomMasterDTO = RoomDTO.builder()
                .roomNo(roomNum)
                .roomName("서비스 테스트 수정")
                .roomType("서비스 테스트타입 수정")
                .roomView("서비스 테스트뷰 수정")
                .roomFloor(20)
                .basePrice(2000)
                .maxPeople(4)
                .bedType("서비스 테스트 수정")
                .area(20.0)
                .rating(4.5)
                .imageUrl("service test modify")
                .build();
        roomService.modify(roomMasterDTO);
    }

    @Test
    void modifyByStatus() {
        RoomDTO roomMasterDTO = RoomDTO.builder()
                .roomNo(1000)
                .currentStatus("Cleaning")
                .build();
        roomService.modifyByCurrentStatus(roomMasterDTO);
    }

    @Test
    void remove() {
        int roomNum = 200;
        roomService.remove(roomNum);
    }
}