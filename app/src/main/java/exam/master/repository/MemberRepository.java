package exam.master.repository;

import exam.master.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // 스프링 빈으로 등록
@RequiredArgsConstructor
public class MemberRepository {

  // 엔티티 메니저 팩토리 주입
//  @PersistenceUnit
//  private EntityManagerFactory rmf;
  
//  @PersistenceContext // 엔티티 매니저 주입
  private final EntityManager em;

  public void save(Member member){
    em.persist(member);
  }

  public void update(Member member){
    em.merge(member);
  }

  public Member findOne(UUID memberId){
    return em.find(Member.class, memberId);
  }

  public List<Member> findAll(){
    return em.createQuery("select m from Member m", Member.class). getResultList();
  }

  // SQL 쿼리문에 파라미터를 설정하는 예제
  // SQL 쿼리 = JPQL
  // 엔티티 객체를 쿼리 한다고 보면 편하다.
  public List<Member> findByEmail(String email){
    return em.createQuery("select m from Member m where m.email = :email", Member.class).setParameter("email", email).getResultList();
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
}
