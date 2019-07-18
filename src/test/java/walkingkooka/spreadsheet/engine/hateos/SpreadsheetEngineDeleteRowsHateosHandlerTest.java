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
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineDeleteRowsHateosHandlerTest extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2<SpreadsheetEngineDeleteRowsHateosHandler,
        SpreadsheetRowReference> {

    @Test
    public void testDeleteRow() {
        final Optional<SpreadsheetRowReference> row = this.id();
        final Optional<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> resource = this.resource();

        final Set<SpreadsheetCell> cells = Sets.of(this.cell());

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta<Range<SpreadsheetRowReference>> deleteRows(final SpreadsheetRowReference r,
                                                                                       final int count,
                                                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row.get(), r, "row");
                        assertEquals(1, count, "count");
                        return SpreadsheetDelta.withRange(Range.singleton(row.get()), cells);
                    }
                }),
                row,
                resource,
                HateosHandler.NO_PARAMETERS,
                Optional.of(SpreadsheetDelta.withId(row, cells)));
    }

    @Test
    public void testDeleteSeveralRows() {
        final Optional<SpreadsheetDelta<Range<SpreadsheetRowReference>>> resource = this.collectionResource();

        final Range<SpreadsheetRowReference> range = SpreadsheetRowReference.parseRange("2:4");
        final Set<SpreadsheetCell> cells = this.cells();

        final SpreadsheetDelta<Range<SpreadsheetRowReference>> delta = SpreadsheetDelta.withRange(range, cells);

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta<Range<SpreadsheetRowReference>> deleteRows(final SpreadsheetRowReference r,
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
    public void testDeleteRowFiltered() {
        final Optional<SpreadsheetRowReference> row = this.id();

        final Set<SpreadsheetCell> cells = this.cells();
        final List<SpreadsheetRange> window = this.window();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta<Range<SpreadsheetRowReference>> deleteRows(final SpreadsheetRowReference c,
                                                                                             final int count,
                                                                                             final SpreadsheetEngineContext context) {
                        assertEquals(row.get(), c, "row");
                        assertEquals(1, count, "count");
                        return SpreadsheetDelta.withRange(Range.singleton(row.get()), cells);
                    }
                }),
                row,
                Optional.of(SpreadsheetDelta.withId(row, SpreadsheetDelta.NO_CELLS).setWindow(window)),
                HateosHandler.NO_PARAMETERS,
                Optional.of(SpreadsheetDelta.withId(row, this.cellsWithinWindow()).setWindow(window)));
    }

    @Test
    public void testDeleteAllRowsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetRowReference.parse("2")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
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
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    private SpreadsheetEngineDeleteRowsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine, this.engineContext());
    }

    @Override
    SpreadsheetEngineDeleteRowsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                           final SpreadsheetEngineContext context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
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
    public Class<SpreadsheetEngineDeleteRowsHateosHandler> type() {
        return SpreadsheetEngineDeleteRowsHateosHandler.class;
    }
}
