package exam.master.repository;

import exam.master.domain.History;
import exam.master.domain.Prompt;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PromptRepository {

  private final EntityManager em;

  public void save(Prompt prompt){
    em.persist(prompt);
  }

  public List<Prompt> findByHistoryId(UUID historyId) {
    return em.createQuery("select p from Prompt p where p.history.id = :historyId", Prompt.class)
        .setParameter("historyId", historyId)
        .getResultList();
  }


}
