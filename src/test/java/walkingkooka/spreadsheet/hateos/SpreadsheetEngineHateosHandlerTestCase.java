package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineHateosHandlerTestCase<H extends HateosHandler<I, JsonNode>, I extends Comparable<I>>
        extends SpreadsheetHateosHandlerTestCase2<H, I> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), null, this.engineContextSupplier());
        });
    }

    @Test
    public void testWithNullEngineContextSupplierFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), this.contentType(), null);
        });
    }

    @Override
    final H createHandler(final HateosContentType<JsonNode, HasJsonNode> contentType) {
        return this.createHandler(this.engine(), contentType, this.engineContextSupplier());
    }

    abstract H createHandler(final SpreadsheetEngine engine,
                             final HateosContentType<JsonNode, HasJsonNode> contentType,
                             final Supplier<SpreadsheetEngineContext> context);

    abstract SpreadsheetEngine engine();

    private Supplier<SpreadsheetEngineContext> engineContextSupplier() {
        return this::engineContext;
    }

    abstract SpreadsheetEngineContext engineContext();
}
