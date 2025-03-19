package bot.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "bot.com.client")
public class SimpleTelegramBot {

    public static void main(String[] args) {
        SpringApplication.run(SimpleTelegramBot.class, args);
    }
}