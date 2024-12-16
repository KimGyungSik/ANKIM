package shoppingmall.ankim.domain.memberLeave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReasonNotFoundException;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;
import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberLeaveServiceImpl implements MemberLeaveService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LeaveReasonRepository leaveReasonRepository;

    /*
    * 1. 로그인되어있는 회원정보를 불러온다. ( O )
    * 2. 입력된 비밀번호를 검증한다.
    * 3. 탈퇴 사유를 조회한다. (LeaveReasom)
    * 4. 탈퇴 사유를 저장한다.
    * 5. 회원 상태를 탈퇴로 변경한다.
    * 6. loginId를 무작위 값으로 변경한다.
    * */
    @Override
    public void leaveMember(String loginId, LeaveServiceRequest request) {
        Member member = getMember(loginId);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(request.getPassword(), member.getPwd());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_PASSWORD);
        }

        // 탈퇴 사유 조회
        Optional<LeaveReason> leaveReason = leaveReasonRepository.findByNo(request.getLeaveReasonNo());
        if (leaveReason.isEmpty()) {
            throw new LeaveReasonNotFoundException(LEAVE_REASON_NOT_FOUND);
        }


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
