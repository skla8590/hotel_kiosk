package hotel_kiosk.domain.admin;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformBoard {
    private Long informId; // '게시글 ID'
    private String title; // '게시글 제목'
    private String content; // '게시글 내용'
    private String writer; // '작성자'
    private LocalDate regDate; // '작성일'
    private LocalDate modDate; // '수정일'
}
