package exam.master.repository;

import exam.master.domain.Member;
import exam.master.security.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository // 스프링 빈으로 등록
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

  // 엔티티 메니저 팩토리 주입
//  @PersistenceUnit
//  private EntityManagerFactory rmf;

  @PersistenceContext // 엔티티 매니저 주입
  private final EntityManager em;

  @Transactional
  public Member save(Member member){
    if (member.getMemberId() == null) {
      em.persist(member);
    } else {
      em.merge(member);
    }
    return member;
  }

  public void update(Member member){
    em.merge(member);
  }

  public Optional<Member> findById(UUID memberId){
    Member member = em.find(Member.class, memberId);
    return Optional.ofNullable(member);
  }

  public void deleteById(UUID memberId) {
    Member member = em.find(Member.class, memberId);
    if (member != null) {
      em.remove(member);
    }
  }

  public boolean existsById(UUID memberId) {
    Member member = em.find(Member.class, memberId);
    return member != null;
  }

  public List<Member> findAll(){
    return em.createQuery("select m from Member m", Member.class). getResultList();
  }

  // SQL 쿼리문에 파라미터를 설정하는 예제
  // SQL 쿼리 = JPQL
  // 엔티티 객체를 쿼리 한다고 보면 편하다.
  public Member findByEmail(String email){
    try{
      return em.createQuery("select m from Member m where m.email = :email", Member.class).setParameter("email", email).getSingleResult();
    } catch (NoResultException e){
      return null;
    }

  }

  public Member findByEmailAndPassword(String email, String password){
    try {
      return em.createQuery("select m from Member m where m.email = :email and m.password = :password", Member.class)
          .setParameter("email", email)
          .setParameter("password", password)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public Optional<Member> findByEmailAndProvider(String email, String provider){

    List<Member> members = em.createQuery("select m from Member m where m.email = :email and m.provider = :provider", Member.class)
        .setParameter("email", email)
        .setParameter("provider", provider)
        .getResultList();

    if (members.isEmpty()) {
      return Optional.empty();
    }

    return Optional.ofNullable(members.getFirst());

  }
}

