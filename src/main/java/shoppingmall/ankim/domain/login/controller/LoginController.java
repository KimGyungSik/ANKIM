package shoppingmall.ankim.domain.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/member")
    public String loginMember() {
        return "login/loginFormMember"; // 사용자 로그인 선택 페이지
    }

    @GetMapping("/email")
    public String loginEmail(Model model) {
        return "login/loginEmail"; // 사용자 이메일 로그인 페이지
    }

    @GetMapping("/admin")
    public String loginAdmin() {
        return "loginFormAdmin"; // FIXME 관리자 로그인 페이지
    }

}
