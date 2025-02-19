package shoppingmall.ankim.domain.email.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    // 이메일 인증 페이지 렌더링
    @GetMapping("/mailVerification")
    public String emailVerificationPage() {
        return "join/mailVerification"; // emailVerification.html 파일을 호출
    }

    @GetMapping("/mailVerificationFragment")
    public String emailVerificationFragment() {
        return "join/mailVerification :: main-content"; // Fragment를 반환하도록 설정
    }

}
