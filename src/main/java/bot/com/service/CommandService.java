package bot.com.service;

import bot.com.handler.command.CommandHandler;
import bot.com.handler.command.CommandRegistry;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class CommandService {
    private final CommandRegistry commandRegistry;

    public CommandService(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @SneakyThrows
    public boolean processCommand(Update update, String userMessage) {

        CommandHandler handler = commandRegistry.findHandler(userMessage);
        if (handler != null) {
            handler.handle(update);
            return true;
        }
        return false;
    }
}

