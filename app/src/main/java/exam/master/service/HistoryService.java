package exam.master.service;

import exam.master.domain.History;
import exam.master.domain.Member;
import exam.master.domain.Prompt;
import exam.master.dto.HistoryDTO;
import exam.master.dto.MemberDTO;
import exam.master.repository.HistoryRepository;
import exam.master.repository.PromptRepository;
import exam.master.status.MemberStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {
  private final PromptRepository promptRepository;
  private final HistoryRepository historyRepository;
  private final AwsS3Service awsS3Service;
  private final PromptService promptService;

  @Transactional
  public int deleteHistoryByHistoryIdAndMemberId(UUID historyId, UUID memberId){

    // 유효성 검사
    History history = historyRepository.findOne(historyId);

    int count = 0;
    log.debug("count 시작 >>> " + count);
    log.debug("delete memberId >>> " + memberId);
    log.debug("delete history.getMember().getMemberId() >>> " + history.getMember().getMemberId());

    if(memberId.equals(history.getMember().getMemberId())){
      log.debug("delete 시작!");

      // s3에 업로드 되어있는 이미지 삭제
      List<Prompt> deletePromptList = promptRepository.findByHistoryId(historyId);
      for(Prompt deletePrompt : deletePromptList){
        // 지금은 photo is null!이 아니라면 s3에 삭제 요청
        if(!deletePrompt.getPhoto().equals("photo is null!")) {
          awsS3Service.deleteFile(deletePrompt.getPhoto());
          log.debug("photo delete!");
        }
      }

      // 히스토리에 해당하는 모든 데이터 삭제
      count += promptRepository.deletePromptsByHistoryId(historyId);
      count += historyRepository.deleteHistoryByHistoryId(historyId);
      log.debug("삭제 완료! >>> "+ count);
    }

    return count;
  }

  public List<HistoryDTO> findByMemberDTO (MemberDTO loginMemberDTO){

    List<History> list = historyRepository.findAllByMemberId(loginMemberDTO.getMemberId());

    // DTO로 변환
    List<HistoryDTO> newList = new ArrayList<>();
    for(History history : list){
      newList.add(promptService.convertToHistoryDTO(history, loginMemberDTO));
    }

    return newList;
  }

}
