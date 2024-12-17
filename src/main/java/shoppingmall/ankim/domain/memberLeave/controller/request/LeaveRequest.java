package shoppingmall.ankim.domain.memberLeave.controller.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;
import shoppingmall.ankim.domain.memberLeave.validation.ValidLeaveMessage;

@Data
@NoArgsConstructor
@ValidLeaveMessage // 커스텀 유효성 검사 적용
public class LeaveRequest {
    @NotNull(message = "탈퇴 사유를 선택 해주세요.")
    private Long leaveReasonNo; // 선택한 탈퇴 사유 ID

    private String leaveReason; // 선택한 탈퇴 사유명

    private String leaveMessage; // 기타 사유(사용자 작성)

    @NotBlank(message = "탈퇴 유의 사항을 읽으신 후 체크해주세요.")
    private String agreeYn; // 탈퇴 동의 여부

    @NotBlank(message = "비밀번호를 정확하게 입력해주세요.")
    private String password; // 비밀번호 검증용

    @Builder
    public LeaveRequest(Long leaveReasonNo, String leaveReason, String leaveMessage, String agreeYn, String password) {
        this.leaveReasonNo = leaveReasonNo;
        this.leaveReason = leaveReason;
        this.leaveMessage = leaveMessage;
        this.agreeYn = agreeYn;
        this.password = password;
    }

    public LeaveServiceRequest toServiceRequest() {
        return LeaveServiceRequest.builder()
                .leaveReasonNo(leaveReasonNo)
                .leaveMessage(leaveMessage)
                .agreeYn(agreeYn)
                .password(password)
                .build();
    }
}