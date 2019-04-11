package walkingkooka.spreadsheet.store;

import java.util.Arrays;
import java.util.Objects;

/**
 * Executes all {@link Runnable} throwing a {@link Throwable} with all additional failures as supressed exceptions.
 */
final class RemoveWatchers {

    static void executeOrFail(final Runnable... removers) {
        Objects.requireNonNull(removers, "removers");

        final RuntimeException failure = new RemoveWatchers(removers).fail;
        if (null != failure) {
            throw failure;
        }
    }

    private RemoveWatchers(final Runnable... removers) {
        super();

        this.fail = null;

        Arrays.stream(removers)
                .forEach(this::tryRemove);
    }

    private void tryRemove(final Runnable runnable) {
        if (null != runnable) {
            try {
                runnable.run();
            } catch (final RuntimeException cause) {
                final Throwable fail = this.fail;
                if (null == fail) {
                    this.fail = cause;
                } else {
                    fail.addSuppressed(cause);
                }
            }
        }
    }

    RuntimeException fail;

    @Override
    public String toString() {
        return fail.toString();
    }
}
