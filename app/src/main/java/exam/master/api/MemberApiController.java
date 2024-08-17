package exam.master.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exam.master.domain.Member;
import exam.master.dto.LogInRequest;
import exam.master.dto.LoginResponse;
import exam.master.dto.MemberDTO;
import exam.master.service.MemberService;

import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse;
import java.util.Hashtable;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://weasel.kkamji.net")
@CrossOrigin(origins = "*")
@RequestMapping("/v1")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<MemberDTO> joinMember(@RequestParam(value="memberDTOstr") String memberDTOstr,
                                                @RequestParam(value = "file", required = false) MultipartFile file)
    throws JsonProcessingException {

        System.out.println("memberDTOstr : "+ memberDTOstr);

        MemberDTO memberDTO = convertStringToMemberDTO(memberDTOstr);
        System.out.println("memberDTO : "+ memberDTO);
        MemberDTO newMember = memberService.joinMember(memberDTO, file);
        return ResponseEntity.ok(newMember);
    }

    @GetMapping("/member/view")
    public ResponseEntity<MemberDTO> memberV1(
        HttpSession session ){
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MemberDTO memberDTO = memberService.getMemberById(member.getMemberId());
         return ResponseEntity.ok(memberDTO);
    }

    //PatchMapping는 엔티티의 일부를 바꿀 때, PutMapping는 엔티티 전부를 바꿀 때 사용
    @PatchMapping("/member/update")
    public ResponseEntity<MemberDTO> updateMemberV1(@RequestBody  MemberDTO updatedMemberDTO, HttpSession session) {
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        MemberDTO memberDTO = memberService.updateMember(member.getMemberId(), updatedMemberDTO);
        return ResponseEntity.ok(memberDTO);
    }

    //정상적으로 삭제되면 빈객체 반환
    @DeleteMapping("/member/delete")
    public ResponseEntity<Void> deleteMember(HttpSession session) {
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberService.deleteMember(member.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LogInRequest logInRequest, HttpSession session){

        Member loginMember = memberService.login(logInRequest);
        if(loginMember == null){
            return new LoginResponse(-1,"존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.","");
        }else {
            // 로그인 성공 처리
            // 세션에 로그인 회원 정보 보관
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
            // S3  URL
            String profilePhoto = loginMember.getProfilePhoto();

            return new LoginResponse(1, "로그인 성공", profilePhoto);
        }

    }

    @GetMapping("/logout")
    public LoginResponse logout(HttpServletRequest request){
        HttpSession session = request.getSession(false); // 로그아웃인데 세션 없을 때 생성 안 함
        if(session != null){
            session.invalidate(); // 세션 정보가 있으면 정보 삭제
        }
        return new LoginResponse(1, "로그아웃 성공","");
    }

    // 세션 리스트 확인용 코드
    public static Hashtable sessionList = new Hashtable();

    private MemberDTO convertStringToMemberDTO(String memberDTOstr) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(memberDTOstr, MemberDTO.class);

   }


}
