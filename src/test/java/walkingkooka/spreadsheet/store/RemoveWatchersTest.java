package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RemoveWatchersTest implements ClassTesting2<RemoveWatchers> {

    @BeforeEach
    public void beforeEachTest() {
        this.counter = 0;
    }

    @Test
    public void testNullFails() {
        assertThrows(NullPointerException.class, () -> {
            RemoveWatchers.executeOrFail((Runnable[]) null);
        });
    }

    @Test
    public void testAllSuccessful() {
        RemoveWatchers.executeOrFail(() -> this.counter++, () -> this.counter++);

        this.check(2);
    }

    @Test
    public void testSkipsNulls() {
        RemoveWatchers.executeOrFail(null, null, () -> this.counter++);

        this.check(1);
    }

    @Test
    public void testFirstFailureContinuesWithOutstanding() {
        final RuntimeException thrown = new RuntimeException("first");

        assertSame(thrown, assertThrows(RuntimeException.class, () -> {

            RemoveWatchers.executeOrFail(
                    () -> {
                        throw thrown;
                    },
                    () -> this.counter++);
        }));

        this.check(1);
    }

    @Test
    public void testMultipleFailures() {
        final RuntimeException first = new RuntimeException("first");
        final RuntimeException second = new RuntimeException("second");

        assertSame(first, assertThrows(RuntimeException.class, () -> {

            RemoveWatchers.executeOrFail(
                    () -> {
                        throw first;
                    },
                    () -> {
                        throw second;
                    },
                    () -> this.counter++);
        }));

        assertArrayEquals(new Throwable[]{second}, first.getSuppressed());
        this.check(1);
    }

    @Test
    public void testMultipleFailures2() {
        final RuntimeException first = new RuntimeException("first");
        final RuntimeException second = new RuntimeException("second");
        final RuntimeException third = new RuntimeException("third");

        assertSame(first, assertThrows(RuntimeException.class, () -> {

            RemoveWatchers.executeOrFail(
                    () -> {
                        throw first;
                    },
                    () -> {
                        throw second;
                    },
                    null,
                    () -> {
                        throw third;
                    },
                    () -> this.counter++);
        }));

        assertArrayEquals(new Throwable[]{second, third}, first.getSuppressed());
        this.check(1);
    }

    private void check(final int expected) {
        assertEquals(expected, counter, "watchers removed");
    }

    private int counter;

    @Override
    public Class<RemoveWatchers> type() {
        return RemoveWatchers.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
