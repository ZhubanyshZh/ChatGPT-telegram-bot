package bot.com.telegram.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class ChangeLanguageCommandHandler implements CommandHandler {

    @Override
    public boolean canHandle(String message) {
        return "Сменить язык".equalsIgnoreCase(message);
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text("*Выберите язык:*")
                .build();

        InlineKeyboardRow englishLanBtn = new InlineKeyboardRow();
        englishLanBtn.add(
                InlineKeyboardButton.builder()
                .text("🇬🇧 English")
                .callbackData("lan:english")
                .build());

        InlineKeyboardRow kazakhLanBtn = new InlineKeyboardRow();
        kazakhLanBtn.add(
                InlineKeyboardButton.builder()
                        .text("🇰🇿 Қазақша")
                        .callbackData("lan:kazakh")
                        .build());

        InlineKeyboardRow russianLanBtn = new InlineKeyboardRow();
        russianLanBtn.add(
                InlineKeyboardButton.builder()
                        .text("🇷🇺 Русский")
                        .callbackData("lan:russian")
                        .build());

        List<InlineKeyboardRow> keyboard = List.of(
                kazakhLanBtn,
                russianLanBtn,
                englishLanBtn
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setParseMode("MarkdownV2");
        telegramClient.execute(message);
    }
}