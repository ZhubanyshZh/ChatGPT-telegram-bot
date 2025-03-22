package bot.com.handler;

import bot.com.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramCallBackQueryHandler extends TelegramBotHandler<Update> {

    private final TelegramBotService telegramBotService;

    @Override
    public void handle(Update update) {
        consumeMessage("CallBackQuery: " +
                        update.getCallbackQuery().getData() + ", Chat ID: " +
                        update.getCallbackQuery().getMessage().getChatId(),
                update,
                telegramBotService::handleCallBackQuery);
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery();
    }
}
