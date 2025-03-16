package shoppingmall.ankim.domain.leaveReason.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.member.dto.MemberAddressResponse;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.global.util.MaskingUtil;

import java.util.List;

@Data
@NoArgsConstructor
public class LeaveReasonResponse {
    private Long no; // 탈퇴 사유번호
    private String reason; // 탈퇴 사유명

    @Builder
    public LeaveReasonResponse(Long no, String reason) {
        this.no = no;
        this.reason = reason;
    }

    public static LeaveReasonResponse of(LeaveReason leaveReason) {
        return LeaveReasonResponse.builder()
                .no(leaveReason.getNo())
                .reason(leaveReason.getReason())
                .build();
    }

}
