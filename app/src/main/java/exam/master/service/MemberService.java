package exam.master.service;

import exam.master.domain.Member;
import exam.master.dto.MemberDTO;
import exam.master.repository.HistoryRepository;
import exam.master.repository.MemberRepository;
import exam.master.status.MemberStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private static final Log log = LogFactory.getLog(MemberService.class);

  private final MemberRepository memberRepository;
  private final HistoryRepository historyRepository;

  // 등록
  @Transactional
  public UUID join(Member member/*, MultipartFile file*/){

    // 중복 회원 검증
    validateDuplicateMember(member);

    // file 스토리지에 업로드 후
//    String fileName = ;
//    member.setProfilePhoto(fileName);

    memberRepository.save(member);
    return member.getMemberId();
  }

  // 검증
  public void validateDuplicateMember(Member member){
    List<Member> findMembers = memberRepository.findByEmail(member.getEmail());
    if(!findMembers.isEmpty()){
      throw new IllegalStateException("이미 존재하는 회원입니다.");
    }
  }

  // 수정
  @Transactional
  public UUID update(Member member){
    memberRepository.update(member);
    return member.getMemberId();
  }

  // 탈퇴
  @Transactional
  public UUID delete(Member member){
    member.setStatus(MemberStatus.DELETE);
    memberRepository.update(member);
    return member.getMemberId();
  }

  // 조회
  public List<Member> findMember(){
    return memberRepository.findAll();
  }

  public Member findOne(UUID memberId){
    return memberRepository.findOne(memberId);
  }

  // 로그인
  public Member findByEmailAndPassword (String email, String password){
    Member loginUser = memberRepository.findByEmailAndPassword(email, password);
    
    // 로그인 하면서 프롬프트 창으로 리다이렉트 하기 때문에 히스토리 리스트를 가져온다
    loginUser.setHistories(historyRepository.findAll(loginUser.getMemberId()));
    return loginUser;
  }

  public MemberDTO convertToDTO(Member member){
    MemberDTO memberDTO = new MemberDTO();

    memberDTO.setMemberId(member.getMemberId());
    memberDTO.setStatus(member.getStatus());
    memberDTO.setEmail(member.getEmail());
    memberDTO.setProfilePhoto(member.getProfilePhoto());

    return memberDTO;
  }
}
