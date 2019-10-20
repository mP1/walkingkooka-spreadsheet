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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.math.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.text.TextNode;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetEngineSaveCellHateosHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineSaveCellHateosHandler,
        SpreadsheetCellReference> {

    // handle...........................................................................................................

    @Test
    public void testHandleSaveCellMissingFails() {
        this.handleFails(this.id(),
                Optional.empty(),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testHandleSaveCell() {
        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                                                                         final SpreadsheetEngineContext context) {
                        Objects.requireNonNull(context, "context");

                        assertEquals(SpreadsheetEngineSaveCellHateosHandlerTest.this.cell(), cell, "cell");
                        assertNotEquals(null, context, "context");

                        return saved();
                    }
                }),
                this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.saved()));
    }

    @Test
    public void testHandleSaveMultipleCellsFails() {
        final SpreadsheetCell cell = this.cell();
        final SpreadsheetCell z99 = SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference("Z99"), SpreadsheetFormula.with("99"));

        this.handleFails(this.id(),
                Optional.of(SpreadsheetDelta.with(Sets.of(cell, z99))),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testHandleSaveWithWindowFilter() {
        final SpreadsheetCell unsaved1 = this.cell();

        final SpreadsheetCell saved1 = unsaved1.setFormatted(Optional.of(TextNode.text("FORMATTED1")));
        final SpreadsheetCell saved2 = this.cellOutsideWindow().setFormatted(Optional.of(TextNode.text("FORMATTED2")));

        final List<SpreadsheetRange> window = this.window();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                                                                         final SpreadsheetEngineContext context) {
                        Objects.requireNonNull(context, "context");

                        assertEquals(SpreadsheetEngineSaveCellHateosHandlerTest.this.cell(), cell, "cell");
                        assertNotEquals(null, context, "context");

                        return SpreadsheetDelta.with(Sets.of(saved1, saved2)).setWindow(window);
                    }
                }),
                this.id(),
                Optional.of(SpreadsheetDelta.with(Sets.of(unsaved1)).setWindow(window)),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(Sets.of(saved1)).setWindow(window)));
    }

    // handleCollection.................................................................................................

    @Test
    public void testHandleCollectionBatchSaves() {
        final SpreadsheetCell b2 = this.cell("B2", "1");
        final SpreadsheetCell c3 = this.cell("C3", "2");
        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(b2.reference(), c3.reference()));

        final SpreadsheetCell d4 = this.cell("C4", "3");
        final SpreadsheetDelta result = SpreadsheetDelta.with(Sets.of(b2, c3, d4));

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                                                                       final SpreadsheetRange from,
                                                                                       final SpreadsheetRange to,
                                                                                       final SpreadsheetEngineContext context) {
                        assertEquals(Sets.of(b2, c3), new LinkedHashSet<>(cells), "cells");
                        assertEquals(range, from, "from");
                        assertEquals(range, to, "to");

                        return result;
                    }
                }),
                range.range(),
                Optional.of(SpreadsheetDelta.with(Sets.of(b2, c3))),
                this.parameters(),
                Optional.of(result));
    }

    @Test
    public void testHandleCollectionBatchSavesFiltersWindow() {
        final SpreadsheetCell unsaved1 = this.cell("B2", "1");
        final SpreadsheetCell unsaved2 = this.cell("B3", "2");

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(unsaved1.reference(), unsaved2.reference()));

        final SpreadsheetCell saved1 = unsaved1.setFormatted(Optional.of(TextNode.text("FORMATTED1")));
        final SpreadsheetCell saved2 = unsaved2.setFormatted(Optional.of(TextNode.text("FORMATTED2")));
        final SpreadsheetCell saved3 = this.cellOutsideWindow().setFormatted(Optional.of(TextNode.text("FORMATTED3")));

        final List<SpreadsheetRange> window = this.window();

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                                                                       final SpreadsheetRange from,
                                                                                       final SpreadsheetRange to,
                                                                                       final SpreadsheetEngineContext context) {
                        assertEquals(Sets.of(unsaved1, unsaved2), new LinkedHashSet<>(cells), "cells");
                        assertEquals(range, from, "from");
                        assertEquals(range, to, "to");

                        return SpreadsheetDelta.with(Sets.of(saved1, saved2, saved3));
                    }
                }),
                range.range(),
                Optional.of(SpreadsheetDelta.with(Sets.of(unsaved1, unsaved2)).setWindow(window)),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(Sets.of(saved1, saved2)).setWindow(window)));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.saveCell");
    }

    @Override
    SpreadsheetEngineSaveCellHateosHandler createHandler(final SpreadsheetEngine engine,
                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
    }

    private SpreadsheetEngineSaveCellHateosHandler createHandler(final SpreadsheetEngine engine) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, this.engineContext());
    }

    @Override
    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return SpreadsheetCellReference.parseCellReferenceRange("B2:D4");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        final SpreadsheetCell cell = this.cell();
        return Optional.of(SpreadsheetDelta.with(Sets.of(cell)));
    }

    @Override
    public Optional<SpreadsheetDelta> collectionResource() {
        return Optional.of(SpreadsheetDelta.with(Sets.of(this.cell())));
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    @Override
    SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetDelta saved() {
        return SpreadsheetDelta.with(Sets.of(savedCell()));
    }

    private SpreadsheetCell savedCell() {
        return this.cell().setFormula(SpreadsheetFormula.with("1+2").setError(Optional.of(SpreadsheetError.with("Error something"))));
    }

    @Override
    public Class<SpreadsheetEngineSaveCellHateosHandler> type() {
        return Cast.to(SpreadsheetEngineSaveCellHateosHandler.class);
    }
}
