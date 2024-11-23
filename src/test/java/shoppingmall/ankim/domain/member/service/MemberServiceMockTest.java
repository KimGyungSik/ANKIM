package shoppingmall.ankim.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class MemberServiceMockTest {

    @Autowired
    MemberService memberService;

    @MockBean
    private MemberRepository mockMemberRepository;  // Mock 테스트에 사용되는 memberRepository

    @Test
    @DisplayName("중복된 이메일로 검증 시 예외가 발생한다.")
    void emailCheckDuplicateLoginIdTest() {
        // given
        String duplicateEmail = "test@ankim.com";

        // when : 중복 이메일이 존재한다고 가정
        when(mockMemberRepository.existsByLoginId(duplicateEmail)).thenReturn(true);

        // then
        assertThrows(MemberRegistrationException.class, () -> memberService.isLoginIdDuplicated(duplicateEmail));
    }

    @Test
    @DisplayName("중복되지 않은 이메일로 검증 시 예외가 발생하지 않는다.")
    void emailCheckUniqueLoginIdTest() {
        // given
        String uniqueEmail = "unique@example.com";

        // when
        when(mockMemberRepository.existsByLoginId(uniqueEmail)).thenReturn(false);

        // then
        memberService.isLoginIdDuplicated(uniqueEmail); // 예외가 발생하지 않으면 성공
    }

}