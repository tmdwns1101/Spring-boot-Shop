package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String name, String address, String password) {
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email(email)
                .name(name)
                .address(address)
                .password(password)
                .build();
        return Member.memberFactory(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원 가입 테스트")
    public void saveMemberTest() {
        Member member = createMember("test@test.com","Tom","Seoul","1234");
        Member savedMember = memberService.saveMember(member);

        assertEquals(1, savedMember.getId());
        assertEquals("Tom", savedMember.getName());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest() {
        Member member1 = createMember("test@test.com","Tom","Seoul","1234");
        Member member2 = createMember("test@test.com","Tom","Seoul","1234");
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, () -> memberService.saveMember(member2));
        assertEquals("이미 가입된 회원입니다.",e.getMessage());
    }


}