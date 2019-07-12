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
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineInsertRowsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertRowsHateosHandler,
        SpreadsheetRowReference> {

    @Test
    public void testInsertRow() {
        final Optional<SpreadsheetRowReference> row = this.id();
        final Optional<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta<Range<SpreadsheetRowReference>> insertRows(final SpreadsheetRowReference r,
                                                                                       final int count,
                                                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row.get(), r, "row");
                        assertEquals(1, count, "count");
                        return delta(Range.singleton(row.get()));
                    }
                }),
                row,
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(SpreadsheetDelta.withId(row, SpreadsheetDelta.NO_CELLS)));
    }

    @Test
    public void testInsertSeveralRows() {
        final Optional<SpreadsheetDelta<Range<SpreadsheetRowReference>>> resource = this.collectionResource();

        final Range<SpreadsheetRowReference> range = SpreadsheetRowReference.parseRange("2:4");
        final SpreadsheetDelta<Range<SpreadsheetRowReference>> delta = delta(range);

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta<Range<SpreadsheetRowReference>> insertRows(final SpreadsheetRowReference r,
                                                                                       final int count,
                                                                                       final SpreadsheetEngineContext context) {
                        assertEquals(SpreadsheetRowReference.parse("2"), r, "row");
                        assertEquals(3, count, "count"); // 2, 3 & 4
                        return delta;
                    }
                }),
                range, // 3 inclusive
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(delta));
    }

    @Test
    public void testInsertAllRowsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testInsertOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetRowReference.parse("2")));
    }

    @Test
    public void testInsertOpenRangeEndFails() {
        this.handleCollectionFails2(Range.greaterThanEquals(SpreadsheetRowReference.parse("3")));
    }

    private void handleCollectionFails2(final Range<SpreadsheetRowReference> rows) {
        assertEquals("Range with both rows required=" + rows,
                this.handleCollectionFails(rows,
                        this.collectionResource(),
                        HateosHandler.NO_PARAMETERS,
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertRows");
    }

    private SpreadsheetEngineInsertRowsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine, this.engineContext());
    }

    @Override
    SpreadsheetEngineInsertRowsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                           final SpreadsheetEngineContext context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
    }

    @Override
    public Optional<SpreadsheetRowReference> id() {
        return Optional.of(SpreadsheetRowReference.parse("2"));
    }

    @Override
    public Range<SpreadsheetRowReference> collection() {
        return SpreadsheetRowReference.parseRange("2:4");
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> resource() {
        return Optional.empty();
    }

    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetRowReference>>> collectionResource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine();
    }

    private SpreadsheetDelta<Range<SpreadsheetRowReference>> delta(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetDelta.withRange(range, SpreadsheetDelta.NO_CELLS);
    }

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosHandler> type() {
        return SpreadsheetEngineInsertRowsHateosHandler.class;
    }
}
