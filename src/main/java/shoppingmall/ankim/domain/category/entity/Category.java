package shoppingmall.ankim.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_no",referencedColumnName = "no",insertable=false, updatable=false)
    private Category parent;

    @Column(name = "parent_no")
    private Long parentNo;
    private Long level; //대분류 1L, 중분류 2L, 소분류 3L.
    @Column(length = 255)
    private String name;

    @Builder
    private Category(Long no, Long parentNo, Long level, String name, Category parent) {
        this.no = no;
        this.parentNo = parentNo;
        this.level = level;
        this.name = name;
        this.parent = parent;
    }

    public void setParentId(Category category){
        this.parent = category;
    }
    // Getters and Setters
}
