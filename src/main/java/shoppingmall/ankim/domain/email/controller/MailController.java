package shoppingmall.ankim.domain.email.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    // 이메일 인증 페이지 렌더링
    @GetMapping("/emailVerification")
    public String emailVerificationPage() {
        return "emailVerification"; // emailVerification.html 파일을 호출
    }

}
