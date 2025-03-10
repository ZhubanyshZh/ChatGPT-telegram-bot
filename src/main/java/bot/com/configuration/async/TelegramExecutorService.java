package bot.com.configuration.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TelegramExecutorService {

    @Value("${telegram.executor.pool-size:2}")
    private int poolSize;

    @Bean
    public ExecutorService telegramExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
