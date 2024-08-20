package exam.master.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exam.master.domain.Member;
import exam.master.dto.JoinResponse;
import exam.master.dto.LoginResponse;
import exam.master.dto.MemberDTO;
import exam.master.security.MemberUserDetails;
import exam.master.service.MemberService;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Hashtable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://weasel.kkamji.net",
    "http://localhost:5173"}, allowCredentials = "true")
//@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/v1")
public class MemberApiController {

  // 세션 리스트 확인용 코드
  public static Hashtable sessionList = new Hashtable();
  private final MemberService memberService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/member/join")
  public ResponseEntity<JoinResponse> joinMember(
      @RequestParam(value = "memberDTOstr") String memberDTOstr,
      @RequestParam(value = "file", required = false) MultipartFile file)
      throws JsonProcessingException {

    System.out.println("memberDTOstr : " + memberDTOstr);

    MemberDTO memberDTO = convertStringToMemberDTO(memberDTOstr);
    // 비밀번호 암호화
    memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

    System.out.println("memberDTO : " + memberDTO);
    JoinResponse newMember = memberService.joinMember(memberDTO, file);

    return ResponseEntity.ok(newMember);
  }

  @GetMapping("/member/view")
  public ResponseEntity<MemberDTO> memberV1(
      HttpSession session) {
    Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    if (member == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    MemberDTO memberDTO = memberService.getMemberById(member.getMemberId());
    return ResponseEntity.ok(memberDTO);
  }

  //PatchMapping는 엔티티의 일부를 바꿀 때, PutMapping는 엔티티 전부를 바꿀 때 사용
  @PatchMapping("/member/update")
  public ResponseEntity<MemberDTO> updateMemberV1(@RequestParam("updatedMemberDTOstr") String updatedMemberDTOstr,
      @RequestParam(value = "file", required = false) MultipartFile file,
      HttpSession session) throws JsonProcessingException {

    log.debug("멤버 업데이트 >>> " + updatedMemberDTOstr);

    Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

    MemberDTO updatedMemberDTO = convertStringToMemberDTO(updatedMemberDTOstr);

    // 비밀번호 암호화
    if(updatedMemberDTO.getPassword() == null){
      updatedMemberDTO.setPassword(member.getPassword());
    }else{
      updatedMemberDTO.setPassword(passwordEncoder.encode(updatedMemberDTO.getPassword()));
    }
    MemberDTO memberDTO = memberService.updateMember(member.getMemberId(), updatedMemberDTO, file);

    Member newMember = memberService.login(memberDTO.getEmail());

    // 세션에 업데이트 된 로그인 정보로 새로 저장
    session.removeAttribute(SessionConst.LOGIN_MEMBER);
    session.setAttribute(SessionConst.LOGIN_MEMBER, newMember);

    return ResponseEntity.ok(memberDTO);
  }

  //정상적으로 삭제되면 빈객체 반환
  @DeleteMapping("/member/delete")
  public ResponseEntity<Void> deleteMember(HttpSession session) {
    Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    memberService.deleteMember(member.getMemberId(), member.getProfilePhoto());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/loginSuccess")
  public ResponseEntity<LoginResponse> login(
      @AuthenticationPrincipal MemberUserDetails principal,
      HttpSession session) {

    Member logInRequest = principal.getMember();
    log.debug("login 성공");
    log.debug("login 정보 >>> " + logInRequest.getEmail());
    log.debug("login 정보 >>> " + logInRequest.getPassword());

    try {

      // 이메일로 로그인 회원 정보 가져오기
      Member loginMember = memberService.login(logInRequest.getEmail());

      String loginMemberPassword = logInRequest.getPassword();
      String memberPassword = loginMember.getPassword();

      // 비밀번호 매칭 체크
      if (loginMemberPassword.equals(memberPassword)) {
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

  @GetMapping("/logout")
  public LoginResponse logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false); // 로그아웃인데 세션 없을 때 생성 안 함
    if (session != null) {
      session.invalidate(); // 세션 정보가 있으면 정보 삭제
    }
    return new LoginResponse(1, "로그아웃 성공", "");
  }

  private MemberDTO convertStringToMemberDTO(String memberDTOstr) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(memberDTOstr, MemberDTO.class);

  }


}