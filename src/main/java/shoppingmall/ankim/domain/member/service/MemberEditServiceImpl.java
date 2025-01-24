package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.member.dto.MemberAddressResponse;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.dto.TermsAgreementResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.member.service.request.PasswordServiceRequest;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;
import shoppingmall.ankim.domain.memberHistory.handler.MemberHistoryHandler;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberEditServiceImpl implements MemberEditService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberHistoryRepository memberHistoryRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final TermsHistoryRepository termsHistoryRepository;

    @Override
    public void isValidPassword(String loginId, PasswordServiceRequest request) {
        Member member = getMember(loginId);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword());
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
    public void changePassword(String loginId, ChangePasswordServiceRequest request) {
        Member member = getMember(loginId);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(request.getOldPassword(), member.getPassword());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_CURRENT_PASSWORD);
        }

        // 새로운 비밀번호 비교(새로운 비밀번호, 확인용 비밀번호 비교)
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidMemberException(PASSWORD_CONFIRMATION_MISMATCH);
        }

        // 새로운 비밀번호가 이전 비밀번호와 동일한지 비교
        if (bCryptPasswordEncoder.matches(request.getNewPassword(), member.getPassword())) {
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

    @Override
    public MemberInfoResponse getMemberInfo(String loginId) {
        Member member = getMember(loginId);


        // 기본 배송지 조회
        MemberAddressResponse addressResponse = memberAddressRepository.findDefaultAddressByMember(member)
                .map(MemberAddressResponse::of)
                .orElse(null);

        // 약관 동의 내역 조회 (findAgreedTermsByMember 활용)
        Long parentTermsNo = 1L; // 회원가입 약관의 최상위 부모 번호 (필요시 변경)
        List<TermsAgreementResponse> agreedTerms = termsHistoryRepository.findAgreedTermsByMember(member.getNo(), parentTermsNo, "Y");


        // MemberInfoResponse 생성 후 반환
        // FIXME MemberInfoResponse 생성 작업 필요
//        return MemberInfoResponse.of(member, addressResponse, agreedTerms);
        return null;
    }

    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}