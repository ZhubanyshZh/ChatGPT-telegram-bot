package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompletionBodyDto {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;
}
