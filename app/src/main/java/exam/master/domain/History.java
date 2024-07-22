package exam.master.domain;

import jakarta.persistence.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @OneToMany(mappedBy = "history")
  private List<Prompt> prompts = new ArrayList<>();

  public History() {
    this.createdDate = LocalDateTime.now();
  }

  //기본값 설정
//  @PrePersist
//  protected void onCreate() {
//
//    if (this.createdDate == null) {
//      this.createdDate = Timestamp.valueOf(LocalDateTime.now()); // 현재 시간으로 기본값 설정
//    }
//
//  }

}
