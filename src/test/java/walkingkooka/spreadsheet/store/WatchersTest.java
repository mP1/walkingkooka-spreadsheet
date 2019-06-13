package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class WatchersTest implements ClassTesting2<Watchers>,
        ToStringTesting<Watchers> {

    private final static String SOURCE1A = "Source1A";
    private final static String SOURCE2B = "Source2B";
    private final static String SOURCE3C = "Source3C";

    @Test
    public void testAddNullWatcherFails() {
        assertThrows(NullPointerException.class, () -> {
            Watchers.create().addWatcher(null);
        });
    }

    @Test
    public void testAddAndFire() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired = Lists.array();
        watchers.addWatcher(this.watcher(fired));

        watchers.accept(SOURCE1A);

        assertEquals(Lists.of(SOURCE1A), fired);
    }

    @Test
    public void testAddAndFire2() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired1 = Lists.array();
        final List<String> fired2 = Lists.array();
        watchers.addWatcher(this.watcher(fired1));
        watchers.addWatcher(this.watcher(fired2));

        watchers.accept(SOURCE1A);

        assertEquals(Lists.of(SOURCE1A), fired1);
        assertEquals(Lists.of(SOURCE1A), fired2);
    }

    @Test
    public void testAddAndFire3() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired1 = Lists.array();
        final List<String> fired2 = Lists.array();
        watchers.addWatcher(this.watcher(fired1));
        watchers.addWatcher(this.watcher(fired2));

        watchers.accept(SOURCE1A);
        watchers.accept(SOURCE2B);

        assertEquals(Lists.of(SOURCE1A, SOURCE2B), fired1);
        assertEquals(Lists.of(SOURCE1A, SOURCE2B), fired2);
    }

    @Test
    public void testAddWatcherMultipleTimes() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired = Lists.array();
        final Consumer<String> watcher = this.watcher(fired);

        watchers.addWatcher(watcher);
        watchers.accept(SOURCE1A);

        watchers.addWatcher(watcher);
        watchers.accept(SOURCE2B);

        assertEquals(Lists.of(SOURCE1A, SOURCE2B, SOURCE2B), fired);
    }

    @Test
    public void testAddAndRemove() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired1 = Lists.array();
        watchers.addWatcher(this.watcher(fired1)).run();

        watchers.accept(SOURCE1A);

        assertEquals(Lists.empty(), fired1);
    }

    @Test
    public void testAddWatcherMultipleTimesRemovedOnce() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired = Lists.array();
        final Consumer<String> watcher = this.watcher(fired);
        final Runnable remover = watchers.addWatcher(watcher);

        watchers.accept(SOURCE1A);

        watchers.addWatcher(watcher);
        watchers.accept(SOURCE2B);

        remover.run();

        watchers.accept(SOURCE3C);

        assertEquals(Lists.of(SOURCE1A, SOURCE2B, SOURCE2B, SOURCE3C), fired);
    }

    @Test
    public void testAddAndRemoveMultipleTimes() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired1 = Lists.array();
        watchers.addWatcher(this.watcher(fired1)).run();

        watchers.accept(SOURCE1A);
        watchers.accept(SOURCE1A);
        watchers.accept(SOURCE1A);

        assertEquals(Lists.empty(), fired1);
    }

    @Test
    public void testAddFireAndRemove() {
        final Watchers<String> watchers = Watchers.create();

        final List<String> fired1 = Lists.array();
        final List<String> fired2 = Lists.array();
        final Runnable remover1 = watchers.addWatcher(this.watcher(fired1));
        final Runnable remover2 = watchers.addWatcher(this.watcher(fired2));

        watchers.accept(SOURCE1A);

        remover1.run();
        remover1.run();

        watchers.accept(SOURCE2B);

        remover2.run();

        watchers.accept(SOURCE3C);

        assertEquals(Lists.of(SOURCE1A), fired1);
        assertEquals(Lists.of(SOURCE1A, SOURCE2B), fired2);
    }

    private Consumer<String> watcher(final List<String> fired) {
        return (s) -> fired.add(s);
    }

    @Test
    public void testToString() {
        final Watchers<String> watchers = Watchers.create();
        watchers.addWatcher(this.watcher("watcher1"));
        watchers.addWatcher(this.watcher("watcher2"));

        this.toStringAndCheck(watchers, "[watcher1, watcher2]");
    }

    @Test
    public void testToString2() {
        final Watchers<String> watchers = Watchers.create();
        watchers.addWatcher(this.watcher("watcher1"));
        watchers.addWatcher(this.watcher("watcher2")).run();

        this.toStringAndCheck(watchers, "[watcher1]");
    }

    private Consumer<String> watcher(final String toString) {
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                throw new UnsupportedOperationException();
            }

            public String toString() {
                return toString;
            }
        };
    }


    @Override
    public Class<Watchers> type() {
        return Watchers.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
