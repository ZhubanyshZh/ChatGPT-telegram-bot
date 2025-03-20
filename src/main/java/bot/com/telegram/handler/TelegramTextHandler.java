package bot.com.telegram.handler;

import bot.com.handler.TelegramBotHandler;
import bot.com.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramTextHandler extends TelegramBotHandler<Update> {

    private final TelegramBotService telegramBotService;

    @Override
    @Async("telegramExecutor")
    public void handle(Update update) {
        consumeMessage("Message: " +
                        update.getMessage().getText() + ", Chat ID: " +
                        update.getMessage().getChatId(),
                update,
                telegramBotService::handleUpdate);
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }
}
