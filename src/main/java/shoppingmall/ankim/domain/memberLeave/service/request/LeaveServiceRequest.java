package shoppingmall.ankim.domain.memberLeave.service.request;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class LeaveServiceRequest {
    private Long leaveReasonNo; // 선택한 탈퇴 사유 ID
    private String leaveMessage; // 기타 사유
    private String agreeYn; // 탈퇴 동의 여부
    private String password; // 비밀번호 검증 용

    @Builder
    public LeaveServiceRequest(Long leaveReasonNo, String leaveMessage, String agreeYn, String password) {
        this.leaveReasonNo = leaveReasonNo;
        this.leaveMessage = leaveMessage;
        this.agreeYn = agreeYn;
        this.password = password;
    }
}