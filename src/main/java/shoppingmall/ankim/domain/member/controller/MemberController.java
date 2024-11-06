package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.service.MemberService;

@RestController
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping("/")
    public String main(Model model) {
        return "main";
    }

    @PostMapping("/members/email-check")
    public ResponseEntity<String> existByEmail(@Valid @RequestBody MemberEmailRequest request, BindingResult bindingResult) {
        // 이메일 형식 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError("id").getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        System.out.println("id :" + request.getId());
        // 이메일 형식이 올바르면 중복 확인 로직으로 이동
        boolean isDuplicate = memberService.emailCheck(request.getId());
        if (isDuplicate) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }

        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
}
