package walkingkooka.spreadsheet.store;

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Abstraction that supports collecting and dispatching events. If a watcher is added multiple times it will be fired each time.
 */
public final class Watchers<T> implements Consumer<T> {

    /**
     * Removes all watchers, skipping nulls, and adding additional {@link RuntimeException} to the first and throwing that at the end.
     */
    public static void removeAllThenFail(final Runnable... removers) {
        WatchersRemoveAllThenFail.executeOrFail(removers);
    }

    /**
     * Creates an empty {@link Watchers}
     */
    public static <T> Watchers<T> create() {
        return new Watchers<>();
    }

    private Watchers() {
        super();
    }

    /**
     * Adds a new watcher which may be removed by calling the returned {@link Runnable}.
     */
    public Runnable addWatcher(final Consumer<T> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        this.watchers.add(watcher);
        return WatchersWatcherRemovingRunnable.with(watcher, this.watchers);
    }

    /**
     * Fire an event to all watchers.
     */
    @Override
    public void accept(final T source) {
        Objects.requireNonNull(source, "source");
        this.watchers.forEach(w -> w.accept(source));
    }

    private final List<Consumer<T>> watchers = Lists.copyOnWrite();

    @Override
    public String toString() {
        return this.watchers.toString();
    }
}
