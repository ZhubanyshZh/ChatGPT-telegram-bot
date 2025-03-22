package bot.com.service;

import bot.com.client.OpenAIClient;
import bot.com.dto.CompletionBodyDto;
import bot.com.dto.Message;
import bot.com.dto.OpenAIResponse;
import bot.com.model.UserChatHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIService {
    private final OpenAIClient openAIClient;
    private final String model;
    private final String openAiToken;

    public AIService(OpenAIClient openAIClient,
                     @Value("${chat-gpt.model}") String model,
                     @Value("${chat-gpt.token}") String openAiToken) {
        this.openAIClient = openAIClient;
        this.model = model;
        this.openAiToken = openAiToken;
    }

    public String getResponse(List<Message> messages) {
        var completionDto = CompletionBodyDto.builder()
                .model(model)
                .messages(messages)
                .build();

        OpenAIResponse response = openAIClient.chat(openAiToken, completionDto);
        return response.getChoices().getFirst().getMessage().getContent()
                .replaceAll("(?s)<think>.*?</think>\\s*", ""); // Удаление тега
    }

    public String generateResponse(UserChatHistory history) {
        List<Message> conversation = history.getMessages().stream()
                .map(entry -> new Message(entry.getRole(), entry.getContent()))
                .toList();
        return getResponse(conversation);
    }
}

