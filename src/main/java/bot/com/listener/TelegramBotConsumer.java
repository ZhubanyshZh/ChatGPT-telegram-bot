package bot.com.listener;

import bot.com.handler.TelegramBotHandler;
import bot.com.service.WaitAnswerMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TelegramBotConsumer implements LongPollingUpdateConsumer {

    private final List<TelegramBotHandler<Update>> telegramBotHandlers;
    private final WaitAnswerMessageService waitAnswerMessageService;

    @Override
    public void consume(List<Update> list) {
        list.forEach(update -> telegramBotHandlers.forEach(handler -> {
            if(handler.isApplicable(update)) {
                if(!update.hasCallbackQuery()){
                    waitAnswerMessageService.sendWaitAnswerMessage(update.getMessage().getChatId());
                }
                handler.handle(update);
            }
        }));
    }
}
