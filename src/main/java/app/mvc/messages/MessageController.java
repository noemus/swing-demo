package app.mvc.messages;

public interface MessageController {
    void showMessage(String message);
    void showError(String message);
    void showWarning(String message);
}
