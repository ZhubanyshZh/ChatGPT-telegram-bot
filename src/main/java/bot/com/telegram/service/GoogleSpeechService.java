package bot.com.telegram.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class GoogleSpeechService {

    private final SpeechClient speechClient;

    public GoogleSpeechService(@Value("${google.credentials.path}") String credentialsPath) throws IOException {
        InputStream credentialsStream = new ClassPathResource(credentialsPath).getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        this.speechClient = SpeechClient.create(SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build());
    }

    public String recognizeAudio(Path wavFilePath, String languageCode) {
        try {
            byte[] audioBytes = Files.readAllBytes(wavFilePath);
            ByteString audioData = ByteString.copyFrom(audioBytes);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode(languageCode)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioData).build();
            RecognizeResponse response = speechClient.recognize(config, audio);

            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcript.append(result.getAlternatives(0).getTranscript()).append(" ");
            }

            return transcript.toString().trim();
        } catch (Exception e) {
            log.error("Ошибка при распознавании речи: ", e);
            return "Ошибка при распознавании аудио.";
        }
    }
}
