package bot.com.telegram.service;

import bot.com.client.OpenAIClient;
import bot.com.dto.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBotService {

    @Value("${chat-gpt.token}")
    private String openAiToken;
    @Value("${messages.welcome.ru}")
    private String welcomeMessageRu;
    @Value("${messages.welcome.kz}")
    private String welcomeMessageKz;
    @Value("${messages.welcome.en}")
    private String welcomeMessageEn;

    private TelegramClient telegramClient;
    private OpenAIClient openAIClient;

    public TelegramBotService(@Value("${bot.token}") String botToken, OpenAIClient openAIClient) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.openAIClient = openAIClient;
    }

    @SneakyThrows
    public void sendMessage(Update update) {
        if(!isFirstMessage(update)) {
            var header = OpenAIRequestHeader.builder()
                    .authorization("Bearer " + openAiToken)
                    .contentType("application/json")
                    .build();
            var completionDto = CompletionBodyDto.builder()
                    .model(OpenAIModel.GPTFOUR.getModelName())
                    .messages(List.of(Message.builder()
                                    .role("user")
                                    .content(update.getMessage().getText())
                                    .build()))
                    .build();
            log.info("Sending message to OpenAI: {}", update.getMessage().getText());
            OpenAIResponse response = openAIClient.chat(header, completionDto);
            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text(response.getChoices().getFirst().getMessage().getContent())
                    .build();

            telegramClient.execute(message);
            log.info("Received message from OpenAI: {}", response.getChoices().getFirst().getMessage().getContent());
        }
    }

    private SendMessage createReplyKeyboard(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(welcomeMessageRu)
                .build();

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .build();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Привет");
        row1.add("Как дела?");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Помощь");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    @SneakyThrows
    private boolean isFirstMessage(Update update) {
        if("/start".equals(update.getMessage().getText())) {
            SendMessage message = createReplyKeyboard(update.getMessage().getChatId());
            telegramClient.execute(message);
            return true;
        }
        return false;
    }
}
