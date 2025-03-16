package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberMyPageServiceImpl implements MemberMyPageService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void isValidPassword(String loginId, String pwd) {

        Member member = getMember(loginId);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(pwd, member.getPassword());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_PASSWORD);
        }
    }

    @Override
    public MemberResponse getMemberInfo(String loginId) {
        Member member = getMember(loginId);

        MemberResponse memberResponse = MemberResponse.of(member);

        return memberResponse;
    }

    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            log.error("회원 정보가 존재하지 않습니다.");
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}
