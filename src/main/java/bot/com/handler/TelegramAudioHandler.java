package bot.com.handler;

import bot.com.service.TelegramAudioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAudioHandler extends TelegramBotHandler<Update> {
    private final TelegramAudioService telegramBotService;

    @Override
    @Async("telegramExecutor")
    public void handle(Update update) {
        consumeMessage("Message: " +
                        update.getMessage().getText() + ", Chat ID: " +
                        update.getMessage().getChatId(),
                update,
                telegramBotService::handleAudioMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasMessage() && (update.getMessage().hasVoice() || update.getMessage().hasAudio());
    }
}
