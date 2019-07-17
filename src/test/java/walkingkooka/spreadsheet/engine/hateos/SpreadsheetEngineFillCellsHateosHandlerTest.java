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
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineFillCellsHateosHandlerTest extends walkingkooka.spreadsheet.engine.hateos.SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineFillCellsHateosHandler,
        SpreadsheetCellReference> {

    @Test
    public void testHandleUnsupported() {
        this.handleUnsupported(this.createHandler());
    }

    @Test
    public void testFillFromParameterMissing() {
        this.handleCollectionAndCheck2(this.parameters(), this.toSpreadsheetRange());
    }

    @Test
    public void testFillFromParameterEmptyFails() {
        this.handleCollectionFails(this.toSpreadsheetRange().range(),
                this.collectionResource(),
                Maps.of(SpreadsheetEngineFillCellsHateosHandler.FROM, Lists.empty()),
                IllegalArgumentException.class);
    }

    @Test
    public void testFillFromParameterInvalidFails() {
        this.handleCollectionFails(this.toSpreadsheetRange().range(),
                this.collectionResource(),
                Maps.of(SpreadsheetEngineFillCellsHateosHandler.FROM, Lists.of("!INVALID")),
                IllegalArgumentException.class);
    }

    @Test
    public void testFillFromParameterPresent() {
        this.handleCollectionAndCheck2(Maps.of(SpreadsheetEngineFillCellsHateosHandler.FROM, Lists.of(TO)), this.toSpreadsheetRange());
    }

    @Test
    public void testFillFromParameterPresent2() {
        this.handleCollectionAndCheck2(Maps.of(SpreadsheetEngineFillCellsHateosHandler.FROM, Lists.of(TO, FROM)), this.toSpreadsheetRange());
    }

    private void handleCollectionAndCheck2(Map<HttpRequestAttribute<?>, Object> parameters,
                                           final SpreadsheetRange from) {
        this.handleCollectionAndCheck(
                SpreadsheetEngineFillCellsHateosHandler.with(new FakeSpreadsheetEngine() {

                                                                 @Override
                                                                 public SpreadsheetDelta<Range<SpreadsheetCellReference>> fillCells(final Collection<SpreadsheetCell> cells,
                                                                                                                                    final SpreadsheetRange f,
                                                                                                                                    final SpreadsheetRange t,
                                                                                                                                    final SpreadsheetEngineContext context) {
                                                                     assertEquals(collectionResource().get().cells(), cells, "cells");
                                                                     assertEquals(from, f, "from");
                                                                     assertEquals(toSpreadsheetRange(), t, "to");
                                                                     return filled();
                                                                 }
                                                             },
                        this.engineContext()),
                this.collection(),
                this.collectionResource(),
                parameters,
                Optional.of(this.filled()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), SpreadsheetEngine.class.getSimpleName() + ".fillCells");
    }

    @Override
    SpreadsheetEngineFillCellsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                          final SpreadsheetEngineContext context) {
        return SpreadsheetEngineFillCellsHateosHandler.with(engine, context);
    }

    @Override
    SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    @Override
    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return SpreadsheetCellReference.parseCellReferenceRange(TO); // url has TO
    }

    private SpreadsheetRange toSpreadsheetRange() {
        return SpreadsheetExpressionReference.parseRange(TO);
    }

    private final static String TO = "B1:C2";

    private SpreadsheetRange fromSpreadsheetRange() {
        return SpreadsheetExpressionReference.parseRange(FROM);
    }

    private final static String FROM = "E1:F2";


    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> resource() {
        return Optional.empty();
    }

    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> collectionResource() {
        final SpreadsheetCell cell = this.cell();
        return Optional.of(SpreadsheetDelta.withRange(collection(), Sets.of(cell)));
    }

    private SpreadsheetDelta<Range<SpreadsheetCellReference>> filled() {
        return SpreadsheetDelta.withRange(this.collection(), Sets.of(this.cell()));
    }

    @Override
    public Class<SpreadsheetEngineFillCellsHateosHandler> type() {
        return SpreadsheetEngineFillCellsHateosHandler.class;
    }
}
