package app.suggestions.ui;

import org.assertj.swing.exception.WaitTimedOutError;

@SuppressWarnings("ClassCanBeRecord")
public class Wait {
    static final long DEFAULT_TIMEOUT = 2000L;
    static final long DEFAULT_POLL = 200L;

    private final String message;
    private final long timeout;
    private final long poll;

    private Wait(String message, long timeout, long poll) {
        this.message = message;
        this.timeout = timeout;
        this.poll = poll;
    }

    public static Wait waitFor(String message) {
        return new Wait(message, DEFAULT_TIMEOUT, DEFAULT_POLL);
    }

    public Wait withTimeout(long timeout) {
        return new Wait(message, timeout, poll);
    }

    public Wait withPolling(long poll) {
        return new Wait(message, timeout, poll);
    }

    public void until(Condition condition) {
        int count = (int) (timeout / poll);
        while (--count > 0) {
            if (condition.isTrue()) {
                return;
            }
            try {
                Thread.sleep(poll);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        throw new WaitTimedOutError(message + ": timed out after " + timeout + " ms");
    }

    @FunctionalInterface
    public interface Condition {
        boolean isTrue();
    }
}
