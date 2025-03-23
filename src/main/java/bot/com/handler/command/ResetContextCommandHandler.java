package bot.com.handler.command;

import bot.com.service.TelegramService;
import bot.com.service.UserChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ResetContextCommandHandler implements CommandHandler {

    private final UserChatHistoryService userChatHistoryService;
    private final TelegramService telegramService;

    @Override
    public boolean canHandle(String message) {
        return "Сбросить контекст".equalsIgnoreCase(message);
    }

    @Override
    @Transactional
    public void handle(Update update) {
        var chatId = update.getMessage().getChatId().toString();
        userChatHistoryService.resetUserChatHistory(chatId);
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("Контекст успешно сброшен")
                .build();
        telegramService.sendMessage(message);
    }
}
