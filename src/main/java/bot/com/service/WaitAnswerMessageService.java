package bot.com.service;

import bot.com.repository.UserChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WaitAnswerMessageService {

    private final TelegramService telegramService;
    private final UserChatHistoryRepository userChatHistoryRepository;
    private final Map<String, String> waitMessagesByLang = new HashMap<>(Map.of(
            "RU", "Пожалуйста, подождите ответа",
            "EN", "Please wait for the answer",
            "KZ", "Жауап күтіңіз"
    ));

    @Async("waitMessageExecutor")
    public void sendWaitAnswerMessage(Long chatId) {
        var lang = userChatHistoryRepository.findById(chatId.toString())
                .map(history -> history.getCurrentLanguage().name())
                .orElse("RU");
        telegramService.sendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(waitMessagesByLang.get(lang))
                .build());
    }
}
