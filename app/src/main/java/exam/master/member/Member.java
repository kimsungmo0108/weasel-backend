package exam.master.member;

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
@Table(name = "Member")
public class Member {
  @Id
  @GeneratedValue
  @Column(name = "member_id", nullable = false, unique = true)
  private UUID memberId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "state", nullable = false)
  private int state;

  @Column(name = "profile_photo", nullable = false)
  private String profilePhoto;



}
