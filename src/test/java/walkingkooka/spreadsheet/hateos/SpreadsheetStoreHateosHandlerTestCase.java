package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetStoreHateosHandlerTestCase<H extends SpreadsheetStoreHateosHandler<I, R, S>,
        I extends Comparable<I>,
        R extends HateosResource<I>,
        S extends Store<?, ?>>
        extends SpreadsheetHateosHandlerTestCase<H> {

    SpreadsheetStoreHateosHandlerTestCase() {
        super();
    }

    @Test
    public final void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null);
        });
    }

    @Test
    public final void testToString() {
        final S store = this.store();
        this.toStringAndCheck(this.createHandler(store), this.toStringExpectation());
    }

    public final H createHandler() {
        return this.createHandler(this.store());
    }

    abstract H createHandler(final S store);

    abstract S store();

    abstract String toStringExpectation();
}
