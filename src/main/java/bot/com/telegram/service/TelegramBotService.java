package bot.com.telegram.service;

import bot.com.client.OpenAIClient;
import bot.com.dto.*;
import bot.com.telegram.handler.command.CommandHandler;
import bot.com.telegram.handler.command.CommandRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBotService {

    @Value("${chat-gpt.token}")
    private String openAiToken;
    @Value("${chat-gpt.model}")
    private String model;

    private TelegramClient telegramClient;
    private OpenAIClient openAIClient;
    private CommandRegistry commandRegistry;

    public TelegramBotService(@Value("${bot.token}") String botToken,
                              OpenAIClient openAIClient,
                              CommandRegistry commandRegistry) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.openAIClient = openAIClient;
        this.commandRegistry = commandRegistry;
    }

    @SneakyThrows
    public void sendMessage(Update update) {
        String messageText = update.getMessage().getText();

        CommandHandler handler = commandRegistry.findHandler(messageText);
        if (handler != null) {
            handler.handle(update, telegramClient);
            return;
        }

        var completionDto = CompletionBodyDto.builder()
                .model(model)
                .messages(List.of(Message.builder()
                        .role("user")
                        .content(update.getMessage().getText())
                        .build()))
                .build();

        log.info("Sending message to DeepInfra: {}", update.getMessage().getText());

        OpenAIResponse response = openAIClient.chat(openAiToken, completionDto);

        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(response.getChoices().getFirst().getMessage().getContent())
                .build();

        telegramClient.execute(message);
        log.info("Received message from DeepInfra: {}", response.getChoices().getFirst().getMessage().getContent());
    }
}
