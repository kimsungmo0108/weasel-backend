package exam.master.api;

import exam.master.domain.Member;
import exam.master.dto.LoginResponse;
import exam.master.service.MemberService;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@CrossOrigin(origins = {"https://weasel.kkamji.net",
    "http://localhost:5173"}, allowCredentials = "true")
public class socialLoginApiController {

  private final MemberService memberService;

  @GetMapping("/social/loginSuccess")
  public ResponseEntity<LoginResponse> loginSuccess(HttpSession session) {

    try {

      // 이메일로 로그인 회원 정보 가져오기
      Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

      // 비밀번호 매칭 체크
      if (loginMember != null) {
        // 로그인 성공 처리 (세션 관리)
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return ResponseEntity.ok(new LoginResponse(1, "로그인 성공", loginMember.getProfilePhoto()));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new LoginResponse(-1, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.", ""));
      }
    } catch (AuthenticationException e) {
      log.debug("로그인 실패: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponse(-1, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.", ""));
    }

  }

}
