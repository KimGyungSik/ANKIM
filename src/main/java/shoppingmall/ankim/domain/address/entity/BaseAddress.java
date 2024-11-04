package shoppingmall.ankim.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter @Setter
public abstract class BaseAddress {

    @Column(name = "zip_code", nullable = false)
    private Integer zipCode;

    @Column(name = "addr_main", length = 40, nullable = false)
    private String addressMain;

    @Column(name = "addr_dtl", length = 40, nullable = false)
    private String addressDetail;

    @Column(name = "phone_num", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "phone_emgcy", length = 20)
    private String emergencyPhoneNumber;

    @Column(name = "reg_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "mod_date")
    private LocalDateTime modificationDate = LocalDateTime.now();
}
