package shoppingmall.ankim.domain.option.service.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionValueUpdateServiceRequest {
    private Long valueId; // 수정할 값 ID
    private String valueName; // 옵션 값 이름
    private String colorCode; // 옵션 색상 코드
}
