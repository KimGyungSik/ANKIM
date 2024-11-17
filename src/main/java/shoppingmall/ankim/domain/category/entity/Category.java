package shoppingmall.ankim.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shoppingmall.ankim.domain.category.exception.CategoryNameTooLongException;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.SUB;
import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_NAME_TOO_LONG;


/**
 카테고리 정책
 * 검색/필터 정보 등이 카테고리 선택 시 들어가야함
 * 판매상태가 '판매중'인 상품은 카테고리 수정불가
 * 카테고리 등록 : 중분류, 소분류 카테고리 추가 가능
 * 카테고리 수정 : 이름만 바꾸도록
 * 카테고리 삭제 : 소분류 삭제 가능, 중분류 삭제 시 그에 해당하는 소분류 카테고리도 삭제
 * 상품 등록  : 중분류 카테고리 선택 시 -> 중분류에 해당하는 소분류 카테고리 선택
 * 상품 검색 : 중분류 선택 시 (중분류 전체 상품 나열) -> 소분류 선택 시 (소분류 전체 상품 나열)
 */

@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category extends BaseEntity {
    private static final int NAME_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryLevel level; // 중분류(MIDDLE) 또는 소분류(SUB) 구분

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_no")
    private Category parent; // 중분류의 경우 null, 소분류의 경우 상위 중분류 참조

    @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
    private List<Category> childCategories = new ArrayList<>(); // 중분류가 갖는 소분류 목록

    @Builder
    public Category(String name, List<Category> subCategories) {
        // 이름 길이 유효성 검사
        validateName(name);
        this.level = MIDDLE;
        // 초기 하위 카테고리 목록 설정 및 부모 관계 자동 설정
        if (subCategories != null && !subCategories.isEmpty()) {
            for (Category child : subCategories) {
                addSubCategory(child); // addSubCategory 메서드를 통해 하위 카테고리와의 관계 설정
            }
        }
    }
    public static Category create(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // 이름 길이 유효성 검사 메서드
    private void validateName(String name) {
        if (name == null || name.length() > NAME_MAX_LENGTH) {
            throw new CategoryNameTooLongException(CATEGORY_NAME_TOO_LONG);
        }
        this.name = name;
    }

    // 하위 카테고리 추가 메서드
    public void addSubCategory(Category subCategory) {
        subCategory.parent = this;
        subCategory.level = SUB;
        this.childCategories.add(subCategory);
    }

    // 모든 최하위 카테고리 ID 추출
    public static List<Long> extractLowestCategoryIds(Category category) {
        if (category.getChildCategories() == null || category.getChildCategories().isEmpty()) {
            return List.of(category.getNo());
        }
        return category.getChildCategories()
                .stream()
                .map(Category::extractLowestCategoryIds)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void updateName(String newName) {
        log.info("Updating category name from {} to {}", this.name, newName);
        validateName(newName); // 이름 유효성 검사
        this.name = newName;
    }

    public void changeParentCategory(Category newParentCategory) {
        this.parent = newParentCategory;
    }
}


