package bot.com.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
public class TelegramBot implements LongPollingUpdateConsumer {

    private final TelegramClient telegramClient;
    private final String botToken;

    public TelegramBot(@Value("${bot.token}") String botToken) {
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(List<Update> list) {
        for (Update update : list) {
            if (update.hasMessage()){
                if (update.getMessage().hasText()) {
                    SendMessage message = SendMessage.builder()
                            .chatId(update.getMessage().getChatId().toString())
                            .text("Ответ: " + update.getMessage().getText())
                            .build();

                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        log.error("asdf");
                    }
                } else if (update.getMessage().hasAudio()) {
                    SendMessage message = SendMessage.builder()
                            .chatId(update.getMessage().getChatId().toString())
                            .text("Ответ: " + update.getMessage().getAudio().getFileId())
                            .build();

                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        log.error("asdf");
                    }
                }
            }
        }
    }
}