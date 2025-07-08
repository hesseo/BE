package com.phraiz.back.common.security.user;

import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // Spring Security 가 로그인 시 호출
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member=memberRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        return new CustomUserDetails(member); // UserDetails 객체로 변환하여 반환
    }
}
