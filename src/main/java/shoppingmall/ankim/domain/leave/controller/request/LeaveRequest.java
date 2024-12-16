package shoppingmall.ankim.domain.leave.controller.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.leave.service.request.LeaveServiceRequest;
@Data
@NoArgsConstructor
public class LeaveRequest {
    @NotBlank
    private String leaveMessage;
    @NotBlank
    private String agreeYn;
    @NotBlank
    private String password;
    @Builder
    public LeaveRequest(String leaveMessage, String agreeYn, String password) {
        this.leaveMessage = leaveMessage;
        this.agreeYn = agreeYn;
        this.password = password;
    }
    LeaveServiceRequest toServiceRequest() {
        return LeaveServiceRequest.builder()
                .leaveMessage(this.leaveMessage)
                .agreeYn(this.agreeYn)
                .password(this.password)
                .build();
    }
}