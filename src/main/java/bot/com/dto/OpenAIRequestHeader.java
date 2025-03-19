package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIRequestHeader {

    @JsonProperty("Authorization")
    private String authorization;

    @JsonProperty("Content-Type")
    private String contentType;
}
