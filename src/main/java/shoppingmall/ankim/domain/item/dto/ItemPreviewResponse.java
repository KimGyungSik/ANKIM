package shoppingmall.ankim.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPreviewResponse {
    private String name; // 조합된 옵션 이름 (e.g., "컬러: Blue, 사이즈: Large")
    private List<String> optionValueNames; // 옵션 값 이름 리스트
}
