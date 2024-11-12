package shoppingmall.ankim.domain.option.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.exception.DuplicateOptionValueException;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.category.entity.CategoryLevel.SUB;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

/*
    * 옵션 항목 정책
        * 최소 1개 ~ 4개까지 선택 가능
        * 사이즈, 컬러,... 등등
*/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "option_group")
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product;

    // 옵션값에 대한 필드 리스트
    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionValue> optionValues = new ArrayList<>();

    @Column(length = 255)
    private String name;

    @Builder
    private OptionGroup(String name, Product product, List<OptionValue> optionValues) {
        this.name = name;
        this.product = product;
        this.optionValues = optionValues != null ? optionValues : new ArrayList<>();
    }

    public static OptionGroup create(String name, Product product) {
        return OptionGroup.builder()
                .name(name)
                .product(product)
                .build();
    }
    //  OptionValue 추가 메서드
    public void addOptionValue(OptionValue optionValue) {
        boolean isDuplicate = optionValues.stream()
                .anyMatch(existingValue -> existingValue.getName().equals(optionValue.getName()));

        if (isDuplicate) {
            throw new DuplicateOptionValueException(DUPLICATE_OPTION_VALUE);
        }

        optionValues.add(optionValue);
    }
    public void removeOptionValue(OptionValue optionValue) {
        this.optionValues.remove(optionValue);
    }

    // OptionGroupResponse를 OptionGroup 엔티티로 변환하는 메서드
    public static OptionGroup fromResponse(OptionGroupResponse response, Product product) {
        OptionGroup optionGroup = OptionGroup.builder()
                .name(response.getGroupName())
                .product(product)
                .build();

        List<OptionValue> optionValues = response.getOptionValueResponses().stream()
                .map(optionValueResponse -> OptionValue.fromResponse(optionValueResponse, optionGroup))
                .toList();
        optionGroup.optionValues.addAll(optionValues);

        return optionGroup;
    }

}

