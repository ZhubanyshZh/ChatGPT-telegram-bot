package bot.com.configuration;

import bot.com.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    @Value("${bot.token}")
    private String botToken;
    private final TelegramBot telegramBot;

    @Bean
    public TelegramBotsLongPollingApplication botsApplication() throws TelegramApiException {
        var app = new TelegramBotsLongPollingApplication();
        app.registerBot(botToken, telegramBot);
        return app;
    }
}
