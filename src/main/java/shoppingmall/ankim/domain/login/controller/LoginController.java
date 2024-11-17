package shoppingmall.ankim.domain.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/member")
    public String loginMember() {
        return "loginFormMember"; // FIXME 사용자 로그인 페이지
    }

    @GetMapping("/admin")
    public String loginAdmin() {
        return "loginFormAdmin"; // FIXME 관리자 로그인 페이지
    }

}
