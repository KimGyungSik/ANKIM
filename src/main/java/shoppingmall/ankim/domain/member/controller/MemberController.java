package shoppingmall.ankim.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/member")
    public String createForm(Model model) {
        return "member Controller";
    }
}
