package bot.com.service;

import bot.com.model.UserChatHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramTextService {

    private final AIService aiService;
    private final CommandService commandService;
    private final UserChatHistoryService userChatHistoryService;

    public void handleTextMessage(Update update) {

        String chatId = update.getMessage().getChatId().toString();
        String userMessage = update.getMessage().getText();
        if (commandService.processCommand(update, update.getMessage().getText())) return;

        UserChatHistory history = userChatHistoryService.getUserChatHistory(chatId);
        userChatHistoryService.addUserMessageToHistory(history, userMessage);

        String responseMessage = aiService.generateResponse(history);
        userChatHistoryService.saveAndSendResponse(history, responseMessage);
    }
}
