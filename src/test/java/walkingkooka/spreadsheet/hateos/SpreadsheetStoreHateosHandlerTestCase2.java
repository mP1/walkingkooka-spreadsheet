package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.test.ToStringTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetStoreHateosHandlerTestCase2<H extends HateosHandler<I, R, R>,
        I extends Comparable<I>,
        R extends HateosResource<?>,
        S extends Store<?, R>>
        extends SpreadsheetStoreHateosHandlerTestCase<H>
        implements ToStringTesting<H> {

    SpreadsheetStoreHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null);
        });
    }

    public final H createHandler() {
        return this.createHandler(this.store());
    }

    abstract H createHandler(final S store);

    abstract S store();

    @Override
    public String typeNamePrefix() {
        return "SpreadsheetStore";
    }
}
