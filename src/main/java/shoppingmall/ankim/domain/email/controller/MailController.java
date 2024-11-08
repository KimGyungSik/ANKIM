package shoppingmall.ankim.domain.email.controller;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    // 메일 전송 요청 처리
    @PostMapping("/send")
    public ApiResponse<String> sendMail(@RequestParam String email) {
        String code = mailService.generateCode(); // 인증번호 생성
        MimeMessage mail = mailService.createMail(email, code); // 메일 생성
        mailService.sendMail(mail); // 메일 전송

        return ApiResponse.ok("메일 전송 완료");
    }

    // 인증번호 검증 요청 처리
    @PostMapping("/verify")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MailRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ApiResponse.of(bindingResult);
        }

        boolean isValid = mailService.verifyCode(request.getEmail(), request.getVerificationCode());
        return isValid ? ApiResponse.ok("OK") : ApiResponse.ok(HttpStatus.OK, "FAIL");
    }


}
