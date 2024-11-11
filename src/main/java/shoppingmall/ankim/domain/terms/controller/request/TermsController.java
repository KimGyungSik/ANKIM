package shoppingmall.ankim.domain.terms.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermsController {

    private final TermsQueryService termsQueryService;

    // 약관 동의 후 다음 이메일 입력 페이지로 이동
    @PostMapping("/join")
    public String getJoinTerms(Model model) {
        List<TermsJoinResponse> termsList = termsQueryService.findJoinTerm();
        model.addAttribute("termsList", termsList);

        return "registerEmail"; // 다음 입력 페이지 (예: registerEmail.html) -> 작성 필요
    }
}
