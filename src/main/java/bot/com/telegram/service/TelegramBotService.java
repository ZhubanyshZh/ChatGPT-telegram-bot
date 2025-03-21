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

    @Value("${telegram.parse-mode:MarkdownV2}")
    private String textParseMode;

    private final CommandService commandService;
    private final AIService aiService;
    private final TelegramService telegramService;
    private final UserChatHistoryRepository userChatHistoryRepository;
    private final String promptContent;

    public TelegramBotService(CommandService commandService,
                              AIService aiService,
                              TelegramService telegramService,
                              UserChatHistoryRepository userChatHistoryRepository,
                              @Value("${messages.prompt.content}") String promptContent) {
        this.commandService = commandService;
        this.aiService = aiService;
        this.telegramService = telegramService;
        this.userChatHistoryRepository = userChatHistoryRepository;
        this.promptContent = promptContent;
    }

    public void handleUpdate(Update update) {
        try {
            String chatId = update.getMessage().getChatId().toString();
            String userMessage = update.getMessage().getText();

            if (commandService.processCommand(update, update.getMessage().getText())) return;

            UserChatHistory history = userChatHistoryRepository
                    .findById(chatId)
                    .orElseGet(() -> UserChatHistory.builder()
                            .chatId(chatId)
                            .messages(new ArrayList<>())
                            .currentLanguage(Language.RU)
                            .build());

            history.getMessages().addAll(
                    List.of(
                            new MessageEntry("user", promptContent + history.getCurrentLanguage(), Instant.now()),
                            new MessageEntry("user", userMessage, Instant.now())
                    )
            );

            List<Message> conversation = history.getMessages().stream()
                    .map(entry -> new Message(entry.getRole(), entry.getContent()))
                    .toList();

            String responseMessage = aiService.getResponse(conversation);
            log.info("Ответ от AI: {}", responseMessage);

            history.getMessages().add(new MessageEntry("assistant", responseMessage, Instant.now()));
            userChatHistoryRepository.save(history);

            telegramService.sendMessage(SendMessage.builder()
                    .chatId(chatId)
                    .text(responseMessage)
                    .parseMode(textParseMode)
                    .build());

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения", e);;
        }
    }

    public void handleCallBackQuery(Update update) {
        if(commandService.processCommand(update, update.getCallbackQuery().getData())) return;

        telegramService.sendMessage(SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .text("Error Callback query")
                .build());
    }
}

