package app.mvc.messages;

import app.mvc.messages.MessageModel.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static app.SpringGuiRunner.runInGui;

@Service
public class PanelsMessageController implements MessageController {
    @Autowired
    private MessageModel messageModel;

    @Autowired
    private MessageUI messageUI;

    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void messageChanged(Message message) {
            runInGui(() -> messageUI.showMessage(message));
        }

        @Override
        public void messageCleared() {
            runInGui(messageUI::clearMessage);
        }
    };


    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Optional<Future<?>> messageTimer = Optional.empty();

    @PostConstruct
    public void setupListener() {
        messageModel.addMessageListener(messageListener);
    }

    @PreDestroy
    public void dispose() {
        messageModel.removeMessageListener(messageListener);
        executorService.shutdown();
    }

    @Override
    public void showMessage(String message) {
        messageTimer.ifPresent(task -> task.cancel(false));

        messageModel.setMessage(Message.info(message));
        messageTimer = Optional.of(executorService.schedule(this::clearMessage, 5, TimeUnit.SECONDS));
    }

    private void clearMessage() {
        messageTimer = Optional.empty();
        messageModel.clear();
    }

    @Override
    public void showWarning(String message) {
        messageModel.setMessage(Message.warning(message));
    }

    @Override
    public void showError(String message) {
        messageModel.setMessage(Message.error(message));
    }
}
