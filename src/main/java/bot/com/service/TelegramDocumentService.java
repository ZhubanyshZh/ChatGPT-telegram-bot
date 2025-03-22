package bot.com.service;

import bot.com.file.FileProcessingService;
import bot.com.file.FileService;
import bot.com.model.UserChatHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramDocumentService {

    private final TelegramService telegramService;
    private final FileProcessingService fileProcessingService;
    private final FileService fileService;
    private final UserChatHistoryService userChatHistoryService;
    private final AIService aiService;

    public void handleDocumentMessage(Update update) {
        try {
            String chatId = update.getMessage().getChatId().toString();
            String fileId = update.getMessage().getDocument().getFileId();
            String fileName = update.getMessage().getDocument().getFileName();
            String mimeType = update.getMessage().getDocument().getMimeType();

            log.info("Получен файл: {} с MIME-типом: {}", fileName, mimeType);

            byte[] fileBytes = fileService.downloadFile(fileId);
            if (fileBytes == null || fileBytes.length == 0) {
                sendTextMessage(chatId, "Ошибка при загрузке файла.");
                return;
            }

            String fileContent;
            try {
                fileContent = fileProcessingService.processFile(fileBytes, mimeType);
            } catch (UnsupportedOperationException e) {
                sendTextMessage(chatId, e.getMessage());
                return;
            }

            // GPT-обработка файла
            UserChatHistory history = userChatHistoryService.getUserChatHistory(chatId);
            userChatHistoryService.addUserMessageToHistory(history, "User uploaded: " + fileName + "\n" + fileContent);
            String responseMessage = aiService.generateResponse(history);
            userChatHistoryService.saveAndSendResponse(history, responseMessage);

        } catch (Exception e) {
            log.error("Ошибка при обработке документа: ", e);
            sendTextMessage(update.getMessage().getChatId().toString(), "Ошибка при обработке документа.");
        }
    }
    private void sendTextMessage(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        telegramService.sendMessage(message);
    }
}

