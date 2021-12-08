package com.shop.security;

import com.shop.Repository.MemberRepository;
import com.shop.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with email : " + email));
        return UserPrincipal.create(member);
    }

    public UserDetails loadUserById(Long id) throws EntityNotFoundException {
        Member member = memberRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return UserPrincipal.create(member);
    }
}
