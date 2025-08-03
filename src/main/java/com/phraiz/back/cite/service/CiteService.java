package com.phraiz.back.cite.service;

import com.phraiz.back.cite.domain.Cite;
import com.phraiz.back.cite.dto.request.CitationRequestDTO;
import com.phraiz.back.cite.dto.request.RenameRequestDTO;
import com.phraiz.back.cite.dto.response.CitationResponseDTO;
import com.phraiz.back.cite.exception.CiteErrorCode;
import com.phraiz.back.cite.repository.CiteRepository;
import com.phraiz.back.common.exception.custom.BusinessLogicException;
import com.phraiz.back.member.domain.Member;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ListItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.helper.json.JsonLexer;
import de.undercouch.citeproc.helper.json.JsonParser;
import de.undercouch.citeproc.output.Citation;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CiteService {
    private final CiteRepository citeRepository;

    // 0. 소유권 체크(본인만 수정)
    public void checkCiteOwnership(Long memberId, Long citeOwnerId){
        if (!memberId.equals(citeOwnerId)) {
            throw new BusinessLogicException(CiteErrorCode.NO_PERMISSION_TO_UPDATE);
        }

    }
    // 1. 인용문 저장 과정
    // 1-1. 처음에 요청받은 url과 변환된 csl json 저장
    // 식별자 리턴
    public Long saveCslJson(String cslJson, String url, Member member) {
        Cite cite = Cite.builder()
                .member(member)
                .cslJson(cslJson)
                .title("제목없음")
                .url(url)
                .build();
        Cite result=citeRepository.save(cite);
        return result.getCiteId();
    }

    // 1-2. 인용문과 스타일 저장
    @Transactional
    public void saveCitation(CitationRequestDTO citationRequestDTO) {
        Cite cite = citeRepository.findById(citationRequestDTO.getCiteId())
                .orElseThrow(() -> new BusinessLogicException(CiteErrorCode.CITE_NOT_FOUND));

        cite.setCitation(citationRequestDTO.getCitation());
        cite.setStyle(citationRequestDTO.getStyle());
    }

    // 2. 히스토리
    // 2-1. 사용자별 저장된 인용문 리스트 가져오기
    public List<CitationResponseDTO> getMyCitations(Member member) {
        List<Cite> list=citeRepository.findAllByMemberOrderByCreatedAtDesc(member);
        return list.stream()
                .map(cite -> CitationResponseDTO.builder()
                        .citeId(cite.getCiteId())
                        .title(cite.getTitle())
                        .style(cite.getStyle())
                        .citation(cite.getCitation())
                        .url(cite.getUrl())
                        .createdAt(cite.getCreatedAt())
                        .build())
                .toList();
    }

    // 2-2. 파일 이름 변경
    public void renameCiteFile(RenameRequestDTO renameRequestDTO, Member member) {
        Cite cite = citeRepository.findById(renameRequestDTO.getCiteId())
                .orElseThrow(() -> new BusinessLogicException(CiteErrorCode.CITE_NOT_FOUND));

        // 소유권 체크 (본인만 수정 가능)
        checkCiteOwnership(member.getMemberId(), cite.getMember().getMemberId());

        cite.setTitle(renameRequestDTO.getNewTitle()); // 새로운 제목으로 파일 이름 수정

    }

    // 2-3. 파일 삭제
    public boolean deleteCiteFile(Long citeId, Member member) {
        Cite cite=citeRepository.findById(citeId).orElseThrow(() -> new BusinessLogicException(CiteErrorCode.CITE_NOT_FOUND));
        // 소유권 체크 (본인만 삭제 가능)
        checkCiteOwnership(member.getMemberId(), cite.getMember().getMemberId());
        // 삭제
        citeRepository.deleteById(citeId);
        return !citeRepository.existsById(citeId);
    }

    // 인용문 생성
//    public String generateCite(String style, JSONObject cslJson){
//        try {
//            // jsonObject 를 cslItemData 로 변환
//            // JSONObject를 CSLItemData로 변환
//            CSLItemData itemData = jsonToCSLItemData(cslJson);
//
//            // ListItemDataProvider 사용 (이미 구현된 클래스)
//            ListItemDataProvider provider = new ListItemDataProvider(itemData);
//
//            String styleContent = loadStyleContent(style);
//            CSL csl = new CSL(provider, styleContent);
//
//            List<Citation> citations = csl.makeCitation(cslJson.getAsString("id"));
//            Citation citation = citations.get(0);
//
//            return citation.getText();
//        } catch (IOException e) {
//            throw new RuntimeException("인용문 생성 실패",e);
//        }
//
//    }

    private String loadStyleContent(String style) {
        try {
            // 리소스 폴더에서 로드
            ClassPathResource resource = new ClassPathResource("csl-styles/" + style + ".csl");
            return Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private CSLItemData jsonToCSLItemData(JSONObject cslJson) {
        try {
            // jsonObject->String
            String jsonString = cslJson.toString();
            // String->Reader
            StringReader sr = new StringReader(jsonString);
            JsonLexer lexer = new JsonLexer(sr);
            JsonParser parser = new JsonParser(lexer);

            return CSLItemData.fromJson(parser.parseObject());

        } catch (Exception e) {
            throw new RuntimeException("CSLItemData 변환 실패", e);
        }
        }
}
