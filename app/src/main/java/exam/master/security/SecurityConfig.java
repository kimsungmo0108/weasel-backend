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

  public SecurityConfig() {
    log.debug("SecurityConfig 객체 생성됨!");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf().disable()  // REST API의 경우 CSRF 비활성화
//        .cors().and()  // CORS 설정을 활성화
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/v1/login", "/v1/member/join").permitAll()  // 로그인, 회원가입은 인증 없이 접근 가능
            .anyRequest().authenticated()  // 그 외의 모든 요청은 인증 필요
          )
        .formLogin(form -> form
            .loginProcessingUrl("/v1/login")  // 로그인 처리 API 경로
            .usernameParameter("email") // 로그인 수행할 때 사용할 사용자 아이디 또는 이메일(principal) 파라미터 명
            .passwordParameter("password") // 로그인 수행할 때 사용할 사용자 암호(credential) 파라미터 명
            .successForwardUrl("/v1/loginSuccess")
            .permitAll()
        )
        .logout(Customizer.withDefaults()).build();
  }


  // 사용자 정보를 리턴해주는 객체
  @Bean
  public UserDetailsService userDetailsService(MemberService memberService) {
    // 우리가 만든 UserDetailsService 객체를 사용한다.
    // => DB에서 사용자 정보를 가져올 것이다.
    return new MyUserDetailsService(memberService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}