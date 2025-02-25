package shoppingmall.ankim.domain.searchLog.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.searchLog.entity.SearchLog;

import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SearchLog s SET s.searchCount = s.searchCount + 1 WHERE s.keyword = :keyword")
    int incrementSearchCount(@Param("keyword") String keyword);

    @Query("SELECT s.keyword FROM SearchLog s ORDER BY s.searchCount DESC")
    List<String> findTopKeywords(Pageable pageable);
}
