package com.shop.entity;

import com.shop.constant.AuthProvider;
import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import com.shop.security.oauth2.user.OAuth2UserInfo;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider",nullable = false)
    private AuthProvider authProvider;

    public static Member memberFactory(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .address(memberFormDto.getAddress())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .role(Role.USER)
                .authProvider(AuthProvider.local)
                .build();
    }

    public static Member memberFactory(OAuth2UserInfo oAuth2UserInfo, AuthProvider authProvider) {
        return Member.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .role(Role.USER)
                .authProvider(authProvider)
                .build();
    }
}
