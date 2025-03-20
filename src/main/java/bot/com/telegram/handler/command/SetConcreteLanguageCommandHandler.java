package bot.com.telegram.handler.command;

import bot.com.telegram.model.Language;
import bot.com.telegram.repository.UserChatHistoryRepository;
import bot.com.telegram.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SetConcreteLanguageCommandHandler implements CommandHandler {

    private final UserChatHistoryRepository userChatHistoryRepository;
    private final TelegramService telegramService;

    @Override
    public boolean canHandle(String message) {
        return "RU".equals(message) || "EN".equals(message) || "KZ".equals(message);
    }

    @Override
    public void handle(Update update) {
        var chatId = update.getMessage().getChatId().toString();
        Language.getByName(update.getMessage().getText().trim())
                .ifPresentOrElse(lang ->
                        userChatHistoryRepository.findById(chatId)
                                .ifPresent(
                                        history -> {
                                            history.setCurrentLanguage(lang);
                                            userChatHistoryRepository.save(history);
                                            telegramService.sendMessage(
                                                    SendMessage.builder()
                                                            .chatId(chatId)
                                                            .text("Язык успешно изменен")
                                                            .build()
                                            );
                                        }
                                ),
                        () -> {
                            var errorLangMessage = SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Выберите язык из предложенных вариантов")
                                    .build();
                            telegramService.sendMessage(errorLangMessage);
                        }
                );
    }
}
