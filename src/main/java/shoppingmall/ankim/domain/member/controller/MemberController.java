package shoppingmall.ankim.domain.member.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;

@RestController
public class MemberController {

    @GetMapping("/")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/join")
    public String joinProcess(MemberRegisterRequest memberRegisterRequest) {

        return "ok"; // 회원가입 페이지로 이동
    }
}
