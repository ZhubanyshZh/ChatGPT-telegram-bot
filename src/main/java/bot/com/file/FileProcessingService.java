package bot.com.file;

import bot.com.file.FileProcessor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FileProcessingService {
    private final List<FileProcessor> processors;

    public FileProcessingService(List<FileProcessor> processors) {
        this.processors = processors;
    }

    public String processFile(byte[] fileBytes, String mimeType) {
        return processors.stream()
                .filter(p -> p.supports(mimeType))
                .findFirst()
                .map(p -> p.process(fileBytes))
                .orElseThrow(() -> new UnsupportedOperationException("Формат не поддерживается: " + mimeType));
    }
}
