package com.shop.security.oauth2;

import com.shop.Repository.MemberRepository;
import com.shop.constant.AuthProvider;
import com.shop.entity.Member;
import com.shop.exception.OAuth2AuthenticationProcessingException;
import com.shop.security.UserPrincipal;
import com.shop.security.oauth2.user.OAuth2UserInfo;
import com.shop.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;



import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuthUser(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        }   catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    private OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email is not found from OAuth2 Provider!");
        }

        Optional<Member> savedMember = memberRepository.findByEmail(oAuth2UserInfo.getEmail());
        Member member;
        if (savedMember.isPresent()) {
            member = savedMember.get();
            if(!member.getAuthProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with " +
                                member.getAuthProvider() + " account. Please use your " + member.getAuthProvider() +
                                " account to login."
                );
            }
            member = updateExistingUser(member, oAuth2UserInfo);
        } else {
            member = registerNewMember(userRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(member, oAuth2User.getAttributes());
    }

    private Member registerNewMember(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member
                .memberFactory(oAuth2UserInfo, AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        return memberRepository.save(member);
    }

    private Member updateExistingUser(Member existingMember, OAuth2UserInfo oAuth2UserInfo) {
        existingMember.setName(oAuth2UserInfo.getName());
        return memberRepository.save(existingMember);
    }

}
