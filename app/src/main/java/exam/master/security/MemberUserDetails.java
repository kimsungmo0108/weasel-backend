package exam.master.security;

import exam.master.domain.Member;
import io.micrometer.common.lang.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Data
@RequiredArgsConstructor
public class MemberUserDetails implements UserDetails, OAuth2User {

  private final Member member;
  private final List<GrantedAuthority> authorities;
  private final Map<String, Object> oauthUserAttributes;

  // 소셜 로그인
  public static MemberUserDetails create(Member member, Map<String, Object> oauthUserAttributes) {
    return new MemberUserDetails(member, List.of(() -> "ROLE_USER"), oauthUserAttributes);
  }

  // 일반 로그인
  public static MemberUserDetails create(Member member) {
    return new MemberUserDetails(member, List.of(() -> "ROLE_USER"), new HashMap<>());
  }
  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return String.valueOf(member.getEmail());
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(oauthUserAttributes);
  }

  @Override
  @Nullable
  @SuppressWarnings("unchecked")
  public <A> A getAttribute(String name) {
    return (A) oauthUserAttributes.get(name);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.unmodifiableList(authorities);
  }

  @Override
  public String getName() {
    return String.valueOf(member.getEmail());
  }
}
