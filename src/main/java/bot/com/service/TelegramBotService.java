package bot.com.service;

import bot.com.dto.Message;
import bot.com.model.Language;
import bot.com.model.MessageEntry;
import bot.com.model.UserChatHistory;
import bot.com.repository.UserChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class TelegramBotService {

    @Value("${telegram.parse-mode}")
    private String parseMode;
    @Value("${messages.prompt.content}")
    private String promptContent;

    private final UserChatHistoryRepository userChatHistoryRepository;
    private final AIService aiService;
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

    public String generateResponse(UserChatHistory history) {
        List<Message> conversation = history.getMessages().stream()
                .map(entry -> new Message(entry.getRole(), entry.getContent()))
                .toList();
        return aiService.getResponse(conversation);
    }

    public void saveAndSendResponse(UserChatHistory history, String responseMessage) {
        log.info("Ответ от AI: {}", responseMessage);

        history.getMessages().add(new MessageEntry("assistant", responseMessage, Instant.now()));
        userChatHistoryRepository.save(history);

        telegramService.sendMessage(SendMessage.builder()
                .chatId(history.getChatId())
                .text(responseMessage)
                .parseMode(parseMode)
                .build());
    }
}

