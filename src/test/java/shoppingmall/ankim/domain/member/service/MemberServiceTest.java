package shoppingmall.ankim.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    
    @Test
    @DisplayName("이메일 중복 검증 테스트")
    void emailCheckTest() {
        String email = "test@ankim.com";
        memberService.emailCheck(email);

    }
}