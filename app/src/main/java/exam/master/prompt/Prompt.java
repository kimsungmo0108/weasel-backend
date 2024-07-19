package exam.master.prompt;

import exam.master.history.History;
import exam.master.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Prompt")
public class Prompt {
  @Id
  @GeneratedValue
  @Column(name = "prompt_id", nullable = false, unique = true)
  private UUID promptId;

  @OneToMany
  @JoinColumn(name = "history_id", nullable = false)
  private History history;

  @Column(name = "photo", nullable = false)
  private String photo;

  @Column(name = "prompt", nullable = false)
  private String prompt;

  @Column(name = "answer", nullable = false)
  private String answer;

}
