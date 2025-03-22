package bot.com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitAnswerMessageService {

    private final TelegramService telegramService;

//    @Async("waitMessageExecutor")
    public void sendWaitAnswerMessage(Long chatId) {
        SendChatAction chatAction = SendChatAction.builder()
                .chatId(chatId.toString())
                .action(ActionType.TYPING.name())
                .build();
        telegramService.sendMessage(chatAction);
    }
}
