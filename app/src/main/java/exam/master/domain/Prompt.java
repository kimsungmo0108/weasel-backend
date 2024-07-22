package exam.master.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Prompt")
public class Prompt {

  // UUID(난수)로 하게 되면 정렬을 못 할 것 같음
  // auto increment로 설정
  @Id
  @GeneratedValue
  @Column(name = "prompt_id", updatable = false)
  private Long promptId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "history_id")
  private History history;

  @Column(name = "photo", nullable = false)
  private String photo;

  @Column(name = "prompt", nullable = false)
  private String prompt;

  @Column(name = "answer", nullable = false)
  private String answer;

}
