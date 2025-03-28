package bot.com.service;

import bot.com.model.Language;
import bot.com.model.MessageEntry;
import bot.com.model.UserChatHistory;
import bot.com.repository.UserChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserChatHistoryService {

    @Value("${messages.prompt.content}")
    private String promptContent;
    @Value("${telegram.parse-mode}")
    private String parseMode;

    private final UserChatHistoryRepository userChatHistoryRepository;
    private final TelegramService telegramService;

    public UserChatHistory getUserChatHistory(String chatId) {
        return userChatHistoryRepository.findById(chatId)
                .orElseGet(() -> UserChatHistory.builder()
                        .chatId(chatId)
                        .messages(new ArrayList<>())
                        .currentLanguage(Language.RU)
                        .build());
    }

    public void addUserMessageToHistory(UserChatHistory history, String userMessage) {
        history.getMessages().addAll(List.of(
                new MessageEntry("user", promptContent + history.getCurrentLanguage(), Instant.now()),
                new MessageEntry("user", userMessage, Instant.now())
        ));
    }

    public void saveAndSendResponse(UserChatHistory history, String responseMessage) {
        log.info("Ответ от AI: {}", responseMessage);

        history.getMessages().add(new MessageEntry("assistant", responseMessage, Instant.now()));
        saveUserChatHistory(history);

        telegramService.sendMessage(SendMessage.builder()
                .chatId(history.getChatId())
                .text(responseMessage)
                .parseMode(parseMode)
                .build());
    }

    private UserChatHistory saveUserChatHistory(UserChatHistory history) {
        return userChatHistoryRepository.save(history);
    }

    public void resetUserChatHistory(String chatId) {
        userChatHistoryRepository.deleteById(chatId);
    }
}
