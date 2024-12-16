package shoppingmall.ankim.domain.memberLeave.service;

import com.mysema.commons.lang.Pair;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReasonNotFoundException;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static shoppingmall.ankim.factory.MemberFactory.createMemberAndLeaveReason;
import static shoppingmall.ankim.factory.MemberFactory.createSecureMember;

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

        LeaveServiceRequest request = LeaveServiceRequest.builder()
                .password(validPassword)
                .leaveReasonNo(etcReason.getNo())
                .build();

        // when
        memberLeaveService.leaveMember(loginId, request);

        // then


        // 추가적으로 탈퇴 처리가 되었는지 검증할 수 있음
    }

}