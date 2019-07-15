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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineCopyCellsHateosHandlerTest extends walkingkooka.spreadsheet.engine.hateos.SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineCopyCellsHateosHandler,
        SpreadsheetCellReference> {

    @Test
    public void testHandleUnsupported() {
        this.handleUnsupported(this.createHandler());
    }

    @Test
    public void testCopy() {
        this.handleCollectionAndCheck(this.collection(),
                this.collectionResource(),
                this.parameters(),
                Optional.of(this.copied()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), SpreadsheetEngine.class.getSimpleName() + ".copyCells");
    }

    @Override
    SpreadsheetEngineCopyCellsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                          final SpreadsheetEngineContext context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {

            @Override
            public SpreadsheetDelta<Range<SpreadsheetCellReference>> copyCells(final Collection<SpreadsheetCell> from,
                                                                               final SpreadsheetRange to,
                                                                               final SpreadsheetEngineContext context) {
                assertEquals(collectionResource().get().cells(), from, "from");
                assertEquals(SpreadsheetExpressionReference.parseRange(TO), to, "to");
                return copied();
            }
        };
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.of(SpreadsheetEngineCopyCellsHateosHandler.TO, Lists.of(TO));
    }

    private final static String TO = "E1:F2";

    @Override
    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return SpreadsheetCellReference.parseCellReferenceRange("C1:D2");
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> resource() {
        return Optional.empty();
    }

    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> collectionResource() {
        final SpreadsheetCell cell = this.cell();
        return Optional.of(SpreadsheetDelta.withRange(collection(), Sets.of(cell)));
    }

    private SpreadsheetDelta<Range<SpreadsheetCellReference>> copied() {
        return SpreadsheetDelta.withRange(this.collection(), Sets.of(this.cell()));
    }

    @Override
    public Class<SpreadsheetEngineCopyCellsHateosHandler> type() {
        return SpreadsheetEngineCopyCellsHateosHandler.class;
    }
}
