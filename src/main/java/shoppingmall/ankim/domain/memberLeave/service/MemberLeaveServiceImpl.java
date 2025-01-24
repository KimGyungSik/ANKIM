package shoppingmall.ankim.domain.memberLeave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReasonNotFoundException;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.memberLeave.entity.MemberLeave;
import shoppingmall.ankim.domain.memberLeave.exception.MemberLeaveException;
import shoppingmall.ankim.domain.memberLeave.repository.MemberLeaveRepository;
import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.handler.RedisHandler;

import java.time.LocalDateTime;
import java.util.Optional;

import static shoppingmall.ankim.domain.memberHistory.handler.MemberHistoryHandler.handleStatusChange;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberLeaveServiceImpl implements MemberLeaveService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LeaveReasonRepository leaveReasonRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final MemberHistoryRepository memberHistoryRepository;
    private final RedisHandler redisHandler;

    /*
    * 1. 로그인되어있는 회원정보를 불러온다. ( O )
    * 2. 입력된 비밀번호를 검증한다.
    * 3. 탈퇴 사유를 조회한다. (LeaveReasom)
    * 4. 탈퇴 사유를 저장한다.
    * 5. 회원 상태를 탈퇴로 변경한다.
    * 6. loginId를 무작위 값으로 변경한다.
    * 7. access, refresh 토큰 삭제
    * */
    @Override
    public void leaveMember(String loginId, LeaveServiceRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Member member = getMember(loginId);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_PASSWORD);
        }

        // 탈퇴 사유 조회
        Optional<LeaveReason> findLeaveReason = leaveReasonRepository.findByNo(request.getLeaveReasonNo());
        if (findLeaveReason.isEmpty()) {
            throw new LeaveReasonNotFoundException(LEAVE_REASON_NOT_FOUND);
        }
        LeaveReason leaveReason = findLeaveReason.get();

        // 탈퇴사유가 기타가 아닌 경우 LeaveReason 엔티티의 해당하는 탈퇴 사유를 담아줘야됨
        String leaveMsg = request.getLeaveMessage();
        if (!"기타".equals(leaveReason.getReason())) {
            leaveMsg = leaveReason.getReason();
        }

        // 탈퇴 사유 저장
        MemberLeave memberLeave = MemberLeave.builder()
                .member(member)
                .leaveReason(leaveReason)
                .leaveMsg(leaveMsg)
                .leaveAt(now)
                .build();

        try {
            memberLeaveRepository.save(memberLeave);

            // 회원 상태를 탈퇴로 변경
            MemberHistory history = handleStatusChange(member, MemberStatus.LEAVE);
            memberHistoryRepository.save(history);
            member.leave(); // 회원 상태 및 loginId 변경

            // header와 redis에서 access 토큰 삭제, cookie에서 refresh토큰 삭제

        } catch (Exception e) {
            throw new MemberLeaveException(MEMBER_LEAVE_FAILED);
        }
    }

    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw  new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}
