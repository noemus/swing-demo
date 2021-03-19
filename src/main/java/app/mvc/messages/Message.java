package app.mvc.messages;

public final class Message {
    private final Type type;
    private final String text;

    public Message(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Type type() {
        return type;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return type.name() + ": " + text;
    }

    public static Message info(String message) {
        return new Message(Type.INFO, message);
    }

    public static Message warning(String message) {
        return new Message(Type.WARNING, message);
    }

    public static Message error(String message) {
        return new Message(Type.ERROR, message);
    }

    public enum Type {
        ERROR,
        WARNING,
        INFO,
    }
}
