package bot.com.telegram.service;

import bot.com.client.TelegramFeignClient;
import bot.com.dto.Message;
import bot.com.dto.TelegramFileResponse;
import bot.com.telegram.model.Language;
import bot.com.telegram.model.MessageEntry;
import bot.com.telegram.model.UserChatHistory;
import bot.com.telegram.repository.UserChatHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TelegramBotService {
    private final UserChatHistoryRepository userChatHistoryRepository;
    private final AIService aiService;
    private final TelegramService telegramService;
    private final String promptContent;

    public TelegramBotService(UserChatHistoryRepository userChatHistoryRepository,
                              AIService aiService,
                              TelegramService telegramService,
                              @Value("${messages.prompt.content}") String promptContent) {
        this.userChatHistoryRepository = userChatHistoryRepository;
        this.aiService = aiService;
        this.telegramService = telegramService;
        this.promptContent = promptContent;

    }

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
                .build());
    }






}


