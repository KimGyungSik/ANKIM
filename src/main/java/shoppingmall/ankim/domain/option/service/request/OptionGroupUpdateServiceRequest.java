package shoppingmall.ankim.domain.option.service.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptionGroupUpdateServiceRequest {
    private Long groupId; // 수정할 그룹 ID
    private String groupName; // 그룹명
    private List<OptionValueUpdateServiceRequest> optionValues; // 옵션 값 수정 요청 리스트
}
