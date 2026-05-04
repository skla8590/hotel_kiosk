package hotel_kiosk.hotel_kiosk.dto.admin;

import hotel_kiosk.dto.admin.PageRequestDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {
    private final int page;
    private final int size;
    private final int total;  // 전체 게시물 숫자

    // 시작 페이지 번호
    private int start;
    // 끝 페이지 번호
    private int end;

    // 이전 페이지의 존재 여부
    private final boolean prev;
    // 다음 페이지의 존재 여부
    private final boolean next;

    // 목록 데이터
    private final List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, int total, List<E> dtoList) {
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        // int pageRangeCount = 5;

        // 페이지 번호가 10개 출력
        this.end = (int) (Math.ceil(this.page / 5.0) * 5);
        this.start = this.end - 4;

        /*if (this.page > 2) {
            this.end = this.page + 2;
            this.start = this.page - 2;
        }*/
        // end는 총 게시물 숫자 / 10을 넘을 수 없음
        // ex) page -> 72, end -> 80, start -> 71
        // if total -> 751, end -> 76

        int last = (int)(Math.ceil((total/(double)size)));

        // this end = end > last ? last : end;
        this.end = Math.min(end, last);

        this.prev = this.start > 1;

        this.next = total > this.end * this.size;
    }
}
