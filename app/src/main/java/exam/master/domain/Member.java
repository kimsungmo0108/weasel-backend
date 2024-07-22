package exam.master.domain;

import static exam.master.status.MemberStatus.USER;

import exam.master.status.MemberStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@Table(name = "Member")
public class Member {
  @Id
  @GeneratedValue(generator = "UUID")
  // @Column(name = "member_id", nullable = false, unique = true)
  // UUID로 설정하게 되면 기본이 유니크 제야조건이 들어감
  // updatable = false => 수정을 방지
  @Column(name = "member_id", nullable = false, updatable = false)
  private UUID memberId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  // 사진을 등록 하지 않으면 기본 사진으로 설정, NULL 허용
  @Column(name = "profile_photo")
  private String profilePhoto;

  @OneToMany(mappedBy = "member")
  private List<History> histories = new ArrayList<>();

  public Member() {
    // member 객체를 만들면 상태 자동 등록
    this.status = USER;
  }
}
