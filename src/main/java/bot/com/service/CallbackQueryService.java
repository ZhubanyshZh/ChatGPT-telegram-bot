package bot.com.service;

import bot.com.handler.callbackquery.CallBackQueryHandler;
import bot.com.handler.callbackquery.CallBackQueryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class CallbackQueryService {

    private final TelegramService telegramService;
    private final CallBackQueryRegistry callBackQueryRegistry;

    public void handleCallBackQuery(Update update) {
        if(processCallbackQuery(update, update.getCallbackQuery().getData())) return;

        telegramService.sendMessage(SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .text("Error Callback query")
                .build());
    }

    private boolean processCallbackQuery(Update update, String userMessage) {
        CallBackQueryHandler handler = callBackQueryRegistry.findHandler(userMessage);
        if (handler != null) {
            handler.handle(update);
            return true;
        }
        return false;
    }
}
