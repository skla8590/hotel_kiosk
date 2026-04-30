package hotel_kiosk.controller.customer;

import hotel_kiosk.service.customer.ai.AiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/JHotel/ai_chat")
public class AIChatController {
    private final AiService aiService;

    @GetMapping("")
    public String aiChat(HttpSession session, Model model) {
        String question = (String) session.getAttribute("finalQuestion");
        model.addAttribute("question", question);

        return "customer/ai_service/ai_chat";
    }

    // AIChatController.java 수정 제안

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody // JSON 형태로 질문과 답변을 함께 반환
    public Map<String, Object> aiChatVoice(
            @RequestParam(value = "file", required = false) MultipartFile file, // 필수 해제
            @RequestParam(value = "question", required = false) String question, // 텍스트 질문 추가
            @RequestParam(value = "score", defaultValue = "0.0") double score,
            @RequestParam("source") String source,
            HttpSession session
    ) throws Exception {
        String finalQuestion = question;
        if (file != null && !file.isEmpty()) {
            finalQuestion = aiService.stt(file.getBytes());
        }

        // 1. 답변 생성 시작
        String finalQ = finalQuestion;
        CompletableFuture<String> answerFuture = CompletableFuture.supplyAsync(() ->
                aiService.chatWithCompression(finalQ, score, source, session.getId()));

        // 2. 추천 질문 생성 시작 (동시 진행)
        CompletableFuture<List<String>> suggestFuture = CompletableFuture.supplyAsync(() ->
                aiService.suggest(finalQ));

        // 두 작업이 모두 끝날 때까지 기다림
        CompletableFuture.allOf(answerFuture, suggestFuture).join();

        return Map.of(
                "question", finalQuestion,
                "answer", answerFuture.get(),
                "suggestions", suggestFuture.get()
        );
    }
}
