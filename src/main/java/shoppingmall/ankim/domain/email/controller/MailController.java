package shoppingmall.ankim.domain.email.controller;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.global.response.ApiResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    // 메일 전송 요청 처리
    @PostMapping("/send")
    @ResponseBody
    public ApiResponse<String> sendMail(
            @RequestParam("id")
            @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            String email) {
        String code = mailService.generateCode(); // 인증번호 생성
        MimeMessage mail = mailService.createMail(email, code); // 메일 생성
        mailService.sendMail(mail); // 메일 전송

        return ApiResponse.ok("메일 전송 완료");
    }

    // 인증번호 검증 요청 처리
    @PostMapping("/verify")
    @ResponseBody
    public ApiResponse<String> existByEmail(@Valid @RequestBody MailRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ApiResponse.of(bindingResult);
        }

        boolean isValid = mailService.verifyCode(request.getEmail(), request.getVerificationCode());
        return isValid ? ApiResponse.ok("OK") : ApiResponse.ok(HttpStatus.OK, "FAIL");
    }

    // 이메일 인증 페이지 렌더링
    @GetMapping("/emailVerification")
    public String emailVerificationPage() {
        return "emailVerification"; // emailVerification.html 파일을 호출
    }

}
