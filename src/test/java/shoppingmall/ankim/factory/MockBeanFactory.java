package shoppingmall.ankim.factory;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.service.CustomUserDetailsService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;

@TestConfiguration
public class MockBeanFactory {

    @Bean
    @Primary
    public MemberService memberService() {
        return Mockito.mock(MemberService.class);
    }

    @Bean
    @Primary
    public TermsHistoryService termsHistoryService() {
        return Mockito.mock(TermsHistoryService.class);
    }

    @Bean
    @Primary
    public TermsQueryService termsQueryService() {
        return Mockito.mock(TermsQueryService.class);
    }

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }
}