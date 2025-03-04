package bot.com.telegram.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBotService {

    private final TelegramClient telegramClient;

    public TelegramBotService(@Value("${bot.token}") String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @SneakyThrows
    public void sendMessage(Update update) {
        if("/start".equals(update.getMessage().getText())) {
            SendMessage message = createReplyKeyboard(update.getMessage().getChatId());
            telegramClient.execute(message);
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text("Ответ: " + update.getMessage().getText())
                .build();

        telegramClient.execute(message);
    }

    private SendMessage createReplyKeyboard(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите команду:")
                .build();

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .build();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Привет");
        row1.add("Как дела?");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Помощь");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
