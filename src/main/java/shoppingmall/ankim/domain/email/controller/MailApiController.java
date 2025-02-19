package shoppingmall.ankim.domain.email.controller;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.service.Count;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_MAIL_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailApiController {

    private final MailService mailService;

    // 메일 전송 요청 처리
    @PostMapping("/send")
    public ApiResponse<String> sendMail(
            @RequestParam("loginId")
            @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "유효하지 않은 이메일 형식입니다.")
            String loginId) {
        log.info("Received loginId: {}", loginId);
        if (loginId == null || loginId.isEmpty()) {
            log.error("loginId is missing or empty");
            throw new MemberRegistrationException(INVALID_MAIL_ID);
        }

        String code = mailService.generateCode(); // 인증번호 생성
        MimeMessage mail = mailService.createMail(loginId, code); // 메일 생성
        mailService.sendMail(mail); // 메일 전송

        return ApiResponse.ok("메일 전송 완료");
    }

    // 인증번호 검증 요청 처리
    @PostMapping("/verify")
    public ApiResponse<String> verifyMail(@Valid @RequestBody MailRequest request) {
        Count isValid = mailService.verifyCode(request.getLoginId(), request.getVerificationCode());

        if (isValid == Count.RETRY) {
            // 인증번호 3번 이상 틀린 경우
            return ApiResponse.ok(HttpStatus.NO_CONTENT, isValid.name()); // "RETRY" 반환
        } else if (isValid == Count.SUCCESS) {
            // 인증번호 제대로 입력한 경우
            return ApiResponse.ok(HttpStatus.OK, isValid.name()); // "SUCCESS" 반환
        } else {
            // 인증번호 입력을 잘못한 경우
            return ApiResponse.ok(HttpStatus.NO_CONTENT, isValid.name()); // "FAIL" 반환
        }
    }

}
