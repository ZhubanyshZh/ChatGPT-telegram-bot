package bot.com.telegram.handler.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartCommandHandler implements CommandHandler {

    @Value("${messages.welcome.ru}")
    private String welcomeMessageRu;

    @Override
    public boolean canHandle(String message) {
        return "/start".equals(message);
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(welcomeMessageRu)
                .parseMode("MarkdownV2")
                .build();

        var keyboards = new ArrayList<KeyboardRow>();
        var keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Сменить язык");
        keyboardRow1.add("Помощь");
        var keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Сбросить все предыдущие сообщения");
        keyboards.add(keyboardRow1);
        keyboards.add(keyboardRow2);
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(keyboards)
                .build();

        message.setReplyMarkup(keyboardMarkup);
        telegramClient.execute(message);
    }
}
