package bot.com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
public class TelegramService {
    private final TelegramClient telegramClient;

    public TelegramService(@Value("${bot.token}") String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    public void sendMessage(BotApiMethod<?> message) {
        try {
            telegramClient.execute(message);
            log.info("Сообщение отправлено");
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }
}

