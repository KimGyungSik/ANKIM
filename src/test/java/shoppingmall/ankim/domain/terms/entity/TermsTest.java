package shoppingmall.ankim.domain.terms.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;

@SpringBootTest
class TermsTest {

    @Autowired
    TermsRepository termsRepository;

    /*
     * 에러가 발생하는 경우를 테스트한다.
     * 1. id
     *   1.1. 길이 초과하는 경우
     *   1.2. null 값인 경우
     * 2. pwd
     *   2.1. 길이 초과하는 경우
     *   2.2. null 값인 경우
     * 3. 이름
     *   3.1. 길이 초과하는 경우
     *   3.2. null 값인 경우
     * 4. 휴대전화번호
     *   4.1 길이 초과하는 경우
     *   4.2. null 값인 경우
     * 5. 생년월일
     *   5.1. null 값인 경우
     * 6. 성별
     *   6.1. 길이 초과하는 경우
     *   6.2. null 값인 경우
     * 7. 회원 등급
     *   7.1. null 값인 경우
     * 8. 회원 상태
     *   8.1. null 값인 경우
     * */
}