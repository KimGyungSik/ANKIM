package shoppingmall.ankim.domain.memberLeave.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.memberLeave.controller.request.LeaveRequest;
import shoppingmall.ankim.domain.memberLeave.service.MemberLeaveService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/leave")
public class MemberLeaveController {

    @GetMapping("")
    public String confirmPassword() {
        return "mypage/leave";
    }
}