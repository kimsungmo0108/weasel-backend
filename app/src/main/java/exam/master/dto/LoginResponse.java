package exam.master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private int resultCode; // 성공 : 1, 실패 : -1
    private String msg;
    private String profilePhoto;
}
