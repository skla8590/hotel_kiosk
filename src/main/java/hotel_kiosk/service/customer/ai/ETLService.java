package hotel_kiosk.service.customer.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class ETLService {
    // ChatModel은 KeywordMetadataEnricher에서 키워드를 추출할 때 사용
    private final ChatModel chatModel;
    // VectorStore 필드는 Document를 벡터 저장소에 저장할 때 사용.
    private final VectorStore vectorStore;

    public ETLService(ChatModel chatModel,
                      @Qualifier("pgVectorStore") VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    private List<Document> extractFromFile(MultipartFile attach) throws IOException {
        /* 업로드된 파일로부터 텍스트를 추출하는 메서드 */
        // 1. MultipartFile의 getBytes() 메서드를 사용하여 파일의 내용을 바이트 배열로 가져옴.
        Resource resource = new ByteArrayResource(attach.getBytes());

        // 2. 파일의 Content-Type에 따라 적절한 DocumentReader를 선택하여 텍스트를 추출함.
        List<Document> documents = null;
        if (attach.getContentType().equals("application/pdf")) {
            // PDF(.pdf) 파일일 경우
            // PDF 파일에서 텍스트를 추출할 때는 PagePdfDocumentReader 사용.
            // PDF 페이지 단위로 Document를 생성.
            // PDF 파일 내에 페이지가 10개라면 10개의 Document를 생성.
            DocumentReader reader = new PagePdfDocumentReader(resource);
            documents = reader.read();
        }

        return documents;
    }

    private List<Document> transform(List<Document> documents) {
        /* 추출된 Document 리스트를 작은 크기로 분할하고, 키워드 메타데이터를 추가하는 메서드 */
        List<Document> transformedDocuments = null;
        
        // 1. 작은 크기로 분할
        // TokenTextSplitter는 텍스트를 토큰 단위로 분할하는 클래스.
        // apply() 메서드를 호출하여 Document 리스트를 분할된 Document 리스트로 변환
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        transformedDocuments = tokenTextSplitter.apply(documents);
        
        // 2. 키워드 메타데이터를 추가
        // 잘게 나눈 Document에서 키워드 5개를 추출 -> 누가 키워드를 알려줄까? -> LLM
        // LLM을 통해 분할된 각각의 Document 콘텐츠에서 키워드를 알아내고, 해당 Document의 메타데이터로 추가.
        // 각 청크 Document마다 수행해야 하므로 비용과 시간이 증가. 핵심 기능은 아니기 때문에 주석 처리해도 됨.
//        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
//        transformedDocuments = keywordMetadataEnricher.apply(transformedDocuments);

        return transformedDocuments;
    }

    public boolean isAlreadyLoaded(String source) {
        /* vector 데이터가 이미 적재되었는지 확인하는 메서드 */
        List<Document> docs = vectorStore.similaritySearch("호텔 정책");

        if (docs == null || docs.isEmpty()) {
            return false;
        }

        return docs.stream()
                .anyMatch(doc -> source.equals(doc.getMetadata().get("source")));
    }

    public void etlFromPath(String filePath, String title, String author) throws IOException {

        Resource resource = new ClassPathResource(filePath);

        DocumentReader reader = new PagePdfDocumentReader(resource);
        List<Document> documents = reader.read();

        log.info("추출된 Document 수: {}", documents.size());

        String sourceName = resource.getFilename();

        // 공통 메타데이터
        for (Document doc : documents) {
            doc.getMetadata().putAll(Map.of(
                    "title", title,
                    "author", author,
                    "source", sourceName
            ));
        }

        // 변환
        documents = transform(documents);
        log.info("변환된 Document 수: {}", documents.size());

        // 적재
        vectorStore.add(documents);
    }

    public String etlFromFile(String title, String author, MultipartFile attach) throws IOException {
        /* 업로드된 파일로부터 텍스트를 추출하고, 이를 변환하여 벡터 스토어에 적재하는 메서드 - ETL 과정 */

        // 1. E : 추출하기
        List<Document> documents = extractFromFile(attach);
        if (documents == null) {
            return ".txt, .pdf, .doc, .docx 파일 중에 하나를 올려주세요.";
        }
        log.info("추출된 Document 수: {}", documents.size());

        // 2. T : 메타데이터에 공통 정보(사용자 입력 정보) 추가하기
        // 공통 메타데이터는 Document가 작게 분할되더라도 유지.
        for (Document doc : documents) {
            Map<String, Object> metadata = doc.getMetadata();
            metadata.putAll(Map.of(
                    "title", title,
                    "author", author,
                    "source", attach.getOriginalFilename()));
        }

        // 2. T : 변환하기 -> Document를 작은 크기로 분할.
        documents = transform(documents);
        log.info("변환된 Document 수: {} 개", documents.size());

        // 3. L : 적재하기
        // Document 목록을 벡터 저장소에 저장(적재).
        // 이때, Document 텍스트 콘텐츠는 임베딩.
        vectorStore.add(documents);

        return "올린 문서를 추출-변환-적재 완료했습니다.";
    }
}
