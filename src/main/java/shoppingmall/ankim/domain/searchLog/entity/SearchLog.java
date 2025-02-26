package shoppingmall.ankim.domain.searchLog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.global.audit.BaseEntity;

@Entity
@Table(name = "search_logs", uniqueConstraints = {
        @UniqueConstraint(name = "unique_keyword", columnNames = "keyword")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private Integer searchCount;

    public SearchLog(String keyword) {
        this.keyword = keyword;
        this.searchCount = 1;
    }

    public void incrementSearchCount() {
        this.searchCount++;
    }
}
