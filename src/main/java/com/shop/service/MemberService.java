package com.shop.service;

import com.shop.Repository.MemberRepository;
import com.shop.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService  {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validationDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validationDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember.isPresent()) throw new IllegalStateException("이미 가입된 회원입니다.");
    }


}
