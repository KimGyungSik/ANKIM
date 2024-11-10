package shoppingmall.ankim.domain.option.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "option_value")
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optg_no", nullable = false)
    private OptionGroup optionGroup;

    @Column(length = 255)
    private String name;

    @Column(length = 7)
    private String colorCode;

    // Getters and Setters
}

