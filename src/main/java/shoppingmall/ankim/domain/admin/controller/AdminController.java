package shoppingmall.ankim.domain.admin.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.admin.controller.request.AdminRegisterRequest;
import shoppingmall.ankim.domain.admin.service.AdminService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String admin() {
        return "관리자 페이지";
    }

    @GetMapping("/join")
    @ResponseBody
    public String joinProcess(Model model) {
        return "관리자 회원가입 페이지";
    }

}
