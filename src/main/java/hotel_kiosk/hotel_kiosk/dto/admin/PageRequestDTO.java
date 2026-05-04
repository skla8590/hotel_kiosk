package hotel_kiosk.hotel_kiosk.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;   // 페이지 번호

    @Builder.Default
    private int size = 10;  // 한 페이지당 개수

    private String type;    // 검색의 종류 ex) t, w, tc, tcw

    private String keyword;

    public String[] getTypes() {
        if (this.type == null || this.type.isEmpty()) {
            return null;
        }
        return this.type.split("");
    }

    public Pageable getPageAble(String prop) {
        return PageRequest.of(this.page-1, this.size, Sort.by(prop).descending());
    }

    private String link;
    public String getLink() {
        if (this.link == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("page=").append(this.page);
            stringBuilder.append("&size=").append(this.size);

            // type
            if (this.type != null && !this.type.isEmpty()) {
                stringBuilder.append("&type=").append(this.type);
            }
            if (this.keyword != null && !this.keyword.isEmpty()) {
                stringBuilder.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
            }
            this.link = stringBuilder.toString();
        }
        return this.link;
    }
}
