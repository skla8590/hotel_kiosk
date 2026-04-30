package hotel_kiosk.service.admin;

import hotel_kiosk.domain.customer.Members;
import hotel_kiosk.domain.customer.Reservations;
import hotel_kiosk.dto.admin.CustomerPageRequestDTO;
import hotel_kiosk.dto.admin.CustomerPageResponseDTO;
import hotel_kiosk.dto.admin.PageRequestDTO;
import hotel_kiosk.dto.admin.PageResponseDTO;
import hotel_kiosk.dto.customer.MemberDTO;
import hotel_kiosk.dto.customer.ReservationsDTO;
import hotel_kiosk.mapper.admin.MemberReadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberReadServiceImpl implements MemberReadService{
    private final MemberReadMapper memberReadMapper;
    private final ModelMapper modelMapper;

    @Override
    public CustomerPageResponseDTO<MemberDTO> read(CustomerPageRequestDTO pageRequestDTO) {
        int offset = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
        List<Members> membersList = memberReadMapper.findAllCustomer(pageRequestDTO.getSize(), offset);
        int total = memberReadMapper.countCustomer();
        List<MemberDTO> dtoList = membersList.stream()
                .map(members -> modelMapper.map(members, MemberDTO.class)).toList();
        return CustomerPageResponseDTO.<MemberDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .dtoList(dtoList)
                .build();
    }

    @Override
    public List<MemberDTO> readAll() {
        List<Members> membersList = memberReadMapper.findCustomerPage();
        List<MemberDTO> dtoList = new ArrayList<>();
        for (Members members : membersList) {
            dtoList.add(modelMapper.map(members, MemberDTO.class));
        }
        return dtoList;
    }

    @Override
    public MemberDTO find(int id) {
        Members members = memberReadMapper.findCustomer(id);
        MemberDTO memberDTO = modelMapper.map(members, MemberDTO.class);
        return memberDTO;
    }

    @Override
    public List<MemberDTO> readByName(String name) {
        List<Members> membersList = memberReadMapper.findCustomerByName(name);
        List<MemberDTO> dtoList = new ArrayList<>();
        for (Members members : membersList) {
            dtoList.add(modelMapper.map(members, MemberDTO.class));
        }
        return dtoList;
    }

    @Override
    public List<MemberDTO> readByPhone(String phone) {
        List<Members> membersList = memberReadMapper.findCustomerByPhone(phone);
        List<MemberDTO> dtoList = new ArrayList<>();
        for (Members members : membersList) {
            dtoList.add(modelMapper.map(members, MemberDTO.class));
        }
        return dtoList;
    }

    /* 포인트 복구 */
    @Override
    public void addPoint(Long memberNo, int usedPoint) {
        memberReadMapper.addPoint(memberNo, usedPoint);
    }

    /* 포인트 회수 */
    @Override
    public void usePoint(Long memberNo, int earnedPoint) {
        memberReadMapper.usePoint(memberNo, earnedPoint);
    }

    @Override
    public int getAllSumPayment(int id) {
        int result = memberReadMapper.allSumPayment(id);
        return result;
    }

    @Override
    public LocalDate getRecentDate(int id) {
        return memberReadMapper.recentDate(id);
    }
}
