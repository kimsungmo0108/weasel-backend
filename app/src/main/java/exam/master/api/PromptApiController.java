package exam.master.api;

import exam.master.domain.Member;
import exam.master.dto.PromptDTO;
import exam.master.service.MemberService;
import exam.master.service.PromptService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompt")
public class PromptApiController {

  private static final Log log = LogFactory.getLog(PromptApiController.class);
  private final PromptService promptService;
  private final MemberService memberService;

  @PostMapping("/add")
  public ResponseEntity<PromptDTO> add(@RequestBody PromptDTO promptDTO,
      @RequestParam(required = false) UUID historyId,
      @RequestParam(required = false) MultipartFile file,
      HttpSession session) {

//    Member loginUser = (Member) session.getAttribute("loginMember");
    Member loginUser = memberService.findByEmailAndPassword("kim@test", "1111");

    PromptDTO newPromptDTO = promptService.addPrompt(promptDTO, historyId, loginUser.getMemberId(),
        file);

    return ResponseEntity.ok(newPromptDTO);
  }

  @GetMapping("/list/{historyId}")
  public ResponseEntity<List<PromptDTO>> list(
      @PathVariable(value = "historyId") UUID historyId,
      HttpSession session) {
    List<PromptDTO> list = promptService.findByHistoryId(historyId);
    return ResponseEntity.ok(list);
  }

}
