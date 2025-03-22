package bot.com.telegram.handler.callbackquery;

import bot.com.handler.TelegramBotHandler;
import bot.com.telegram.service.CallbackQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramCallBackQueryHandler extends TelegramBotHandler<Update> {

    private final CallbackQueryService callbackQueryService;

    @Override
    public void handle(Update update) {
        consumeMessage("CallBackQuery: " +
                        update.getCallbackQuery().getData() + ", Chat ID: " +
                        update.getCallbackQuery().getMessage().getChatId(),
                update,
                callbackQueryService::handleCallBackQuery);
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery();
    }
}
