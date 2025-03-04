package bot.com.telegram.consumer;

import bot.com.handler.TelegramBotHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBotConsumer implements LongPollingUpdateConsumer {

    private final List<TelegramBotHandler<Update>> telegramBotHandlers;

    @Override
    public void consume(List<Update> list) {
        list.forEach(update -> telegramBotHandlers.forEach(handler -> {
            if(handler.isApplicable(update)) {
                handler.handle(update);
            }
        }));
    }
}
