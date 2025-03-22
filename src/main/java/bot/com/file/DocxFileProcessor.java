package bot.com.file;

import bot.com.file.FileProcessor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class DocxFileProcessor implements FileProcessor {
    @Override
    public boolean supports(String mimeType) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType);
    }

    @Override
    public String process(byte[] fileBytes) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileBytes))) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке DOCX", e);
        }
    }
}
