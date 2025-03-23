package bot.com.handler.callbackquery;

import bot.com.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

@Component
@RequiredArgsConstructor
public class DeleteCallBackQueryHandler implements CallBackQueryHandler {

    @Value("${telegram.bot.delete-callback-query:false}")
    private boolean isDeleteCallBackQuery;
    private final TelegramService telegramService;

    @Override
    @Order(Integer.MIN_VALUE)
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        DeleteMessage delete = DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build();

        telegramService.sendMessage(delete);
    }

    @Override
    public boolean canHandle(String callBackData) {
        return isDeleteCallBackQuery;
    }
}
