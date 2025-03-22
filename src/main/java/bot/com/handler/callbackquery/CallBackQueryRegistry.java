package bot.com.handler.callbackquery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallBackQueryRegistry {

    private final List<CallBackQueryHandler> callBackQueryHandlers;

    public CallBackQueryHandler findHandler(String message) {
        return callBackQueryHandlers.stream()
                .filter(handler -> handler.canHandle(message))
                .findFirst()
                .orElseGet(() -> null);
    }
}
