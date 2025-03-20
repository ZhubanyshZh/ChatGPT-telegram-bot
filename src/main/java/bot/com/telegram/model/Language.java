package bot.com.telegram.model;

import java.util.Optional;

public enum Language {
    KZ, RU, EN;

    public static Optional<Language> getByName(String lan) {
        for (Language language : Language.values()) {
            if (language.name().equals(lan)) {
                return Optional.of(language);
            }
        }
        return Optional.empty();
    }
}
