package shoppingmall.ankim.domain.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;

@RestController
public class MemberJoinController {

    @PostMapping("/join")
    public String joinProcess(MemberRegisterRequest memberRegisterRequest) {

        return "ok";
    }
}
