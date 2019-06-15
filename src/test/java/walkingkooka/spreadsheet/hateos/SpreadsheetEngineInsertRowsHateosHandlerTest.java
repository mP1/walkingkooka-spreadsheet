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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineInsertRowsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertRowsHateosHandler,
        SpreadsheetRowReference,
        SpreadsheetDelta,
        SpreadsheetDelta> {

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
    public void testInsertOneRow() {
        final SpreadsheetRowReference row = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta insertRows(final SpreadsheetRowReference r,
                                                       final int count,
                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(1, count, "count");
                        return delta();
                    }
                }),
                row,
                resource,
                parameters("1"),
                Optional.of(delta())
        );
    }

    @Test
    public void testInsertSeveralRows() {
        final SpreadsheetRowReference row = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta insertRows(final SpreadsheetRowReference r,
                                                       final int count,
                                                       final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(3, count, "count");
                        return delta();
                    }
                }),
                this.id(),
                resource,
                parameters("3"),
                Optional.of(delta()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertRows");
    }

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosHandler> type() {
        return Cast.to(SpreadsheetEngineInsertRowsHateosHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("3");
    }

    @Override
    public Range<SpreadsheetRowReference> collection() {
        return SpreadsheetRowReference.parseRange("2:4");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return this.spreadsheetDeltaWithoutCells();
    }

    private SpreadsheetEngineInsertRowsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final String... count) {
        return Maps.of(UrlParameterName.with("count"), Lists.of(count));
    }

    @Override
    SpreadsheetEngineInsertRowsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                           final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
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
