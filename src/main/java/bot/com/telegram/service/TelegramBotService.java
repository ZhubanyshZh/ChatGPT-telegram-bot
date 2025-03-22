package bot.com.telegram.service;

import bot.com.dto.Message;
import bot.com.telegram.model.Language;
import bot.com.telegram.model.MessageEntry;
import bot.com.telegram.model.UserChatHistory;
import bot.com.telegram.repository.UserChatHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RefreshScope
public class TelegramBotService {

    @Value("${telegram.parse-mode}")
    private String parseMode;

    private final UserChatHistoryRepository userChatHistoryRepository;
    private final AIService aiService;
    private final TelegramService telegramService;
    private final String promptContent;
    private final CommandService commandService;
    public TelegramBotService(UserChatHistoryRepository userChatHistoryRepository,
                              AIService aiService,
                              TelegramService telegramService,
                              @Value("${messages.prompt.content}") String promptContent,CommandService commandService) {
        this.userChatHistoryRepository = userChatHistoryRepository;
        this.aiService = aiService;
        this.telegramService = telegramService;
        this.promptContent = promptContent;
        this.commandService = commandService;
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
                .parseMode(parseMode)
                .build());
    }

    public void handleCallBackQuery(Update update) {
        if(commandService.processCommand(update, update.getCallbackQuery().getData())) return;

        telegramService.sendMessage(SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .text("Error Callback query")
                .build());
    }
}

