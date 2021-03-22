package app.mvc.messages;

public record Message(Type type, String text) {
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
