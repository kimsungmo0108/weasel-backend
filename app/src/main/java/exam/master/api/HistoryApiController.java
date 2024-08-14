package exam.master.api;

import exam.master.domain.Member;
import exam.master.service.HistoryService;
import exam.master.service.MemberService;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/history")
//@CrossOrigin(origins = "https://weasel.kkamji.net")
@CrossOrigin(origins = "*")
public class HistoryApiController {

  private static final Log log = LogFactory.getLog(PromptApiController.class);
  private final HistoryService historyService;

  @DeleteMapping("/delete/{historyId}")
  public ResponseEntity<Integer> delete(@PathVariable("historyId") UUID historyId, HttpSession session) {

    // 히스토리에 있는 memberId와 로그인 한 memberId 유효성 검사하기 위해서 member 객체 추출
    Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

    int count = historyService.deleteHistoryByHistoryIdAndMemberId(historyId, loginMember.getMemberId());

    return ResponseEntity.ok(count);
  }

}
