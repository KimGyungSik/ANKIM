package shoppingmall.ankim.domain.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
//    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 일반 회원 조회
        if (memberRepository.existsByLoginId(username)) {
//            return new CustomUserDetails(memberRepository.findByLoginId(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다.")));
        }

        // 관리자 조회
//        if (adminRepository.existsById(username)) {
//            return new CustomUserDetails(adminRepository.findById(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("관리자 정보를 찾을 수 없습니다.")));
//        }

        // 사용자 정보가 없으면 예외 처리
        throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
    }
}