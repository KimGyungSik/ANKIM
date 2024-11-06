package shoppingmall.ankim.domain.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.service.port.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    // 이메일 중복 검증
    public Boolean emailCheck(String id) {
        Boolean isExist = memberRepository.existsById(id);

        if (isExist) {
            // 조회 결과 있음 -> 사용 할 수 없음
            return false;
        }

        // 조회 결과 없음 -> 사용 가능한 id
        return true;
    }

    public void joinProcess(MemberRegisterRequest memberRegisterRequest) {

    }
}
