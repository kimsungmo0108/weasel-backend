package exam.master.service;

import exam.master.domain.History;
import exam.master.domain.Member;
import exam.master.domain.Prompt;
import exam.master.repository.HistoryRepository;
import exam.master.repository.PromptRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptService {

  private final PromptRepository promptRepository;
  private final HistoryRepository historyRepository;

  public Prompt addPrompt(Prompt prompt, UUID historyId, Member loginUser/*, MultipartFile file*/) {
    
    // 컨트롤러에서 프롬프트에 히스토리id를 검사
    if (historyId == null) {
      History history = new History();
      history.setTitle(prompt.getPrompt());
      history.setMember(loginUser);
      historyRepository.save(history);
      prompt.setHistory(history);
    }

//    file 스토리지에 업로드 후
//    String fileName = "";
//    prompt.setPhoto(fileName);

//    베드락에 프롬프트와 사진을 보내고 응답을 받는다
//    String answer = "";
//    prompt.setAnswer(answer);

    promptRepository.save(prompt);
    return prompt;

  }

  public List<Prompt> findByHistoryId(UUID historyId){
    return promptRepository.findByHistoryId(historyId);
  }
}
