package bot.com.handler.command;

import bot.com.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AboutBotCommandHandler implements CommandHandler {

    @Value("${messages.welcome.ru}")
    private String aboutBotMessage;

    private final TelegramService telegramService;

    @Override
    public boolean canHandle(String message) {
        return "О боте".equals(message);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        var message = SendMessage.builder()
                        .text(aboutBotMessage)
                        .chatId(chatId.toString())
                        .build();
        telegramService.sendMessage(message);
    }
}
