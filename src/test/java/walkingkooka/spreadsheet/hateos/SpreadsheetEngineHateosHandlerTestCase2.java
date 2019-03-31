package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineHateosHandlerTestCase2<H extends HateosHandler<I, R, S>,
        I extends Comparable<I>,
        R extends HateosResource<I>,
        S extends HateosResource<?>>
        extends SpreadsheetEngineHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, R, S>{

    SpreadsheetEngineHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null, this.engineContextSupplier());
        });
    }

    @Test
    public final void testWithNullEngineContextSupplierFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), null);
        });
    }

    @Override
    public final H createHandler() {
        return this.createHandler(this.engine(), this.engineContextSupplier());
    }

    abstract H createHandler(final SpreadsheetEngine engine,
                             final Supplier<SpreadsheetEngineContext> context);

    abstract SpreadsheetEngine engine();

    final Supplier<SpreadsheetEngineContext> engineContextSupplier() {
        return this::engineContext;
    }

    abstract SpreadsheetEngineContext engineContext();
}
