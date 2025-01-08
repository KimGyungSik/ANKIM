package shoppingmall.ankim.domain.terms.controller.request;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/api/terms")
@RequestMapping("/join")
public class TermsController {

    private final TermsQueryService termsQueryService;
    private final HttpSession httpSession;

    @ModelAttribute("termsAgreements")
    public List<TermsAgreement> termsAgreements() {
        return new ArrayList<>(); // termsAgreements 리스트 초기화
    }

    // 이메일로 가입하기 누르는 경우 약관동의 페이지로 이동한다.
    @GetMapping("/email")
    public String getJoinTerms(Model model) {
        httpSession.removeAttribute("termsAgreements");  // 약관 동의 초기화

        List<TermsJoinResponse> termsList = termsQueryService.findJoinTerm();

        model.addAttribute("termsList", termsList);

        return "join/termsJoin"; // FIXME 약관 동의 페이지 작성
    }
}
