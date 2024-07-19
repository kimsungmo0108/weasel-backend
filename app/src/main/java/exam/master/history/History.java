package exam.master.history;

import exam.master.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@Table(name = "History")
public class History {
  @Id
  @GeneratedValue
  @Column(name = "history_id", nullable = false, unique = true)
  private UUID historyId;

  @OneToMany
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "created_date", nullable = false)
  private Timestamp createdDate;

  //기본값 설정
  @PrePersist
  protected void onCreate() {

    if (this.createdDate == null) {
      this.createdDate = Timestamp.valueOf(LocalDateTime.now()); // 현재 시간으로 기본값 설정
    }

  }

}
