package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.dto.admin.RoomDTO;

import java.util.List;

public interface RoomService {
    void add(RoomDTO roomMasterDTO);  // 객실 등록

    List<RoomDTO> read(); // 객실 목록

    // 객실 조회
    List<RoomDTO> readByName(String name, int floor);          // 이름 기준
    List<RoomDTO> readByNo(Integer no, int floor);          // 이름 기준
    List<RoomDTO> readByType(String type, int floor);    // 타입 기준
    List<RoomDTO> readByFloor(int roomFloor);  // 층별 기준
    RoomDTO readOneByNo(Integer no, int floor);          // 이름 기준

    void modify(RoomDTO roomMasterDTO);       // 객실 수정

    void remove(int roomNum);                       // 객실 삭제
}
