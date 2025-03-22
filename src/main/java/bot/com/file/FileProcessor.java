package bot.com.file;

public interface FileProcessor {
    boolean supports(String mimeType);
    String process(byte[] fileBytes);
}
