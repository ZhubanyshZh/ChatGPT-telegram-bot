package bot.com.client;

import bot.com.dto.TelegramFileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "telegramClient", url = "https://api.telegram.org")
public interface TelegramFeignClient {

    @GetMapping("/bot{token}/getFile")
    TelegramFileResponse getFile(@PathVariable("token") String token,
                                 @RequestParam("file_id") String fileId);
}
