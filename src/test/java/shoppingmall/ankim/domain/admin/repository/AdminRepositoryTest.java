package shoppingmall.ankim.domain.admin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.global.config.QuerydslConfig;
import shoppingmall.ankim.global.config.RedisConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class AdminRepositoryTest {

    @MockBean
    S3Service s3Service;

    @Autowired
    private AdminRepository adminRepository;

    @MockBean
    private RedisHandler redisHandler;

    @MockBean
    private RedisConfig redisConfig;

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