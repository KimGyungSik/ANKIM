package shoppingmall.ankim.domain.memberLeave.service;

import com.mysema.commons.lang.Pair;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReasonNotFoundException;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.memberLeave.entity.MemberLeave;
import shoppingmall.ankim.domain.memberLeave.repository.MemberLeaveRepository;
import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static shoppingmall.ankim.factory.MemberFactory.createMemberAndLeaveReason;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class MemberLeaveServiceTest {

    @Autowired
    private MemberLeaveService memberLeaveService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LeaveReasonRepository leaveReasonRepository;

    @Autowired
    private MemberLeaveRepository memberLeaveRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("존재하지 않는 탈퇴 사유로 요청 시 탈퇴할 수 없다.")
    void leaveMember_fail_withInvalidLeaveReason() {
        // given
        String loginId = "test@example.com";
        String validPassword = "password123";

        // Factory를 통해 Member와 LeaveReason 생성
        Pair<Member, List<LeaveReason>> result = createMemberAndLeaveReason(em, loginId, validPassword, bCryptPasswordEncoder);
        Member member = result.getFirst();

        // 유효하지 않은 LeaveReason ID
        Long invalidReasonNo = 999L;

        LeaveServiceRequest request = LeaveServiceRequest.builder()
                .password(validPassword)
                .leaveReasonNo(invalidReasonNo)
                .build();

        // when & then
        Exception exception = assertThrows(LeaveReasonNotFoundException.class, () -> {
            memberLeaveService.leaveMember(loginId, request);
        });

        // 예외 메시지 검증
        assertThat(exception.getMessage()).isEqualTo("탈퇴 사유를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("정상적인 입력으로 탈퇴 처리에 성공한다.")
    void leaveMember_success() {
        // given
        String loginId = "test@example.com";
        String validPassword = "password123";

        // Factory를 통해 Member와 LeaveReason 생성
        Pair<Member, List<LeaveReason>> result = createMemberAndLeaveReason(em, loginId, validPassword, bCryptPasswordEncoder);
        Member member = result.getFirst();
        List<LeaveReason> leaveReasons = result.getSecond();

        // "기타" 사유의 LeaveReason 선택
        LeaveReason etcReason = leaveReasons.stream()
                .filter(reason -> "기타".equals(reason.getReason()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("기타 사유가 존재하지 않습니다."));

        String leaveMessage = "탈퇴 테스트 사유입니다.";

        LeaveServiceRequest request = LeaveServiceRequest.builder()
                .password(validPassword)
                .leaveReasonNo(etcReason.getNo())
                .leaveMessage(leaveMessage)
                .build();

        // when
        memberLeaveService.leaveMember(loginId, request);

        // then
        Member updatedMember = memberRepository.findById(member.getNo()).orElseThrow();
        assertThat(updatedMember.getLoginId()).isNotEqualTo(loginId); // loginId 변경 확인
        assertThat(updatedMember.getStatus()).isEqualTo(MemberStatus.LEAVE); // 상태 변경 확인

        // MemberLeave 테이블에 저장된 이력 검증
        List<MemberLeave> leaveHistory = memberLeaveRepository.findAll();
        assertThat(leaveHistory).hasSize(1); // 탈퇴이력이 저장되어야 됨

        MemberLeave savedLeave = leaveHistory.get(0);

        assertThat(savedLeave.getMember().getLoginId()).isNotEqualTo(loginId); // 로그인 ID 검증
        assertThat(savedLeave.getLeaveReason().getReason()).isEqualTo("기타"); // 탈퇴 사유 검증
        assertThat(savedLeave.getLeaveMsg()).isEqualTo(leaveMessage); // 기타 사유 메시지 검증
        assertThat(savedLeave.getLeaveAt()).isNotNull(); // 탈퇴 날짜 검증
    }

}