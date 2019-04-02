package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.MemberVisibility;

import java.util.function.Consumer;

public final class WatchersWatcherRemovingRunnableTest implements ClassTesting2<WatchersWatcherRemovingRunnable>,
        ToStringTesting<WatchersWatcherRemovingRunnable> {

    @Test
    public void testToStringRemoved() {
        final Watchers<String> watchers = Watchers.create();
        final Runnable remover = watchers.addWatcher(this.watcher());
        remover.run();

        this.toStringAndCheck(remover, "Watcher123 Removed");
    }

    @Test
    public void testToStringActive() {
        final Watchers<String> watchers = Watchers.create();
        this.toStringAndCheck(watchers.addWatcher(this.watcher()), "Watcher123 Active");
    }

    private Consumer<String> watcher() {
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "Watcher123";
            }
        };
    }

    @Override
    public Class<WatchersWatcherRemovingRunnable> type() {
        return WatchersWatcherRemovingRunnable.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
