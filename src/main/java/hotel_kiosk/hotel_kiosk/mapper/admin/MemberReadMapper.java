package hotel_kiosk.hotel_kiosk.mapper.admin;

import hotel_kiosk.domain.customer.Members;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MemberReadMapper {
    /* 고객 목록 */
    List<Members> findAllCustomer(@Param("size") int size, @Param("offset") int offset);
    List<Members> findCustomerPage();
    int countCustomer();

    /* 고객 상세 조회 */
    Members findCustomer(int id);

    /* 고객 조건 조회 */
    List<Members> findCustomerByName(String name);      // 고객명
    List<Members> findCustomerByPhone(String phone);    // 연락처

    /* 포인트 복구 */
    void addPoint(Long memberNo, int usedPoint);

    /* 포인트 회수 */
    void usePoint(Long memberNo, int earnedPoint);

    int allSumPayment(int id);

    LocalDate recentDate(int id);
}
