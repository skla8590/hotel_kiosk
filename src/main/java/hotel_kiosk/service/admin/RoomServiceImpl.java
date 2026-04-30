package hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.RoomMaster;
import hotel_kiosk.dto.admin.RoomDTO;
import hotel_kiosk.mapper.admin.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomMapper roomMapper;
    private final ModelMapper modelMapper;

    @Override
    public void add(RoomDTO roomMasterDTO) {
        log.info("roomMasterDTO: {}", roomMasterDTO);

        RoomMaster roomMaster = modelMapper.map(roomMasterDTO, RoomMaster.class);
        log.info(roomMaster);
        roomMapper.insertRoomMaster(roomMaster);
        roomMapper.insertRoomStatus(roomMaster);
    }

    @Override
    public List<RoomDTO> read() {
        List<RoomMaster> roomMasterDTOS = roomMapper.findAllRoom();
        List<RoomDTO> dtoList = new ArrayList<>();
        for (RoomMaster roomMaster : roomMasterDTOS) {
            dtoList.add(modelMapper.map(roomMaster, RoomDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<RoomDTO> readByName(String name, int floor) {
        List<RoomMaster> roomMasterDTOS = roomMapper.findRoomByName(name, floor);
        List<RoomDTO> dtoList = new ArrayList<>();
        for (RoomMaster roomMaster : roomMasterDTOS) {
            dtoList.add(modelMapper.map(roomMaster, RoomDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<RoomDTO> readByNo(Integer no, int floor) {
        List<RoomMaster> roomMasterDTOS = roomMapper.findRoomByNo(no, floor);
        List<RoomDTO> dtoList = new ArrayList<>();
        for (RoomMaster roomMaster : roomMasterDTOS) {
            dtoList.add(modelMapper.map(roomMaster, RoomDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<RoomDTO> readByType(String type, int floor) {
        List<RoomMaster> roomMasterDTOS = roomMapper.findRoomByType(type, floor);
        List<RoomDTO> dtoList = new ArrayList<>();
        for (RoomMaster roomMaster : roomMasterDTOS) {
            dtoList.add(modelMapper.map(roomMaster, RoomDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<RoomDTO> readByFloor(int roomFloor) {
        List<RoomMaster> roomMasterList = roomMapper.findRoomByFloor(roomFloor);
        List<RoomDTO> dtoList = new ArrayList<>();
        for (RoomMaster roomMaster : roomMasterList) {
            dtoList.add(modelMapper.map(roomMaster, RoomDTO.class));
        }
        return dtoList;
    }

    @Override
    public RoomDTO readOneByNo(Integer no, int floor) {
        RoomMaster roomMasterS = roomMapper.findOneRoomByNo(no, floor);
        RoomDTO roomDTO = modelMapper.map(roomMasterS, RoomDTO.class);
        log.info(roomDTO);
        return  roomDTO;
    }

    @Override
    public void modify(RoomDTO roomMasterDTO) {
        log.info(roomMasterDTO);

        RoomMaster roomMaster = modelMapper.map(roomMasterDTO, RoomMaster.class);
        log.info(roomMaster);
        roomMapper.updateRoomMaster(roomMaster);
    }

    @Override
    public void modifyByCurrentStatus(RoomDTO roomMasterDTO) {
        log.info(roomMasterDTO);

        RoomMaster roomMaster = modelMapper.map(roomMasterDTO, RoomMaster.class);
        log.info(roomMaster);

        roomMapper.updateRoomCurrentStatus(roomMaster);
    }

    @Override
    public void remove(int roomNum) {
        roomMapper.deleteRoomStatus(roomNum);
        roomMapper.deleteRoomMaster(roomNum);
    }
}
