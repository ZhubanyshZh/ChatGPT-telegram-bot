package bot.com.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "chat_history")
public class UserChatHistory {

    @Id
    private String chatId;

    private List<MessageEntry> messages;

    private Language currentLanguage = Language.RU;

}
