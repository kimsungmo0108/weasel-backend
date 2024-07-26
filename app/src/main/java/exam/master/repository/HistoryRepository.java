package exam.master.repository;

import exam.master.domain.History;
import exam.master.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {

  private final EntityManager em;

  public void save(History history){
    em.persist(history);
  }

  public List<History> findAll(UUID memberId){
    return em.createQuery("select h from History h where h.member.id = :memberId", History.class)
        .setParameter("memberId", memberId)
        .getResultList();
  }
}
