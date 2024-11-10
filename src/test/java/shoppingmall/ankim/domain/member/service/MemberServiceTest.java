package shoppingmall.ankim.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;


    @Test
    @DisplayName("중복된 이메일로 검증 시 예외가 발생한다.")
    void emailCheckDuplicateEmailTest() {
        // given
        String duplicateEmail = "test@ankim.com";

        // when : 중복 이메일이 존재한다고 가정
        when(memberRepository.existsById(duplicateEmail)).thenReturn(true);

        // then
        assertThrows(MemberRegistrationException.class,
                () -> memberService.emailCheck(duplicateEmail));
    }

    @Test
    @DisplayName("중복되지 않은 이메일로 검증 시 예외가 발생하지 않는다.")
    void emailCheckUniqueEmailTest() {
        // given
        String uniqueEmail = "unique@example.com";

        // when
        when(memberRepository.existsById(uniqueEmail)).thenReturn(false);

        // then
        memberService.emailCheck(uniqueEmail); // 예외가 발생하지 않으면 성공
    }
}