package bot.com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAIResponse {

    @JsonProperty("choices")
    private List<Choice> choices;
}
