package com.phraiz.back.paraphrase.controller;

import com.phraiz.back.common.security.user.CustomUserDetails;
import com.phraiz.back.member.domain.Member;
import com.phraiz.back.paraphrase.dto.request.ParaphraseRequestDTO;
import com.phraiz.back.paraphrase.dto.response.ParaphraseResponseDTO;
import com.phraiz.back.paraphrase.service.ParaphraseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paraphrase")
public class ParaphraseController {

    /*
        AI 패러프레이징 구현
        1. 모드 별 패러프레이징
        2. 패러프레이징 폴더 목록 불러오기
        3. 히스토리 목록 불러오기
         - 매 요청마다?(캐싱 포함) 또는 처음에 싹 다?
        4. 히스토리 이동 요청
     */

    private final ParaphraseService paraphraseService;

    // 1. 모드 별 패러프레이징
    @PostMapping("/paraphrasing/standard")
    public ResponseEntity<?> paraphraseStandard(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody ParaphraseRequestDTO dto) {
        // 로그인한 유저의 ID
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String memberId = authentication.getName(); // 보통 여기서 memberId 반환됨
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseStandard(memberId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrasing/academic")
    public ResponseEntity<?> paraphraseAcademic(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody ParaphraseRequestDTO dto) {
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseAcademic(memberId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrasing/creative")
    public ResponseEntity<?> paraphraseCreative(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody ParaphraseRequestDTO dto) {
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseCreative(memberId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrasing/fluency")
    public ResponseEntity<?> paraphraseFluency(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody ParaphraseRequestDTO dto) {
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseFluency(memberId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrasing/experimental")
    public ResponseEntity<?> paraphraseExperimental(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody ParaphraseRequestDTO dto) {
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseExperimental(memberId, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paraphrasing/custom")
    public ResponseEntity<?> paraphraseCustom(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody ParaphraseRequestDTO dto) {
        String memberId = "user01";
        ParaphraseResponseDTO result = paraphraseService.paraphraseCustom(memberId, dto);
        return ResponseEntity.ok(result);
    }


}
