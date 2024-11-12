package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;

@Controller
@RequiredArgsConstructor
public class MemberController {

    @GetMapping("/")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/join")
    public String joinProcess() {

        return "welcome"; // FIXME 회원가입 페이지 작성 필요
    }
}
