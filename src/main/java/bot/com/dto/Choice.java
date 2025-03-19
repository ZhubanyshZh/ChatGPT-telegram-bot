package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {

    @JsonProperty("index")
    private String index;

    @JsonProperty("message")
    private Message message;

}
