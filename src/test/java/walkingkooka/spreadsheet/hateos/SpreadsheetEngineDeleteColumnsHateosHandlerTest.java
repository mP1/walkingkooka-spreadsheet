/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineDeleteColumnsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteColumnsHateosHandler,
        SpreadsheetColumnReference,
        SpreadsheetDelta,
        SpreadsheetDelta> {

    @Test
    public void testDeleteColumn() {
        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference c,
                                                          final int count,
                                                          final SpreadsheetEngineContext context) {
                        assertEquals(column, c, "column");
                        assertEquals(1, count, "count");
                        return delta();
                    }
                }),
                column,
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(this.delta()));
    }

    @Test
    public void testDeleteSeveralColumns() {
        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference c,
                                                          final int count,
                                                          final SpreadsheetEngineContext context) {
                        assertEquals(column, c, "column");
                        assertEquals(2, count, "count");
                        return delta();
                    }
                }), Range.greaterThanEquals(column).and(Range.lessThanEquals(SpreadsheetColumnReference.parse("D"))), // 2 inclusive
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(this.delta()));
    }

    @Test
    public void testDeleteAllColumnsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.handleCollectionFails2(Range.greaterThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    private void handleCollectionFails2(final Range<SpreadsheetColumnReference> columns) {
        assertEquals("Range of columns required=" + columns,
                this.handleCollectionFails(columns,
                        this.resource(),
                        HateosHandler.NO_PARAMETERS,
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteColumns");
    }

    @Override
    public Class<SpreadsheetEngineDeleteColumnsHateosHandler> type() {
        return SpreadsheetEngineDeleteColumnsHateosHandler.class;
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("C");
    }

    @Override
    public Range<SpreadsheetColumnReference> collection() {
        return SpreadsheetColumnReference.parseRange("B:D");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return this.spreadsheetDeltaWithoutCells();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private SpreadsheetEngineDeleteColumnsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    SpreadsheetEngineDeleteColumnsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
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
