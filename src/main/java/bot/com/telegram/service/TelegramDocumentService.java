package bot.com.telegram.service;

import bot.com.client.TelegramFeignClient;
import bot.com.file.FileProcessingService;
import bot.com.file.FileService;
import bot.com.telegram.model.UserChatHistory;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TelegramDocumentService {
    private final TelegramBotService telegramBotService;
    private final TelegramService telegramService;
    private final FileProcessingService fileProcessingService;
    private final FileService fileService;

    public TelegramDocumentService(TelegramBotService telegramBotService,
                                   TelegramService telegramService,
                                   FileProcessingService fileProcessingService,
                                   FileService fileService) {
        this.telegramBotService = telegramBotService;
        this.telegramService = telegramService;
        this.fileProcessingService = fileProcessingService;
        this.fileService = fileService;
    }

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
            UserChatHistory history = telegramBotService.getUserChatHistory(chatId);
            telegramBotService.addUserMessageToHistory(history, "User uploaded: " + fileName + "\n" + fileContent);
            String responseMessage = telegramBotService.generateResponse(history);
            telegramBotService.saveAndSendResponse(history, responseMessage);

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

