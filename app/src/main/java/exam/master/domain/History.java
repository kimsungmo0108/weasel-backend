package exam.master.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@Table(name = "History")
public class History {
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "history_id", nullable = false, updatable = false)
  private UUID historyId;

  // fetch 란 history 테이블 정보를 select 했을 때 연관 테이블를 조인한다(기본 값일 때)
  // LAZY로 했을 때 필요없는 테이블 정보를 빼고 하나의 테이블 정보만 select 하는 옵션
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id") // 외래 키 설정
  private Member member;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  // cascade 란 
  // em.persist("promptA") em.persist("promptB") em.persist("promptC") em.persist("history") 3줄의 코드를
  // em.persist("history")로 줄여주는 옵션
  @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
  private List<Prompt> prompts = new ArrayList<>();

  public History() {
    this.createdDate = LocalDateTime.now();
  }

}
