package shoppingmall.ankim.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shoppingmall.ankim.domain.admin.entity.Admin;

@Data
@NoArgsConstructor
@ToString(of = {"name"})
public class AdminResponse {

    private Long no;
    private String name;

    @Builder
    public AdminResponse(Long no, String name) {
        this.no = no;
        this.name = name;
    }

    public static AdminResponse of(Admin admin) {
        return AdminResponse.builder()
                .no(admin.getNo())
                .name(admin.getName())
                .build();
    }
}
