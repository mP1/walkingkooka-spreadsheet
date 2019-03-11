package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineInsertColumnsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertColumnsHateosHandler, SpreadsheetColumnReference, SpreadsheetColumn> {

    @Test
    public void testInsertMissingCountParametersFails() {
        this.handleFails(this.id(),
                this.resource(),
                HateosHandler.NO_PARAMETERS,
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertInvalidCountParametersFails() {
        this.handleFails(this.id(),
                this.resource(),
                this.parameters("1", "2"),
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertOneColumn() {
        final Latch inserted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetColumn> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void insertColumns(final SpreadsheetColumnReference r,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
                        assertEquals(column, r, "column");
                        assertEquals(1, count, "count");
                        inserted.set("inserted");
                    }
                }),
                column,
                resource,
                parameters("1"),
                Optional.empty()
        );

        assertTrue(inserted.value());
    }

    @Test
    public void testInsertSeveralColumns() {
        final Latch inserted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetColumn> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void insertColumns(final SpreadsheetColumnReference r,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
                        assertEquals(column, r, "column");
                        assertEquals(3, count, "count");
                        inserted.set("Deleted");
                    }
                }),
                this.id(),
                resource,
                parameters("3"),
                Optional.empty());
        assertTrue(inserted.value());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertColumns");
    }

    @Override
    public Class<SpreadsheetEngineInsertColumnsHateosHandler> type() {
        return Cast.to(SpreadsheetEngineInsertColumnsHateosHandler.class);
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("D");
    }

    @Override
    public Range<SpreadsheetColumnReference> collection() {
        return Range.greaterThanEquals(this.id()).and(Range.lessThanEquals(SpreadsheetColumnReference.parse("G")));
    }

    @Override
    public Optional<SpreadsheetColumn> resource() {
        return Optional.empty();
    }

    @Override
    public List<SpreadsheetColumn> resourceCollection() {
        return Lists.empty();
    }

    private SpreadsheetEngineInsertColumnsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final String... count) {
        return Maps.one(UrlParameterName.with("count"), Lists.of(count));
    }

    @Override
    SpreadsheetEngineInsertColumnsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertColumnsHateosHandler.with(engine, context);
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine();
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }
}
