package bot.com.configuration.telegram;

import bot.com.telegram.consumer.TelegramBotConsumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;


@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    @Value("${bot.token}")
    private String botToken;
    private final TelegramBotConsumer telegramBotConsumer;

    @Bean
    @SneakyThrows
    public TelegramBotsLongPollingApplication botsApplication() {
        var app = new TelegramBotsLongPollingApplication();
        app.registerBot(botToken, telegramBotConsumer);
        return app;
    }
}
