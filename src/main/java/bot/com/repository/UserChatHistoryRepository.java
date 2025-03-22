package bot.com.repository;

import bot.com.model.UserChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatHistoryRepository extends MongoRepository<UserChatHistory, String> {
}
