package shoppingmall.ankim.domain.admin.service.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AdminRegisterServiceRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 AdminRegisterServiceRequest는 엔티티로 변환이 가능하다.")
    void validRequestToEntity() {
        // given
        AdminRegisterServiceRequest request = AdminRegisterServiceRequest.builder()
                .loginId("admin123")
                .pwd("SecurePass123!")
                .name("홍길동")
                .email("admin@example.com")
                .phoneNum("010-1234-5678")
                .officeNum("02-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .joinDate("2023-01-01")
                .status(AdminStatus.ACTIVE)
                .zipCode(12345)
                .addressMain("서울특별시 강남구")
                .addressDetail("101호")
                .build();

        // when
        BaseAddress baseAddress = request.toBaseAddress();
        Admin admin = request.toAdminEntity(request.getPwd());

        // then
        assertThat(admin).isNotNull();
        assertThat(admin.getLoginId()).isEqualTo("admin123");
        assertThat(admin.getAdminAddress().getBaseAddress().getAddressMain()).isEqualTo("서울특별시 강남구");
    }

    @Test
    @DisplayName("BaseAddress 변환이 올바르게 이루어진다.")
    void toBaseAddressTest() {
        // given
        AdminRegisterServiceRequest request = AdminRegisterServiceRequest.builder()
                .zipCode(12345)
                .addressMain("서울특별시 강남구")
                .addressDetail("101호")
                .build();

        // when
        BaseAddress baseAddress = request.toBaseAddress();

        // then
        assertThat(baseAddress).isNotNull();
        assertThat(baseAddress.getZipCode()).isEqualTo(12345);
        assertThat(baseAddress.getAddressMain()).isEqualTo("서울특별시 강남구");
        assertThat(baseAddress.getAddressDetail()).isEqualTo("101호");
    }
}