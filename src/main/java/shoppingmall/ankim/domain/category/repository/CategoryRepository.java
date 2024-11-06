package shoppingmall.ankim.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;
import shoppingmall.ankim.domain.category.repository.query.CategoryQueryRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category,Long>, CategoryQueryRepository {
    // 중분류와 그에 속한 소분류를 모두 가져오는 쿼리
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.level = 'MIDDLE'")
    List<Category> findAllMiddleCategoriesWithSubCategories();
    // 특정 중분류 ID에 속한 모든 소분류를 조회
    List<Category> findByParent_IdAndLevel(Long middleCategoryId, CategoryLevel level);
    // 소분류 ID로 해당 소분류의 상위 중분류를 조회
    @Query("SELECT c.parent FROM Category c WHERE c.no = :subCategoryId AND c.level = 'SUB'")
    Optional<Category> findMiddleCategoryBySubCategoryId(@Param("subCategoryId") Long subCategoryId);
    // 중분류만 조회
    List<Category> findByLevel(CategoryLevel level);
    @Query("SELECT sc FROM Category sc WHERE sc.parent.no = :middleCategoryId AND sc.level = 'SUB'")
    List<Category> findSubCategoriesByMiddleCategoryId(@Param("middleCategoryId") Long middleCategoryId);


}
