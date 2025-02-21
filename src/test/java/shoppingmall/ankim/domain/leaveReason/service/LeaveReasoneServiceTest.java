package shoppingmall.ankim.domain.leaveReason.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shoppingmall.ankim.domain.leaveReason.dto.LeaveReasonResponse;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LeaveReasoneServiceTest {

    @Mock
    private LeaveReasonRepository leaveReasonRepository;

    @InjectMocks
    private LeaveReasoneServiceImpl leaveReasoneService;

    @DisplayName("탈퇴 사유를 조회한다.")
    @Test
    void getReason() {
        // given
        List<LeaveReason> leaveReasons = Arrays.asList(
                new LeaveReason(1L, "탈퇴 후 재가입을 위해서", "Y"),
                new LeaveReason(2L, "사고 싶은 상품이 없어서", "Y"),
                new LeaveReason(3L, "자주 이용하지 않아서", "Y")
        );

        // LeaveReasonResponse는 LeaveReason 객체들을 감싸는 DTO 역할을 한다고 가정
        List<LeaveReasonResponse> expectedResponse = Arrays.asList(
                LeaveReasonResponse.of(leaveReasons.get(0)),
                LeaveReasonResponse.of(leaveReasons.get(1)),
                LeaveReasonResponse.of(leaveReasons.get(2))
        );

        // Repository mock 설정
        when(leaveReasonRepository.findAllByActiveYn("Y")).thenReturn(leaveReasons);

        // when
        List<LeaveReasonResponse> actualResponse = leaveReasoneService.getReason();

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse, actualResponse);
        verify(leaveReasonRepository, times(1)).findAllByActiveYn("Y");
    }
}