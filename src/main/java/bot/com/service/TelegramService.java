package bot.com.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
public class TelegramService {
    private final TelegramClient telegramClient;

    public TelegramService(@Value("${bot.token}") String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @SneakyThrows
    public void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            var errorMessage = SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("Ошибка при отправке сообщения")
                            .build();
            telegramClient.execute(errorMessage);
            log.error("Ошибка при отправке сообщения", e);
        }
    }
}

