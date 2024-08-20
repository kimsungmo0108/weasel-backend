package exam.master.service;

import exam.master.domain.Member;
import exam.master.dto.JoinResponse;
import exam.master.dto.LogInRequest;
import exam.master.dto.MemberDTO;
import exam.master.repository.HistoryRepository;
import exam.master.repository.MemberRepository;
import exam.master.status.MemberStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final AwsS3Service awsS3Service;

  // 회원 등록
  @Transactional
  public JoinResponse joinMember(MemberDTO memberDTO, MultipartFile file) {

    Member member = new Member();
    member.setEmail(memberDTO.getEmail());

    Member joinMember = null;

    if (member.getEmail() != null) {
      //중복된 이메일로 가입되있는지 확인
      joinMember = validateDuplicateMember(member);
    }

    if (joinMember == null) {
      // joinMember가 null이면 회원가입 진행
      member.setPassword(memberDTO.getPassword());
      if (file != null) {
        member.setProfilePhoto(awsS3Service.uploadFile(file));
      } else {
        member.setProfilePhoto(null);
      }

      // 새로운 멤버를 저장
      Member savedMember = memberRepository.save(member);

      log.debug("회원가입 성공!");
      return new JoinResponse(1, convertToDTO(savedMember));

    } else {
      // 회원가입 된 회원이라면 회원 정보 리턴
      log.debug("이미 등록된 회원");
      return new JoinResponse(-1, convertToDTO(joinMember));
    }
  }

  // 회원정보 수정

  @Transactional
  public MemberDTO updateMember(UUID memberId, MemberDTO updatedMemberDTO, MultipartFile file) {
    Optional<Member> optionalMember = memberRepository.findById(memberId);

    if (!optionalMember.isPresent()) {
      throw new RuntimeException("Member not found");
    }
    Member existingMember = optionalMember.get();

    if(file != null){
      // 새로운 사진 업로드
      String photo = awsS3Service.uploadFile(file);
      existingMember.setProfilePhoto(photo);
      if(existingMember.getProfilePhoto() != null){
        // 기존에 존재하는 사진 s3에서 삭제
        awsS3Service.deleteFile(existingMember.getProfilePhoto());
      }
    }

    if (updatedMemberDTO.getEmail() != null) {
      existingMember.setEmail(updatedMemberDTO.getEmail());
    }
    if (updatedMemberDTO.getProfilePhoto() != null) {
      existingMember.setProfilePhoto(updatedMemberDTO.getProfilePhoto());
    }
    // 비밀번호 업데이트 시 추가 검증 로직이 필요할 수 있음
    if (updatedMemberDTO.getPassword() != null) {
      existingMember.setPassword(updatedMemberDTO.getPassword());
    }

    // 변경된 내용을 저장
    Member updatedMember = memberRepository.save(existingMember);
    return convertToDTO(updatedMember);

  }
  // 회원탈퇴

  @Transactional
  public void deleteMember(UUID memberId, String profilePhoto) {
    if (!memberRepository.existsById(memberId)) {
      throw new RuntimeException("존재하지 않는 회원입니다");
    }

    // s3에서 프로필 사진 삭제
    if(profilePhoto != null){
      awsS3Service.deleteFile(profilePhoto);
    }

    memberRepository.deleteById(memberId);
  }
  // 회원정보 조회


  public List<Member> findMember() {
    return memberRepository.findAll();
  }

  public MemberDTO getMemberById(UUID memberId) {
    Optional<Member> optionalMember = memberRepository.findById(memberId);
    if (!optionalMember.isPresent()) {
      throw new RuntimeException("존재하지 않는 회원입니다");
    }
    Member member = optionalMember.get();
    return convertToDTO(member);
  }


  /**
   * 로그인 기능 return member - 로그인 성공 return null - 로그인 실패
   */
  public Member login(String email) {

    // 해당 이메일로 회원인지 확인
    Member existMember = memberRepository.findByEmail(email);

    // Email과 일치하는 Member가 없으면 null return
    if (existMember.getEmail().isEmpty()) {
      return null;
    }

    return existMember;
  }

  private LogInRequest convertToLoginRequest(Member member) {
    LogInRequest logInRequest = new LogInRequest();

    logInRequest.setEmail(member.getEmail());
    logInRequest.setPassword(member.getPassword());

    return logInRequest;
  }


  // Member api Response에 사용될 DTO로 변환
  public MemberDTO convertToDTO(Member member) {
    MemberDTO memberDTO = new MemberDTO();

    memberDTO.setMemberId(member.getMemberId());
    memberDTO.setStatus(member.getStatus());
    memberDTO.setEmail(member.getEmail());
    memberDTO.setProfilePhoto(member.getProfilePhoto());

    return memberDTO;
  }

  /* 아래는 테스트로 썻던 오브젝트 */
  // 등록
  @Transactional
  public UUID join(Member member) {

    // 중복 회원 검증
    validateDuplicateMember(member);
    memberRepository.save(member);
    return member.getMemberId();
  }

  // 수정
  @Transactional
  public UUID update(Member member, MultipartFile file) {
    memberRepository.update(member);
    return member.getMemberId();
  }

  // 탈퇴
  @Transactional
  public UUID delete(Member member) {
    member.setStatus(MemberStatus.DELETE);
    memberRepository.update(member);
    return member.getMemberId();
  }

  // 검증
  public Member validateDuplicateMember(Member member) {
    return memberRepository.findByEmail(member.getEmail());
  }

}