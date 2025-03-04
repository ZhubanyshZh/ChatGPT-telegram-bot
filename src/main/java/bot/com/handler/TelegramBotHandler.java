package bot.com.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

@Slf4j
@Component
public abstract class TelegramBotHandler<T> implements EventHandler<Update> {

    protected <Text> void consumeMessage(Text text, T t, Consumer<T> process) {
        log.info("Consuming message: {}", text);
        process.accept(t);
    }

}
