package shoppingmall.ankim.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.hibernate.annotations.Array;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberJwtFactory;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartApiController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
@Import({JwtTokenProvider.class, MemberRepository.class})
class CartApiControllerTest {

    @Autowired
    EntityManager em;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // FIXME dfgdfg

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // Spring Security의 인증된 사용자 모의
    @DisplayName("장바구니에 상품 담기 성공")
    void addToCart_Success() throws Exception {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String accessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        AddToCartRequest request = new AddToCartRequest();
        request.setProductNo(1L);
        request.setOptionValueNoList(List.of(1L, 3L));
        request.setQty(2);

        Cookie cookie = new Cookie("access", accessToken);

        // when & then
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie) // HTTP 쿠키 설정
                        .content(objectMapper.writeValueAsString(request))) // 요청 본문 설정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니에 상품이 담겼습니다.")); // 응답 메시지 확인        }
    }
}