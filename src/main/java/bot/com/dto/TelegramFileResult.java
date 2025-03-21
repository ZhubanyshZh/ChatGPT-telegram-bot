package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramFileResult {
    @JsonProperty("file_path")
    private String filePath;
}
