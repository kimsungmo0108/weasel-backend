package exam.master.service;

import exam.master.domain.Member;
import exam.master.repository.MemberRepository;
import exam.master.status.MemberStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private static final Log log = LogFactory.getLog(MemberService.class);

  private final MemberRepository memberRepository;

  // 등록
  @Transactional
  public UUID join(Member member){

    // 중복 회원 검증
    validateDuplicateMember(member);
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
    return memberRepository.findByEmailAndPassword(email, password);
  }
}
