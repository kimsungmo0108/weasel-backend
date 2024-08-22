package exam.master.security;

import exam.master.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
    this.customOAuth2UserService = customOAuth2UserService;
    log.debug("SecurityConfig 객체 생성됨!");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()  // REST API의 경우 CSRF 비활성화
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/v1/login", "/v1/member/join", "/oauth2/authorization/google")
            .permitAll()  // 로그인, 회원가입은 인증 없이 접근 가능
            .anyRequest().authenticated()  // 그 외의 모든 요청은 인증 필요
        );

    http.oauth2Login(oauth2 -> oauth2
//            .userInfoEndpoint()
//            .userService(customOAuth2UserService)  // CustomOAuth2UserService 등록
//            .and()
            .successHandler((request, response, authentication) -> {
              log.debug("소셜 로그인 성공");
              response.sendRedirect("https://weasel.kkamji.net/home");
            })
            .failureHandler((request, response, authenticationException) -> {
              log.debug("OAuth2 로그인 실패: {}", authenticationException.getMessage());
              response.sendRedirect("/login?error");
            })
    );
    http.formLogin(form -> form
            .loginProcessingUrl("/v1/login")  // 로그인 처리 API 경로
            .usernameParameter("email") // 로그인 수행할 때 사용할 사용자 아이디 또는 이메일(principal) 파라미터 명
            .passwordParameter("password") // 로그인 수행할 때 사용할 사용자 암호(credential) 파라미터 명
            .successForwardUrl("/v1/loginSuccess")
            .permitAll()
        )
        .logout(Customizer.withDefaults());
    return http.build();
  }


  @Bean
  public UserDetailsService userDetailsService(MemberService memberService) {
    // 우리가 만든 UserDetailsService 객체를 사용한다.
    // => DB에서 사용자 정보를 가져올 것이다.
    return new MyUserDetailsService(memberService);
  }

//  @Bean
//  public DefaultOAuth2UserService defaultOAuth2UserService(MemberRepository memberRepository) {
//    // 우리가 만든 UserDetailsService 객체를 사용한다.
//    // => DB에서 사용자 정보를 가져올 것이다.
//    return new CustomOAuth2UserService(memberRepository);
//  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
