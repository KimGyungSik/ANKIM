package shoppingmall.ankim.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;
import shoppingmall.ankim.domain.category.repository.query.CategoryQueryRepository;



public interface CategoryRepository extends JpaRepository<Category,Long>, CategoryQueryRepository {
    // 특정 ID와 레벨을 통해 중분류 혹은 소분류가 존재하는지 확인
    boolean existsByNoAndLevel(Long categoryId, CategoryLevel level);

    // 특정 중분류에 소분류가 존재하는지 확인
    boolean existsByParentNo(Long parentNo);
    // 같은 이름의 카테고리가 존재하는지 확인
    boolean existsByNameAndLevel(String name, CategoryLevel level);

    // 중분류 아래 소분류 이름 중복 확인
    boolean existsByNameAndParentNo(String name, Long parentNo);
}
