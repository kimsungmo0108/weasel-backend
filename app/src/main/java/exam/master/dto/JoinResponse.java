package exam.master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinResponse {
  private int resultCode; // 성공 : 1, 실패 : -1
  private MemberDTO memberDTO;
}
