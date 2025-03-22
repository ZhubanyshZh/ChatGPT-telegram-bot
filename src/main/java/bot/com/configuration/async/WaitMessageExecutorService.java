package bot.com.configuration.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WaitMessageExecutorService {

    @Value("${telegram.executor.wait-message.pool-size:1}")
    private int poolSize;

    @Bean
    public ExecutorService waitMessageExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
