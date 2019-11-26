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

package walkingkooka.spreadsheet.engine.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.math.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineInsertRowsHateosHandlerTest extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2<SpreadsheetEngineInsertRowsHateosHandler,
        SpreadsheetRowReference> {

    @Test
    public void testInsertRow() {
        final Optional<SpreadsheetRowReference> row = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        final Set<SpreadsheetCell> cells = this.cells();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    @SuppressWarnings("OptionalGetWithoutIsPresent")
                    public SpreadsheetDelta insertRows(final SpreadsheetRowReference r,
                                                       final int count,
                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row.get(), r, "row");
                        assertEquals(1, count, "count");
                        return SpreadsheetDelta.with(cells);
                    }
                }),
                row,
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(SpreadsheetDelta.with(cells)));
    }

    @Test
    public void testInsertSeveralRows() {
        final Optional<SpreadsheetDelta> resource = this.collectionResource();

        final Range<SpreadsheetRowReference> range = SpreadsheetColumnOrRowReference.parseRowRange("2:4");
        final Set<SpreadsheetCell> cells = this.cells();

        final SpreadsheetDelta delta = SpreadsheetDelta.with(cells);

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta insertRows(final SpreadsheetRowReference r,
                                                       final int count,
                                                       final SpreadsheetEngineContext context) {
                        assertEquals(SpreadsheetColumnOrRowReference.parseRow("2"), r, "row");
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
    public void testInsertRowFiltered() {
        final Optional<SpreadsheetRowReference> row = this.id();

        final Set<SpreadsheetCell> cells = this.cells();
        final List<SpreadsheetRange> window = this.window();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    @SuppressWarnings("OptionalGetWithoutIsPresent")
                    public SpreadsheetDelta insertRows(final SpreadsheetRowReference r,
                                                       final int count,
                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row.get(), r, "row");
                        assertEquals(1, count, "count");
                        return SpreadsheetDelta.with(cells);
                    }
                }),
                row,
                Optional.of(SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS).setWindow(window)),
                HateosHandler.NO_PARAMETERS,
                Optional.of(SpreadsheetDelta.with(this.cellsWithinWindow()).setWindow(window)));
    }

    @Test
    public void testInsertAllRowsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testInsertOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetColumnOrRowReference.parseRow("2")));
    }

    @Test
    public void testInsertOpenRangeEndFails() {
        this.handleCollectionFails2(Range.greaterThanEquals(SpreadsheetColumnOrRowReference.parseRow("3")));
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
        return Optional.of(SpreadsheetColumnOrRowReference.parseRow("2"));
    }

    @Override
    public Range<SpreadsheetRowReference> collection() {
        return SpreadsheetColumnOrRowReference.parseRowRange("2:4");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return Optional.empty();
    }

    @Override
    public Optional<SpreadsheetDelta> collectionResource() {
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

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosHandler> type() {
        return SpreadsheetEngineInsertRowsHateosHandler.class;
    }
}
