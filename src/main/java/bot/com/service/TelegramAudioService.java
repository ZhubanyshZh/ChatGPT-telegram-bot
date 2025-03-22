package bot.com.service;

import bot.com.file.FileService;
import bot.com.model.UserChatHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAudioService {

    @Value("${audio.language}")
    private String languageCode;

    private final TelegramService telegramService;
    private final GoogleSpeechService googleSpeechService;
    private final FileService fileService;
    private final UserChatHistoryService userChatHistoryService;
    private final AIService aiService;

    public void handleAudioMessage(Update update) {
        try {
            String chatId = update.getMessage().getChatId().toString();
            String fileId = update.getMessage().getVoice().getFileId();

            byte[] fileData = fileService.downloadFile(fileId);
            if (fileData == null) {
                telegramService.sendMessage(SendMessage.builder()
                        .chatId(chatId)
                        .text("Ошибка: не удалось скачать файл.")
                        .build());
                return;
            }
            log.info(fileData.toString());
            Path oggPath = Files.createTempFile("audio", ".ogg");
            Files.write(oggPath, fileData);

            File wavFile = convertOggToWav(oggPath.toFile());

            String recognizedText = googleSpeechService.recognizeAudio(wavFile.toPath(), languageCode);
            UserChatHistory history = userChatHistoryService.getUserChatHistory(chatId);
            userChatHistoryService.addUserMessageToHistory(history, "User uploaded: " + recognizedText + "\n");
            String responseMessage = aiService.generateResponse(history);
            userChatHistoryService.saveAndSendResponse(history, responseMessage);
            Files.deleteIfExists(oggPath);
            wavFile.delete();

        } catch (Exception e) {
            log.error("Ошибка при обработке голосового сообщения: ", e);
        }
    }

    private File convertOggToWav(File oggFile) throws IOException, InterruptedException {
        File wavFile = new File(oggFile.getAbsolutePath().replace(".ogg", ".wav"));
        ProcessBuilder builder = new ProcessBuilder("/opt/homebrew/bin/ffmpeg", "-i", oggFile.getAbsolutePath(),
                "-ac", "1", "-ar", "16000", wavFile.getAbsolutePath());
        builder.start().waitFor();
        return wavFile;
    }
}
