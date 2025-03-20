package bot.com.telegram.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface CommandHandler {
    boolean canHandle(String message);
    void handle(Update update, TelegramClient telegramClient) throws TelegramApiException;

}
