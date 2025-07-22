package com.phraiz.back.cite.controller;

import com.phraiz.back.cite.dto.response.ZoteroItem;
import com.phraiz.back.cite.service.CiteConvertService;
import com.phraiz.back.cite.service.CiteService;
import com.phraiz.back.cite.service.CiteTranslationService;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    // 메타데이터 가지고오고 CSL json으로 변환
    @PostMapping("/getUrlData")
    public ResponseEntity<Map<String, Object>> getCite(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String url=request.get("url");

        // 1. URL 을 Zotero Translation Server 에 보내서 논문 등의 메타데이터를 가져옴
        ZoteroItem item = citeTranslationService.translateFromUrl(url);
        // 2. cslJson 으로 변환
        JSONObject cslJson=citeConvertService.toCSL(item);

        response.put("cslJson", cslJson);

        return ResponseEntity.ok(response);
    }
}
