package shoppingmall.ankim.domain.admin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("AdminRepository를 통해 Admin이 정상적으로 저장된다.")
    void testAdminSave() {
        // given
        String unvalidatedId = "validid";
        Admin admin = Admin.builder()
                .loginId(unvalidatedId)
                .pwd("Password123!")
                .name("홍길동")
                .email("test@example.com")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .status(AdminStatus.ACTIVE)
                .joinDate(LocalDate.now())
                .modDate(LocalDate.now())
                .build();

        // when
        Admin savedAdmin = adminRepository.save(admin);

        // then
        assertNotNull(savedAdmin);
        assertEquals("validid", savedAdmin.getLoginId());
    }
}