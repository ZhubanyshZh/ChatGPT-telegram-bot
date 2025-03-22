package bot.com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntry {

    private String role; // "user" или "assistant"
    private String content; // Сам текст сообщения
    private Instant timestamp; // Время отправки

}
