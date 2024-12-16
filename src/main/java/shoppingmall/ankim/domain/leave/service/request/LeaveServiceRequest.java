package shoppingmall.ankim.domain.leave.service.request;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class LeaveServiceRequest {
    private String leaveMessage;
    private String agreeYn;
    private String password;
    @Builder
    public LeaveServiceRequest(String leaveMessage, String agreeYn, String password) {
        this.leaveMessage = leaveMessage;
        this.agreeYn = agreeYn;
        this.password = password;
    }
}