package hotel_kiosk.hotel_kiosk.service.customer.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@Service
public class AiService {
    private final ChatClient chatClient;
    @Autowired
    private ChatModel chatModel; // ChatModel은 검색 전 모듈들이 사용할 새로운 ChatClient.Builder를 생성할 때 필요.
    @Qualifier("pgVectorStore")
    @Autowired
    private VectorStore vectorStore; // VectorStoreDocumentRetriever를 생성할 때 필요.
    @Autowired
    private ChatMemory chatMemory; // MessageChatMemoryAdvisor에서 대화 기억을 프롬프트에 추가할 때 필요.
    @Autowired
    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                )
                .build();
    }

    private CompressionQueryTransformer compressionQueryTransformer() {
        /* CompressionQueryTransformer를 생성하고 반환하는 메서드 */
        // 새로운 ChatClient를 생성하는 빌더 생성
        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                );

        // 압축 쿼리 변환기 생성 -> LLM 이용 -> LLM 채팅을 함
        CompressionQueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        return queryTransformer;
    }

    private VectorStoreDocumentRetriever createVectorStoreDocumentRetriever(
            double similarityThreshold,
            String source) {
        /* VectorStoreDocumentRetriever 생성하고 반환하는 메서드 */
        // VectorStoreDocumentRetriever는 벡터 저장소에서 유사도 검색을 수행하는 모듈.

        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore) // 유사도 검색을 수행하기 위한 벡터 저장소
                .similarityThreshold(similarityThreshold) // 유사도 임계점수를 지정
                .topK(3) // 상위 3개만
                // 메타데이터 필터링을 위한 코드.
                // 화면에서 출처(source)를 보내면, 동일한 출처인 Document만 유사도 검색을 수행.
                // 출처가 없으면, 전체 Document를 대상으로 유사도 검색을 수행.
                .filterExpression(() -> {
                    FilterExpressionBuilder builder = new FilterExpressionBuilder();
                    if (StringUtils.hasText(source)) {
                        return builder.eq("source", source).build();
                    } else {
                        return null;
                    }
                })
                .build();

        return vectorStoreDocumentRetriever;
    }

    public String chatWithCompression(String question, double score, String source, String conversationId) {
        /* LLM과 대화하는 메서드 */
        // RetrievalAugmentationAdvisor 생성
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(compressionQueryTransformer())
                .documentRetriever(createVectorStoreDocumentRetriever(score, source))
                .build();

        // 프롬프트를 LLM으로 전송하고 응답을 받는 코드
        String answer = this.chatClient.prompt()
                .user(question)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        retrievalAugmentationAdvisor
                )
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        return answer;
    }

    public Flux<String> streamChat(String question,
                                   double score,
                                   String source,
                                   String conversationId) {

        RetrievalAugmentationAdvisor rag = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(compressionQueryTransformer())
                .documentRetriever(createVectorStoreDocumentRetriever(score, source))
                .build();

        return chatClient.prompt()
                .user(question)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        rag
                )
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

    /* 음성을 텍스트로 변환하는 STT() 메서드를 작성 */
    public String stt(byte[] bytes) {
        /* 음성을 텍스트로 변환 */

        // 1. 음성 데이터(byte[])를 ByteArrayResource로 생성
        Resource audioResource = new ByteArrayResource(bytes) {
            // getFilename()을 오버라이드 하여 파일명을 제공
            @Override
            public String getFilename() {
                // OpenAI가 인식할 수 있는 확장자(mp3, wav, m4a 등)를 포함한 이름을 리턴
                return "audio.wav";
            }
        };
        log.info("audioResource: {}", audioResource);

        // 2. 모델 옵션 설정
        // 언어를 명시하지 않으면 자동으로 감지되지만,
        // 명시할 경우 음성의 언어를 판별하는 과정을 생략할 수 있어 처리 속도가 다소 향상될 수 있음.
        // 출력 텍스트는 입력 음성과 동일한 언어로 반환되며,
        // language 값은 ISO 639-1 형식(ko, en)의 언어 코드를 사용해야 함.
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .model("whisper-1")
                .language("ko") // 입력 음성 언어의 종류 설정, 출력 언어에도 영향을 미침
                .build();

        // 3. 프롬프트 생성
        // ByteArrayResource와 모델 옵션을 가지고 AudioTranscriptionPrompt를 생성.
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

        // 4. 모델을 호출하고 응답받기
        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
        String text = response.getResult().getOutput();

        return text;
    }

    public List<String> suggest(String question) {
        try {
            String prompt = """
                    사용자의 질문을 보고
                    사용자의 마지막 질문과 관련된
                    현재 데이터에서 답변이 가능하고
                    호텔 키오스크에서 사용할 수 있는
                    추가 질문 4개를 만들어줘.
                    
                    질문: %s
                    
                    반드시 JSON 배열로만 출력:
                    ["질문1", "질문2", "질문3", "질문4"]
                    """.formatted(question);

            String result = chatClient.prompt().user(prompt).call().content();

            String cleanJson = result.replaceAll("```json", "")
                            .replaceAll("```", "")
                            .trim();

            ObjectMapper mapper = new ObjectMapper();

            List<String> suggestions = mapper.readValue(cleanJson, new TypeReference<List<String>>() {});
            List<String> mutableList = new ArrayList<>(suggestions);
            mutableList.add("기타 문의");

            return mutableList;

        } catch (Exception e) {
            log.error("suggest 파싱 실패", e);
            return List.of("기타");
        }
    }
}
