package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramFileResponse {
    private boolean ok;

    @JsonProperty("result")
    private TelegramFileResult result;
}
