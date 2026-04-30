package hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.admin.RoomMaster;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

@Mapper
public interface RoomMapper {

    /* 객실 등록 */
    void insertRoomMaster(RoomMaster roomMaster);
    void insertRoomStatus(RoomMaster roomMaster);

    /* 객실 목록 */
    List<RoomMaster> findAllRoom();

    /* 객실 조회 */
    List<RoomMaster> findRoomByName(String roomName, int roomFloor);   // 이름
    List<RoomMaster> findRoomByNo(Integer roomNo, int roomFloor);   // 이름
    List<RoomMaster> findRoomByType(String roomType, int roomFloor);   // 객실 종류
    List<RoomMaster> findRoomByFloor(int roomFloor);

    RoomMaster findOneRoomByNo(Integer roomNo, int roomFloor);   // 이름

    /* 객실 갱신 */
    void updateRoomMaster(RoomMaster roomMaster);

    /* 객실 현재 상태 변경 */
    void updateRoomCurrentStatus(RoomMaster roomMaster);

    /* 객실 삭제 */
    void deleteRoomMaster(int roomNum);
    void deleteRoomStatus(int roomNum);

    int findPrice(int roomNo);
}
