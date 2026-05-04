package hotel_kiosk.hotel_kiosk.service.admin;

import hotel_kiosk.domain.admin.OptionsMaster;
import hotel_kiosk.dto.admin.OptionsMasterDTO;
import hotel_kiosk.mapper.admin.OptionMapper;
import hotel_kiosk.service.admin.OptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionMapper optionMapper;
    private final ModelMapper modelMapper;

    /* 옵션 정책 등록 */
    @Override
    public void add(OptionsMasterDTO optionsMasterDTO) {
        log.info("OptionsMasterDTO: {}", optionsMasterDTO);

        // DTO -> VO
        OptionsMaster optionsMaster = modelMapper.map(optionsMasterDTO, OptionsMaster.class);
        log.info("OptionsMaster: {}", optionsMaster);

        optionMapper.insert(optionsMaster);
    }

    /* 옵션 정책 목록 */
    @Override
    public List<OptionsMasterDTO> getAll() {
        List<OptionsMaster> optionsMasterList = optionMapper.selectAll();
        List<OptionsMasterDTO> optionsMasterDTOList = new ArrayList<>();

        for (OptionsMaster optionsMaster : optionsMasterList) {
            OptionsMasterDTO optionsMasterDTO = modelMapper.map(optionsMaster, OptionsMasterDTO.class);
            optionsMasterDTOList.add(optionsMasterDTO);
        }

        for (OptionsMasterDTO optionsMasterDTO : optionsMasterDTOList) {
            log.info("OptionsMasterDTO: {}", optionsMasterDTO);
        }

        return optionsMasterDTOList;
    }

    /* 옵션 정책 수정 */
    @Override
    public void modify(OptionsMasterDTO optionsMasterDTO) {
        if (optionsMasterDTO.getOptionId() == null) {
            throw new IllegalArgumentException("optionId 없음");
        }

        // DTO -> VO
        OptionsMaster optionsMaster = modelMapper.map(optionsMasterDTO, OptionsMaster.class);
        log.info("OptionsMaster: {}", optionsMaster);

        optionMapper.update(optionsMaster);
    }
}
