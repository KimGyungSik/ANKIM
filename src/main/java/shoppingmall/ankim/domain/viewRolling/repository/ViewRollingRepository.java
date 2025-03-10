package shoppingmall.ankim.domain.viewRolling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.viewRolling.entity.ViewRolling;

import java.util.List;

public interface ViewRollingRepository extends JpaRepository<ViewRolling, Long> {

    @Modifying
    @Query(value = """
    UPDATE view_rolling 
    SET total_views = total_views + 1, last_updated = NOW() 
    WHERE prod_no = :productNo AND period = 'REALTIME'
    """, nativeQuery = true)
    void increaseRealTimeViewCount(@Param("productNo") Long productNo);



    @Modifying
    @Query(value = """
    UPDATE view_rolling v 
    JOIN (SELECT prod_no, SUM(total_views) AS sum_views
          FROM view_rolling WHERE period = 'REALTIME' 
          GROUP BY prod_no) r
    ON v.prod_no = r.prod_no AND v.period = 'DAILY'
    SET v.total_views = v.total_views + r.sum_views, v.last_updated = NOW()
    """, nativeQuery = true)
    void rollupRealTimeToDaily();


    @Modifying
    @Query(value = """
    UPDATE view_rolling v 
    JOIN (SELECT prod_no, SUM(total_views) AS sum_views
          FROM view_rolling WHERE period = 'DAILY' 
          AND last_updated >= DATE_SUB(NOW(), INTERVAL 7 DAY)
          GROUP BY prod_no) r
    ON v.prod_no = r.prod_no AND v.period = 'WEEKLY'
    SET v.total_views = v.total_views + r.sum_views, v.last_updated = NOW()
    """, nativeQuery = true)
    void rollupDailyToWeekly();



    @Modifying
    @Query(value = """
    UPDATE view_rolling v 
    JOIN (SELECT prod_no, SUM(total_views) AS sum_views
          FROM view_rolling WHERE period = 'WEEKLY' 
          AND last_updated >= DATE_SUB(NOW(), INTERVAL 1 MONTH)
          GROUP BY prod_no) r
    ON v.prod_no = r.prod_no AND v.period = 'MONTHLY'
    SET v.total_views = v.total_views + r.sum_views, v.last_updated = NOW()
    """, nativeQuery = true)
    void rollupWeeklyToMonthly();


    @Modifying
    @Query(value = """
        INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
        VALUES 
            (:categoryNo, :productNo, 'REALTIME', 0, NOW()),
            (:categoryNo, :productNo, 'DAILY', 0, NOW()),
            (:categoryNo, :productNo, 'WEEKLY', 0, NOW()),
            (:categoryNo, :productNo, 'MONTHLY', 0, NOW())
        """, nativeQuery = true)
    void initializeViewRolling(@Param("categoryNo") Long categoryNo, @Param("productNo") Long productNo);

    List<ViewRolling> findByProduct_No(Long productNo);

    @Modifying
    @Query(value = """
    UPDATE view_rolling v
    JOIN (SELECT prod_no, total_views FROM view_rolling WHERE period = 'REALTIME') r
    ON v.prod_no = r.prod_no AND v.period = 'REALTIME'
    SET v.total_views = v.total_views - r.total_views, v.last_updated = NOW()
    WHERE v.total_views >= r.total_views
    """, nativeQuery = true)
    void subtractRealTimeViews(); // 🔥 REALTIME 롤업 후 차감

    @Modifying
    @Query(value = """
    UPDATE view_rolling v
    JOIN (SELECT prod_no, total_views FROM view_rolling WHERE period = 'DAILY') r
    ON v.prod_no = r.prod_no AND v.period = 'DAILY'
    SET v.total_views = v.total_views - r.total_views, v.last_updated = NOW()
    WHERE v.total_views >= r.total_views
    """, nativeQuery = true)
    void subtractDailyViews(); // 🔥 DAILY 롤업 후 차감

    @Modifying
    @Query(value = """
    UPDATE view_rolling v
    JOIN (SELECT prod_no, total_views FROM view_rolling WHERE period = 'WEEKLY') r
    ON v.prod_no = r.prod_no AND v.period = 'WEEKLY'
    SET v.total_views = v.total_views - r.total_views, v.last_updated = NOW()
    WHERE v.total_views >= r.total_views
    """, nativeQuery = true)
    void subtractWeeklyViews(); // 🔥 WEEKLY 롤업 후 차감

}

