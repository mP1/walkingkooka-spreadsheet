package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An abstract {@link HateosHandler} that includes uses a {@link Store}
 */
abstract class SpreadsheetStoreHateosHandler<I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>>
        extends SpreadsheetHateosHandler {

    /**
     * Checks required factory method parameters are not null.
     */
    static void check(final Store<?, ?> store) {
        Objects.requireNonNull(store, "store");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetStoreHateosHandler(final S store) {
        super();
        this.store = store;
    }

    /**
     * Complains if the id is null.
     */
    final void checkId(final Comparable<?> id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * Complains if the value is null.
     */
    final void checkValue(final R value) {
        Objects.requireNonNull(value, "value");
    }

    /**
     * Complains if the value is null.
     */
    final void checkValue(final Optional<R> value) {
        Objects.requireNonNull(value, "value");
    }

    /**
     * Complains if the value is null or present.
     */
    final void checkValueEmpty(final Optional<R> value) {
        this.checkValue(value);
        if (value.isPresent()) {
            throw new IllegalArgumentException("Value not allowed=" + value);
        }
    }

    /**
     * Complains if the value is NOT present.
     */
    final void checkValuePresent(final Optional<R> value) {
        this.checkValue(value);
        if (!value.isPresent()) {
            throw new IllegalArgumentException("Required not present=" + value);
        }
    }

    /**
     * Complains if the ids is null.
     */
    final void checkIds(final Range<?> ids) {
        Objects.requireNonNull(ids, "ids");
    }

    /**
     * Complains if the value is null.
     */
    final void checkValues(final List<R> values) {
        Objects.requireNonNull(values, "values");
    }

    /**
     * Complains if the value is null or present.
     */
    final void checkValuesEmpty(final List<R> values) {
        this.checkValues(values);
        if (values.size() > 0) {
            throw new IllegalArgumentException("Values not allowed=" + values);
        }
    }

    final S store;
}
