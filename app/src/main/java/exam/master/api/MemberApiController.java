package exam.master.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exam.master.domain.Member;
import exam.master.dto.JoinResponse;
import exam.master.dto.LogInRequest;
import exam.master.dto.LoginResponse;
import exam.master.dto.MemberDTO;
import exam.master.service.MemberService;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Hashtable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.mapping.Join;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://weasel.kkamji.net")
@CrossOrigin(origins = "*")
@RequestMapping("/v1")
public class MemberApiController {

    private static final Log log = LogFactory.getLog(MemberApiController.class);
    // 세션 리스트 확인용 코드
    public static Hashtable sessionList = new Hashtable();
    private final MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<JoinResponse> joinMember(
        @RequestParam(value = "memberDTOstr") String memberDTOstr,
        @RequestParam(value = "file", required = false) MultipartFile file)
        throws JsonProcessingException {

        System.out.println("memberDTOstr : " + memberDTOstr);

        MemberDTO memberDTO = convertStringToMemberDTO(memberDTOstr);
        System.out.println("memberDTO : " + memberDTO);
        JoinResponse newMember = memberService.joinMember(memberDTO, file);
        return ResponseEntity.ok(newMember);
    }

    @GetMapping("/member/view/{id}")
    public ResponseEntity<MemberDTO> Memberv1(
        @PathVariable("id") UUID id) {
        MemberDTO memberDTO = memberService.getMemberById(id);
        return ResponseEntity.ok(memberDTO);
    }

    //PatchMapping는 엔티티의 일부를 바꿀 때, PutMapping는 엔티티 전부를 바꿀 때 사용
    @PatchMapping("/member/update/{id}")
    public ResponseEntity<MemberDTO> updateMemberV1(@PathVariable("id") UUID id,
        @RequestBody MemberDTO updatedMemberDTO) {
        MemberDTO memberDTO = memberService.updateMember(id, updatedMemberDTO);
        return ResponseEntity.ok(memberDTO);
    }

    //정상적으로 삭제되면 빈객체 반환
    @DeleteMapping("/member/delete/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LogInRequest logInRequest,
        HttpServletRequest request) {

        Member loginMember = memberService.login(logInRequest);
        log.debug("loginUser >>> " + logInRequest);
        if (loginMember == null) {
            log.debug("loginUser is null!!! >>> " + loginMember);
            return new LoginResponse(-1, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.", "");
        } else {
            // 로그인 성공 처리
            // 세션이 있으면 세션 반환, 없으면 신규 세션 생성
            HttpSession session = request.getSession();
            // 세션에 로그인 회원 정보 보관
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
            log.debug("session에 저장한 유저 id >>> " + loginMember.getMemberId());
            log.debug("session에 저장한 유저 email >>> " + loginMember.getEmail());
            // S3  URL
            String profilePhoto = loginMember.getProfilePhoto();

            return new LoginResponse(1, "로그인 성공", profilePhoto);
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