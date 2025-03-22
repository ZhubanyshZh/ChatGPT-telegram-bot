package bot.com.file;

import bot.com.client.TelegramFeignClient;
import bot.com.dto.TelegramFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final TelegramFeignClient telegramFeignClient;
    private final RestTemplate restTemplate;
    @Value("${bot.token}")
    private String botToken;
    public byte[] downloadFile(String fileId) {
        try {
            String filePath = getFilePath(fileId);
            if (filePath == null) {
                throw new RuntimeException("Не удалось получить путь к файлу");
            }
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
            log.info("Скачиваем файл по URL: {}", fileUrl);
            return restTemplate.getForObject(fileUrl, byte[].class);
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
}
