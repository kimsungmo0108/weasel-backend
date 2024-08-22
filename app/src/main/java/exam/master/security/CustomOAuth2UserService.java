package exam.master.security;

import exam.master.domain.Member;
import exam.master.repository.MemberRepository;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {

    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
    log.info("oAuth2UserRequest : {}", oAuth2UserRequest);
    log.info("getAttributes : {}", oAuth2User.getAttributes());

    Map<String, Object> attributes = oAuth2User.getAttributes();
    String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
    log.debug("소셜 로그인 ing...");

    String email = (String) attributes.get("email");
    validateAttributes(email);

    // 이메일 로그 출력
    log.debug("이메일: {}", email);

    Member member = registerIfNewUser(email, provider);

    // 회원 정보 저장 후 로그 출력
    log.debug("회원 저장 완료: {}", member);

    MemberUserDetails userDetails = MemberUserDetails.create(member, attributes);

    // 세션에 사용자 정보 저장
    HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
        .getRequest().getSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER, userDetails.getMember());

    return userDetails;
  }

  private void validateAttributes(String email) {
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("서드파티의 응답에 email이 존재하지 않습니다!!!");
    }
  }

  private Member registerIfNewUser(String email, String provider) {
    Optional<Member> optionalUser = memberRepository.findByEmailAndProvider(email, provider);
    log.debug("DB에서 사용자 검색 중...");

    if (optionalUser.isPresent()) {
      log.debug("기존 사용자 발견: {}", optionalUser.get());
      return optionalUser.get();
    }

    Member member = new Member();
    member.setEmail(email);
    member.setPassword("1111");
    member.setProvider(provider);

    // 회원 정보 저장 시 로그 출력
    log.debug("신규 사용자 저장: {}", member);
    return memberRepository.save(member);
  }
}
