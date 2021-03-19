package app.mvc.messages;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageModel {
    private Optional<Message> message = Optional.empty();

    private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();

    public void setMessage(Message message) {
        this.message = Optional.of(message);
        fireMessageChanged();
    }

    public void clear() {
        message = Optional.empty();
        fireMessageCleared();
    }

    public void addMessageListener(MessageListener listener) {
        listeners.add(0, listener);
    }

    public void removeMessageListener(MessageListener listener) {
        listeners.remove(listener);
    }

    private void fireMessageChanged() {
        message.ifPresent(this::fireMessageChanged);
    }

    private void fireMessageChanged(Message message) {
        listeners.forEach(listener -> listener.messageChanged(message));
    }

    private void fireMessageCleared() {
        listeners.forEach(MessageListener::messageCleared);
    }

    public interface MessageListener {
        void messageChanged(Message message);
        void messageCleared();
    }
}
