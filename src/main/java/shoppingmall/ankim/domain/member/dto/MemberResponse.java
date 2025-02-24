package shoppingmall.ankim.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shoppingmall.ankim.domain.member.entity.Member;

import static shoppingmall.ankim.global.util.MaskingUtil.maskLoginId;
import static shoppingmall.ankim.global.util.MaskingUtil.maskName;

@Data
@NoArgsConstructor
@ToString(of = {"name"})
public class MemberResponse {

    private Long no;
    private String loginId;
    private String name;

    @Builder
    public MemberResponse(Long no, String loginId, String name) {
        this.no = no;
        this.loginId = loginId;
        this.name = name;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .no(member.getNo())
                .loginId(maskLoginId(member.getLoginId()))
                .name(maskName(member.getName()))
                .build();
    }
}