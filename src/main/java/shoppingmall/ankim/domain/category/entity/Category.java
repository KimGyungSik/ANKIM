package shoppingmall.ankim.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.stream.Collectors.toList;


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

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryLevel level; // 중분류(MIDDLE) 또는 소분류(SUB) 구분

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_no")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent; // 중분류의 경우 null, 소분류의 경우 상위 중분류 참조

    @OneToMany(mappedBy = "parent", cascade = ALL)
    private List<Category> subCategories = new ArrayList<>(); // 중분류가 갖는 소분류 목록

    @Builder
    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
        this.level = parent != null ? CategoryLevel.SUB  : CategoryLevel.MIDDLE;
    }

    public void addSubCategories(Category child) {
        this.subCategories.add(child);
        child.parent = this;
    }

    public static List<Long> extractLowestCategoryIds(Category category) {

        if (category.getSubCategories() == null || category.getSubCategories().isEmpty()) {
            return List.of(category.getNo());
        }

        return category.getSubCategories()
                .stream()
                .map(Category::extractLowestCategoryIds)
                .flatMap(List::stream)
                .collect(toList());
    }

}

