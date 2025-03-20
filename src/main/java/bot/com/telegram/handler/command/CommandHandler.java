package bot.com.telegram.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    boolean canHandle(String message);
    void handle(Update update);

}
