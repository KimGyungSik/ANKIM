package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;
import shoppingmall.ankim.domain.memberHistory.handler.MemberHistoryHandler;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberEditServiceImpl implements MemberEditService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberHistoryRepository memberHistoryRepository;

    @Override
    public void isValidPassword(String accessToken, String pwd) {
        Member member = getMember(accessToken);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(pwd, member.getPwd());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_PASSWORD);
        }
    }

    /*
    * TODO
    * [비밀번호 변경]
    * 1. 현재 비밀번호 비교 -> 일치 하지 않으면 현재 비빌번호를 정확히 입력하도록 안내
    * 2. 새로운 비밀번호 비교 -> 비밀번호가 일치하더라도 비밀번호 형식에 맞지 않으면 허용 X(request에서 valid로 수행)
    * 3. 새로운 비밀번호 비교 -> 비밀번호가 일치하고, 비밀번호 형식도 알맞게 입력한 경우 비밀번호 변경
    * 4. 새로운 비밀번호 비교 -> 기존의 비밀번호와 일치하는지 확인
    * 5. 변경 이력 테이블에 변경 전/후 입력
    * */
    @Override
    public void changePassword(String accessToken, ChangePasswordServiceRequest request) {
        Member member = getMember(accessToken);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(request.getOldPassword(), member.getPwd());
        if (!isPasswordValid) {
            throw new InvalidMemberException(CURRENT_PASSWORD_INVALID);
        }

        // 새로운 비밀번호 비교(새로운 비밀번호, 확인용 비밀번호 비교)
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidMemberException(PASSWORD_CONFIRMATION_MISMATCH);
        }

        // 새로운 비밀번호가 이전 비밀번호와 동일한지 비교
        if (bCryptPasswordEncoder.matches(request.getNewPassword(), member.getPwd())) {
            throw new InvalidMemberException(PASSWORD_SAME_AS_OLD);
        }

        // 비밀번호 변경을 위해 새로운 비밀번호 암호화
        String encodedNewPassword = bCryptPasswordEncoder.encode(request.getNewPassword());

        // 변경 이력 테이블에 기록
        MemberHistory history = MemberHistoryHandler.handlePasswordChange(member, encodedNewPassword);

        // 비밀번호 번경
        member.changePassword(encodedNewPassword);

        memberHistoryRepository.save(history);
    }

    private Member getMember(String accessToken) {
        // 토큰 유효성 검사(만료 검사도 들어있음)
        if (!jwtTokenProvider.isTokenValidate(accessToken)) {
            throw new JwtValidException(TOKEN_VALIDATION_ERROR);
        }
        // member의 loginId 추출
        String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}
