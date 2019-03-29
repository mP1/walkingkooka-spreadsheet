package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StoreTesting<S extends Store<K, V>, K, V> extends ClassTesting2<S> {

    @Test
    default void testLoadNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().load(null);
        });
    }

    @Test
    default void testSaveNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().save(null);
        });
    }

    @Test
    default void testDeleteNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().delete(null);
        });
    }

    @Test
    default void testIdsInvalidFromFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().ids(-1, 0);
        });
    }

    @Test
    default void testIdsInvalidCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().ids(0, -1);
        });
    }

    @Test
    default void testIdsFrom0AndCountZero() {
        this.idsAndCheck(this.createStore(), 0, 0);
    }

    @Test
    default void testValuesNullFromIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().values(null, 0);
        });
    }

    @Test
    default void testValuesInvalidCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().values(this.id(), -1);
        });
    }

    @Test
    default void testValueFromAndZeroCount() {
        this.valuesAndCheck(this.createStore(), this.id(), 0);
    }

    @Test
    default void testFirstIdWhenEmpty() {
        assertEquals(Optional.empty(),
                this.createStore().firstId());
    }

    @Test
    default void testFirstValueWhenEmpty() {
        assertEquals(Optional.empty(),
                this.createStore().firstValue());
    }

    @Test
    default void testAllWhenEmpty() {
        assertEquals(Lists.empty(),
                this.createStore().all());
    }

    S createStore();

    K id();

    default void loadAndCheck(final S store, final K id, final V value) {
        assertEquals(Optional.of(value),
                store.load(id),
                () -> " store load " + id);
    }

    default void loadFailCheck(final K id) {
        this.loadFailCheck(this.createStore(), id);
    }

    default void loadFailCheck(final S store, final K id) {
        final Optional<V> value = store.load(id);
        assertEquals(Optional.empty(), value, () -> "Expected id " + id + " to fail");
    }

    default void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count, store.count(), () -> "Wrong count " + store);
    }

    default void idsAndCheck(final S store,
                             final int from,
                             final int to,
                             final K... ids) {
        this.idsAndCheck(store, from, to, Sets.of(ids));
    }

    default void idsAndCheck(final S store,
                             final int from,
                             final int to,
                             final Set<K> ids) {
        assertEquals(ids,
                store.ids(from, to),
                "ids from " + from + " count=" + to);
    }

    default void valuesAndCheck(final S store,
                                final K from,
                                final int count,
                                final V... values) {
        this.valuesAndCheck(store, from, count, Lists.of(values));
    }

    default void valuesAndCheck(final S store,
                                final K from,
                                final int count,
                                final List<V> values) {
        assertEquals(values,
                store.values(from, count),
                "values from " + from + " count=" + count);
    }

    @Override
    default MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
