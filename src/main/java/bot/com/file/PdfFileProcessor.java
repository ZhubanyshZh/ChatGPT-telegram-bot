package bot.com.file;

import bot.com.file.FileProcessor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class PdfFileProcessor implements FileProcessor {
    @Override
    public boolean supports(String mimeType) {
        return "application/pdf".equals(mimeType);
    }

    @Override
    public String process(byte[] fileBytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке PDF", e);
        }
    }
}
