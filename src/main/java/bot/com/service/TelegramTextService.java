package bot.com.service;

import bot.com.model.UserChatHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class TelegramTextService {
    private final TelegramBotService telegramBotService;
    private final CommandService commandService;
    public TelegramTextService(TelegramBotService telegramBotService, CommandService commandService) {
        this.commandService=commandService;
        this.telegramBotService = telegramBotService;
    }

    public void handleTextMessage(Update update) {

        String chatId = update.getMessage().getChatId().toString();
        String userMessage = update.getMessage().getText();
        if (commandService.processCommand(update, update.getMessage().getText())) return;

        UserChatHistory history = telegramBotService.getUserChatHistory(chatId);
        telegramBotService.addUserMessageToHistory(history, userMessage);

        String responseMessage = telegramBotService.generateResponse(history);
        telegramBotService.saveAndSendResponse(history, responseMessage);
    }
}
