package bot.com.telegram.service;

import bot.com.client.TelegramFeignClient;
import bot.com.dto.Message;
import bot.com.dto.TelegramFileResponse;
import bot.com.telegram.model.UserChatHistory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class TelegramDocumentService {
    private final TelegramBotService telegramBotService;
    private final TelegramService telegramService;
    private final TelegramFeignClient telegramFeignClient;
    private final String botToken;

    public TelegramDocumentService(TelegramBotService telegramBotService,
                                   TelegramService telegramService,
                                   @Value("${bot.token}") String botToken,
                                   TelegramFeignClient telegramFeignClient) {
        this.telegramBotService = telegramBotService;
        this.telegramService = telegramService;
        this.botToken = botToken;
        this.telegramFeignClient = telegramFeignClient;
    }

    public void handleDocumentMessage(Update update) {
        try {
            String chatId = update.getMessage().getChatId().toString();
            String fileId = update.getMessage().getDocument().getFileId();
            String fileName = update.getMessage().getDocument().getFileName();
            String mimeType = update.getMessage().getDocument().getMimeType();

            log.info("Получен файл: {} с MIME-типом: {}", fileName, mimeType);

            byte[] fileBytes = downloadFile(fileId);
            if (fileBytes == null || fileBytes.length == 0) {
                sendTextMessage(chatId, "Ошибка при загрузке файла.");
                return;
            }

            String fileContent = processFile(fileBytes, mimeType, fileName, chatId);
            if (fileContent == null) return;

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

    private String processFile(byte[] fileBytes, String mimeType, String fileName, String chatId) {
        if (mimeType.startsWith("text/") || mimeType.equals("application/json")) {
            return new String(fileBytes, StandardCharsets.UTF_8);
        } else if (mimeType.equals("application/pdf")) {
            return extractTextFromPdf(fileBytes);
        } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return extractTextFromDocx(fileBytes);
        } else if (mimeType.startsWith("image/")) {
            sendTextMessage(chatId, "Изображение загружено: " + fileName);
            return null;
        } else {
            sendTextMessage(chatId, "Формат файла не поддерживается: " + mimeType);
            return null;
        }
    }

    private byte[] downloadFile(String fileId) {
        try {
            String filePath = getFilePath(fileId);
            if (filePath == null) {
                throw new RuntimeException("Не удалось получить путь к файлу");
            }
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
            log.info("Скачиваем файл по URL: {}", fileUrl);
            return new org.springframework.web.client.RestTemplate().getForObject(fileUrl, byte[].class);
        } catch (Exception e) {
            log.error("Ошибка при скачивании файла: ", e);
            return null;
        }
    }

    private String getFilePath(String fileId) {
        try {
            TelegramFileResponse response = telegramFeignClient.getFile(botToken, fileId);
            log.info(response.toString());
            return response != null && response.getResult() != null ? response.getResult().getFilePath() : null;
        } catch (Exception e) {
            log.error("Ошибка при получении пути к файлу: ", e);
            return null;
        }
    }

    private String extractTextFromPdf(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            log.error("Ошибка при обработке PDF: ", e);
            return null;
        }
    }

    private String extractTextFromDocx(byte[] docxBytes) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docxBytes))) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(paragraph -> text.append(paragraph.getText()).append("\n"));
            return text.toString();
        } catch (IOException e) {
            log.error("Ошибка при обработке DOCX: ", e);
            return null;
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
