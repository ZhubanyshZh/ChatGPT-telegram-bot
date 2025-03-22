package bot.com.handler.command;

import bot.com.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChangeLanguageCommandHandler implements CommandHandler {

    private final TelegramService telegramService;

    @Override
    public boolean canHandle(String message) {
        return "Сменить язык".equalsIgnoreCase(message);
    }

    @Override
    public void handle(Update update) {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text("*Выберите язык:*")
                .build();

        InlineKeyboardRow englishLanBtn = new InlineKeyboardRow();
        englishLanBtn.add(
                InlineKeyboardButton.builder()
                .text("🇬🇧 English")
                .callbackData("/EN")
                .build());

        InlineKeyboardRow kazakhLanBtn = new InlineKeyboardRow();
        kazakhLanBtn.add(
                InlineKeyboardButton.builder()
                        .text("🇰🇿 Қазақша")
                        .callbackData("/KZ")
                        .build());

        InlineKeyboardRow russianLanBtn = new InlineKeyboardRow();
        russianLanBtn.add(
                InlineKeyboardButton.builder()
                        .text("🇷🇺 Русский")
                        .callbackData("/RU")
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
        telegramService.sendMessage(message);
    }
}