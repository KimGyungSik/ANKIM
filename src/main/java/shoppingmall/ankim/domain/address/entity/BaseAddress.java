package shoppingmall.ankim.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseAddress {

    @Column(name = "zip_code", nullable = false)
    private Integer zipCode;

    @Column(name = "addr_main", length = 40, nullable = false)
    private String addressMain;

    @Column(name = "addr_dtl", length = 40, nullable = false)
    private String addressDetail;

    @Column(name = "reg_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "mod_date")
    private LocalDateTime modificationDate = LocalDateTime.now();

    @Builder
    public BaseAddress(Integer zipCode, String addressMain, String addressDetail, LocalDateTime registrationDate, LocalDateTime modificationDate) {
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
        this.registrationDate = registrationDate;
        this.modificationDate = modificationDate;
    }
}
