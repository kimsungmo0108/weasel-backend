package exam.master.service;

import exam.master.domain.History;
import exam.master.domain.Member;
import exam.master.repository.HistoryRepository;
import exam.master.repository.PromptRepository;
import exam.master.status.MemberStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {
  private final PromptRepository promptRepository;
  private final HistoryRepository historyRepository;

  @Transactional
  public int deleteHistoryByHistoryIdAndMemberId(UUID historyId, UUID memberId){

    // 유효성 검사
    History history = historyRepository.findOne(historyId);

    int count = 0;

    if(memberId == history.getMember().getMemberId()){
      // 히스토리에 해당하는 모든 데이터 삭제
      count += promptRepository.deletePromptsByHistoryId(historyId);
      count += historyRepository.deleteHistoryByHistoryId(historyId);
    }

    return count;
  }

}
