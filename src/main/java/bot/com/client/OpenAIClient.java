package bot.com.client;

import bot.com.dto.CompletionBodyDto;
import bot.com.dto.OpenAIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "deepinfra-client", url = "${chat-gpt.chat-url}")
public interface OpenAIClient {

    @PostMapping
    OpenAIResponse chat(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CompletionBodyDto completionDto
    );
}
