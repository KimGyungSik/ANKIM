package shoppingmall.ankim.domain.email.controller;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.service.Count;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailApiController {

    private final MailService mailService;

    // 메일 전송 요청 처리
    @PostMapping("/send")
    public ApiResponse<String> sendMail(
            @RequestParam("loginId")
            @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            String loginId) {
        String code = mailService.generateCode(); // 인증번호 생성
        MimeMessage mail = mailService.createMail(loginId, code); // 메일 생성
        mailService.sendMail(mail); // 메일 전송

        return ApiResponse.ok("메일 전송 완료");
    }

    // 인증번호 검증 요청 처리
    @PostMapping("/verify")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MailRequest request) {
        Count isValid = mailService.verifyCode(request.getLoginId(), request.getVerificationCode());

        if (isValid == Count.RETRY) {
            // 인증번호 3번 이상 틀린 경우
            return ApiResponse.ok(HttpStatus.NO_CONTENT, isValid.name()); // "RETRY" 반환
        } else if (isValid == Count.SUCCESS) {
            // 인증번호 제대로 입력한 경우
            return ApiResponse.ok(HttpStatus.OK, isValid.name()); // "OK" 반환
        } else {
            // 인증번호 입력을 잘못한 경우
            return ApiResponse.ok(HttpStatus.NO_CONTENT, isValid.name()); // "FAIL" 반환
        }
    }

}
