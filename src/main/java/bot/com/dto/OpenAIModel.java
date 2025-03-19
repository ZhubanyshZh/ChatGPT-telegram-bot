package bot.com.dto;

public enum OpenAIModel {

    GPTFOUR("gpt-4");

    private final String modelName;

    OpenAIModel(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}

