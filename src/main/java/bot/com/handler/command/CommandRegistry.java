package bot.com.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandRegistry {

    private final List<CommandHandler> commandHandlers;

    public CommandHandler findHandler(String message) {
        return commandHandlers.stream()
                .filter(handler -> handler.canHandle(message))
                .findFirst()
                .orElseGet(() -> null);
    }
}
