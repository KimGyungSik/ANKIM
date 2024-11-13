package shoppingmall.ankim.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.terms.entity.Terms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    MemberRepository memberRepository;  // 실제 DB 테스트에 사용되는 memberRepository

    @Test
    @DisplayName("회원가입 정보가 Member 테이블에 정상적으로 저장된다.")
    void registerMemberTest() {
        // given
        MemberRegisterServiceRequest request = MemberRegisterServiceRequest.builder()
                .id("test@example.com")
                .pwd("ValidPassword123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();
        List<Terms> terms = new ArrayList<>();

        // when
        memberService.registerMember(request, terms);

        // then
        Member savedMember = memberRepository.findByEmail(request.getId());

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
    }

}