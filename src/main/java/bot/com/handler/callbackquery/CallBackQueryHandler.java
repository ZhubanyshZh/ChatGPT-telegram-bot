package bot.com.handler.callbackquery;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallBackQueryHandler {

    void handle(Update update);
    boolean canHandle(String callBackData);
}
