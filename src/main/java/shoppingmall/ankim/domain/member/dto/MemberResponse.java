package shoppingmall.ankim.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shoppingmall.ankim.domain.member.entity.Member;

@Data
@NoArgsConstructor
@ToString(of = {"name"})
public class MemberResponse {

    private String name;

    @Builder
    public MemberResponse(String name) {
        this.name = name;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .name(maskName(member.getName()))
                .build();
    }

    // 이름의 첫 글자와 마지막 글자를 제외한 중간 글자를 *로 변경하는 로직
    // 만약 두글자 이름이면 마지막 글자를 *로 변경
    public static String maskName(String name) {
        if(name.length() == 2) {
            return name.charAt(0) + "*";
        }

        String firstChar = name.substring(0, 1);
        String lastChar = name.substring(name.length() - 1);
        String middle = "*".repeat(name.length() - 2);

        return firstChar + middle + lastChar;
    }
}