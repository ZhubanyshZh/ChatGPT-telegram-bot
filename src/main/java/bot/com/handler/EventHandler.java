package bot.com.handler;

public interface EventHandler<T> {

    void handle(T t);

    boolean isApplicable(T t);

}
