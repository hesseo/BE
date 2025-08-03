package com.phraiz.back.cite.controller;

import com.phraiz.back.cite.dto.request.CitationRequestDTO;
import com.phraiz.back.cite.dto.request.RenameRequestDTO;
import com.phraiz.back.cite.dto.response.CitationResponseDTO;
import com.phraiz.back.cite.dto.response.ZoteroItem;
import com.phraiz.back.cite.service.CiteConvertService;
import com.phraiz.back.cite.service.CiteService;
import com.phraiz.back.cite.service.CiteTranslationService;
import com.phraiz.back.common.security.user.CustomUserDetails;
import com.phraiz.back.member.domain.Member;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//👉 Zotero API 활용: URL만 던지면, 메타데이터 자동 수집
// url 의 메타데이터를 가지고 오려면 zotero translation server 사용해야 함
//👉 Zotero는 책, 논문, 웹페이지 구분 없이 잘 처리함
//
//👉 결과 받아서 포맷 생성 (APA/MLA)
//
//비용: 무료, 다만 설정 약간 필요 (OAuth or API 토큰)

@RestController
@RequestMapping("/api/cite")
@AllArgsConstructor
public class CiteController {
    private final CiteConvertService citeConvertService;
    private final CiteTranslationService citeTranslationService;
    private final CiteService citeService;

    // 1. 인용문 저장 과정
    // 1-1. 메타데이터 가지고오고 CSL json으로 변환 후 csl, cite_id 응답보내기
    @PostMapping("/getUrlData")
    public ResponseEntity<Map<String, Object>> getUrlData(@RequestBody Map<String, String> request,  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member=userDetails.getMember();
        Map<String, Object> response = new HashMap<>();
        String url=request.get("url");

        // 1. URL 을 Zotero Translation Server 에 보내서 논문 등의 메타데이터를 가져옴
        ZoteroItem item = citeTranslationService.translateFromUrl(url);
        // 2. cslJson 으로 변환
        JSONObject cslJson=citeConvertService.toCSL(item);
        String csl=cslJson.toString();
        // 3. cslJson & url 저장
        // 응답으로 식별자도 리턴
        Long citeId=citeService.saveCslJson(csl,url,member);

        response.put("csl", csl);
        response.put("cite_id",citeId);

        return ResponseEntity.ok(response);
    }

    // 1-2. 인용문 받아서 저장
    @PostMapping("/getCitation")
    public ResponseEntity<Map<String, Object>> getCitation(@RequestBody CitationRequestDTO citationRequestDTO) {

        citeService.saveCitation(citationRequestDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인용문 저장 완료");
        return ResponseEntity.ok(response);
    }
    // 2. 히스토리
    // 2-1. 사용자별 저장된 인용문 리스트 가져오기
    @GetMapping("/myCitations")
    public ResponseEntity<List<CitationResponseDTO>> getMyCitations(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member=userDetails.getMember();
        List<CitationResponseDTO> citationList = citeService.getMyCitations(member);

        return ResponseEntity.ok(citationList);
    }
    // 2-2. 파일 이름 변경
    @PatchMapping("/renameCiteFile")
    public ResponseEntity<?> renameCiteFile(@RequestBody RenameRequestDTO renameRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        citeService.renameCiteFile(renameRequestDTO, member);

        return ResponseEntity.ok(Map.of("message", "파일 이름이 성공적으로 변경되었습니다."));
    }
    // 2-3. 파일 삭제
    @DeleteMapping("/deleteCiteFile")
    public ResponseEntity<Map<String, Object>> deleteCiteFile(@RequestBody Map<String, Long> request, @AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        Long citeId=request.get("citeId");
        boolean result = citeService.deleteCiteFile(citeId,member);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("result", true);
            response.put("message", "파일이 성공적으로 삭제되었습니다.");
        }else {
            response.put("result", false);
            response.put("message", "파일이 삭제되지 않았습니다.");
        }
        return ResponseEntity.ok(response);

    }

    // TODO 폴더 생성은 Basic 부터
    // 3. 폴더
    // 3-1. 폴더 생성
    // 3-2. 폴더 이름 변경
    // 3-3. 폴더 삭제
    // 3-4. 파일 위치 변경


}
