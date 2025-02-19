package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.handler.LogoutHandler;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MemberMyPageController {

    private final MemberEditService memberEditService;
    private final SecurityContextHelper securityContextHelper;

    @GetMapping
    public String myPage() {

        return "mypage/mypage"; // FIXME 마이 페이지
    }

    @GetMapping("/edit/info")
    public String editInfo() {
//    public String editInfo(Model model) {
//        String loginId = securityContextHelper.getLoginId();
//
//        if(loginId == null) {
//            log.error("loginId is null");
//        }
//
//        // 회원 정보 가져오기
//        MemberInfoResponse response = memberEditService.getMemberInfo(loginId);
//
//        // 모델에 회원 정보 추가 (Thymeleaf에서 사용할 수 있도록)
//        model.addAttribute("memberInfo", response);

        return "mypage/myInfo"; // FIXME 회원정보 수정 페이지
    }

}
