package com.phraiz.back.summary.service;

import com.phraiz.back.common.service.OpenAIService;
import com.phraiz.back.common.service.RedisService;
import com.phraiz.back.common.type.Plan;
import com.phraiz.back.common.util.GptTokenUtil;
import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.enums.LoginType;
import com.phraiz.back.member.repository.MemberRepository;
import com.phraiz.back.paraphrase.dto.request.ParaphraseRequestDTO;
import com.phraiz.back.paraphrase.dto.response.ParaphraseResponseDTO;
import com.phraiz.back.summary.dto.request.SummaryRequestDTO;
import com.phraiz.back.summary.dto.response.SummaryResponseDTO;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SummaryService {

    private final OpenAIService openAIService;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    public SummaryResponseDTO oneLineSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        return summary(memberId, summaryRequestDTO.getText(), "다음 글을 전체 내용을 하나의 문장으로 간결하게 요약해줘.");
    }

    public SummaryResponseDTO fullSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        return summary(memberId, summaryRequestDTO.getText(), "다음 글의 전반적인 내용을 여러 문장으로 자연스럽게 요약해줘.");
    }

    public SummaryResponseDTO paragraphSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        return summary(memberId, summaryRequestDTO.getText(), "다음 글의 각 문단의 핵심 내용을 따로따로 요약해줘.");
    }

    public SummaryResponseDTO keyPointSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        return summary(memberId, summaryRequestDTO.getText(), "다음 글의 전체 내용을 문단 구분 없이 핵심 문장 리스트 형식으로 요약해줘.");
    }

    public SummaryResponseDTO questionBasedSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        // question 값 추출
        String question = summaryRequestDTO.getQuestion();
        if(question == null){
            // Todo. 예외 던지기
        }
        // 프롬프트에 삽입
        String prompt = String.format("다음 글을 읽고 '%s'에 대해 요약해서 답변해줘.", question);
        return summary(memberId, summaryRequestDTO.getText(), prompt);
    }

    public SummaryResponseDTO targetedSummary(String memberId, SummaryRequestDTO summaryRequestDTO){
        // target 값 추출
        String target = summaryRequestDTO.getTarget();
        if(target == null){
            // Todo. 예외 던지기
        }
        // 프롬프트에 삽입
        String prompt = String.format("다음 글을 '{target}'에게 설명하듯 요약해줘.", target);
        return summary(memberId, summaryRequestDTO.getText(), prompt);
    }

    // 1. 요약 메서드
    private SummaryResponseDTO summary(String memberId, String summarizeRequestedText, String summarizeMode){

        // 1. 로그인한 멤버 정보 가져오기 - 멤버의 요금제 정보
        Member member=memberRepository.findById(memberId).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 사용자입니다."));
//        Member member = Member.builder()
//                .memberId(1L)
//                .planId(1L)
//                .id("user01")
//                .email("user01@example.com")
//                .pwd(null)
//                .loginType(LoginType.LOCAL)
//                .role("ROLE_USER")
//                .build();

        // 2. 요금제 정책에 따라 다음 로직 분기
        //    (현재안: 일일 횟수 + 월 토큰 / 대안안: 월 토큰만)

        // 2-1. 남은 월 토큰 확인 (DB나 Redis에서 누적 사용량 조회)
        Plan userPlan = Plan.fromId(member.getPlanId());
        validateRemainingMonthlyTokens(memberId, userPlan, summarizeRequestedText);

        // 3. 요약 처리 (service 호출)
        String result = openAIService.callSummaryOpenAI(summarizeRequestedText, summarizeMode);

        /**
         * Todo. 히스토리 업데이트
         */
        // 4. 내용 저장 (히스토리 업데이트)

        // 5. 사용량 업데이트
        //    - 월 토큰 사용량 증가
        redisService.incrementMonthlyUsage(memberId, YearMonth.now().toString(), GptTokenUtil.estimateTokenCount(summarizeRequestedText));

        // 6. result return
        SummaryResponseDTO responseDTO = SummaryResponseDTO.builder().result(result).build();
        return responseDTO;
    }

    private void validateRemainingMonthlyTokens(String memberId, Plan plan, String text){

        // 1. 현재까지 사용량
        long usedTokensThisMonth = findOrInitializeMonthlyUsage(memberId, YearMonth.now().toString());

        // 2. 요청 텍스트 토큰 수
        int requestedTokens = GptTokenUtil.estimateTokenCount(text);

        // 3. 남은 토큰
        long remaining = plan.getMaxTokensPerMonth() - usedTokensThisMonth;

        // 4. 남은 월 토큰 < 요청 토큰이면 → 에러 응답 반환
        if (remaining < requestedTokens) {
            /**
             * Todo.
             * '월 토큰 한도를 초과하였습니다.' 예외 던지기
             *  + 현재 입력된 토큰 수 및 남은 토큰 수 출력
             */
        }
    }

    private long findOrInitializeMonthlyUsage(String memberId, String month){
        // 1. Redis에서 조회
        Long value = redisService.getMonthlyUsage(memberId, month);
        if (value >= 0) {
            // Redis에 값이 있으면 바로 반환
            return value;
        }

        /**
         * Todo. Redis에 없으면 DB에서 조회
         */
        // 2-1. Redis에 없으면 DB에서 조회
//        Long dbValue = userTokenUsageRepository
//                .findUsedTokens(memberId, month)  // ex) Optional<Long>
//                .orElse(0L);
        Long dbValue = 0L;

        // 2-2. Redis에 캐싱
        redisService.setMonthlyUsage(memberId, month, dbValue);

        return dbValue;
    }

}
