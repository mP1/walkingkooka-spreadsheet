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
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineLoadCellHateosHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineLoadCellHateosHandler,
        SpreadsheetCellReference> {

    private final static SpreadsheetEngineEvaluation EVALUATION = SpreadsheetEngineEvaluation.FORCE_RECOMPUTE;

    @Test
    public void testWithNullEvaluationFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetEngineLoadCellHateosHandler.with(null, this.engine(), this.engineContext()));
    }

    // handle...........................................................................................................

    @Test
    public void testLoadCell() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.spreadsheetDelta()));
    }

    @Test
    public void testLoadCellAndFilter() {
        final Optional<SpreadsheetCellReference> id = this.id();
        final List<SpreadsheetRange> window = this.window();

        this.handleAndCheck(SpreadsheetEngineLoadCellHateosHandler.with(EVALUATION,
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCell(final SpreadsheetCellReference cell,
                                                     final SpreadsheetEngineEvaluation evaluation,
                                                     final SpreadsheetEngineContext context) {
                        assertSame(EVALUATION, evaluation, "evaluation");
                        assertNotNull(context, "context");

                        return SpreadsheetDelta.with(cells());
                    }
                },
                this.engineContext()),
                id,
                Optional.of(SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS).setWindow(window)),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(this.cellsWithinWindow()).setWindow(window)));
    }

    // handleCollection.................................................................................................

    @Test
    public void testBatchLoadIndividually() {
        // B1, B2, B3
        // C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1));
        cellToDelta.put(b2.reference(), this.delta(b2));
        cellToDelta.put(b3.reference(), this.delta(b3));

        cellToDelta.put(c1.reference(), this.delta(c1));
        cellToDelta.put(c2.reference(), this.delta(c2));
        cellToDelta.put(c3.reference(), this.delta(c3));

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3, c1, c2, c3));
    }

    @Test
    public void testBatchLoadMissing() {
        // B1, B2, B3
        // C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1));
        cellToDelta.put(b2.reference(), this.delta(b2));
        cellToDelta.put(b3.reference(), this.delta(b3));

        cellToDelta.put(c1.reference(), this.delta(c1));
        cellToDelta.put(c2.reference(), this.delta(c2));
        cellToDelta.put(c3.reference(), this.delta(c3));

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3, c1, c2, c3));
    }

    @Test
    public void testBatchLoadOnce() {
        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCellReference c1 = this.c1().reference();
        final SpreadsheetCellReference c2 = this.c2().reference();
        final SpreadsheetCellReference c3 = this.c3().reference();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1, b2, b3));
        cellToDelta.put(c1, this.delta());
        cellToDelta.put(c2, this.delta());
        cellToDelta.put(c3, this.delta());

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3));
    }

    @Test
    public void testBatchLoadChunks() {
        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1, b2, c3));
        cellToDelta.put(b3.reference(), this.delta(b3));
        cellToDelta.put(c1.reference(), this.delta(c1, c2));

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3, c1, c2, c3));
    }

    @Test
    public void testBatchLoadExtra() {
        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell z99 = this.z99();

        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1, b2, b3, z99));
        cellToDelta.put(c1.reference(), this.delta(c1, c2, c3));

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3, z99, c1, c2, c3));
    }

    @Test
    public void testBatchLoadMixed() {
        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell z99 = this.z99();

        final SpreadsheetCellReference c1 = this.c1().reference();
        final SpreadsheetCellReference c2 = this.c2().reference();
        final SpreadsheetCellReference c3 = this.c3().reference();

        final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta = Maps.sorted(SpreadsheetCellReference.COMPARATOR);
        cellToDelta.put(b1.reference(), this.delta(b1, b2, b3, z99));
        cellToDelta.put(c1, this.delta());
        cellToDelta.put(c2, this.delta());
        cellToDelta.put(c3, this.delta());

        this.handleCollectionAndCheck2(cellToDelta, this.result(b1, b2, b3, z99));
    }

    private void handleCollectionAndCheck2(final Map<SpreadsheetCellReference, SpreadsheetDelta> cellToDelta,
                                           final Optional<SpreadsheetDelta> result) {
        final SpreadsheetEngineLoadCellHateosHandler handler = SpreadsheetEngineLoadCellHateosHandler.with(EVALUATION,
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCell(final SpreadsheetCellReference cell,
                                                     final SpreadsheetEngineEvaluation evaluation,
                                                     final SpreadsheetEngineContext context) {
                        assertSame(EVALUATION, evaluation, "evaluation");
                        assertNotNull(context, "context");

                        final SpreadsheetDelta delta = cellToDelta.remove(cell);
                        assertNotEquals(null, delta, () -> "unexpected cell load " + cell + " outstanding cells: " + cellToDelta.keySet());
                        return delta;
                    }

                    @Override
                    public String toString() {
                        return "load: " + cellToDelta.toString();
                    }
                },
                this.engineContext());
        this.handleCollectionAndCheck(handler,
                this.collection(),
                this.collectionResource(),
                this.parameters(),
                result);
    }

    @Test
    public void testBatchLoadIndividuallyAndFilterWindow() {
        // B1, B2, B3
        // C1, C2, C3

        final SpreadsheetCell b1 = this.b1();
        final SpreadsheetCell b2 = this.b2();
        final SpreadsheetCell b3 = this.b3();

        final SpreadsheetCell c1 = this.c1();
        final SpreadsheetCell c2 = this.c2();
        final SpreadsheetCell c3 = this.c3();

        final List<SpreadsheetCell> cells = Lists.of(b1, b2, b3, c1, c2, c3);

        final Range<SpreadsheetCellReference> range = this.collection();
        final List<SpreadsheetRange> window = this.window();

        this.handleCollectionAndCheck(SpreadsheetEngineLoadCellHateosHandler.with(EVALUATION,
                new FakeSpreadsheetEngine() {
                    @Override
                    public SpreadsheetDelta loadCell(final SpreadsheetCellReference cell,
                                                     final SpreadsheetEngineEvaluation evaluation,
                                                     final SpreadsheetEngineContext context) {
                        assertSame(EVALUATION, evaluation, "evaluation");
                        assertNotNull(context, "context");

                        final SpreadsheetCell loaded = cells.stream()
                                .filter(c -> c.reference().equalsIgnoreReferenceKind(cell))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError("Unable to find cell " + cell));

                        return SpreadsheetDelta.with(Sets.of(loaded, cellOutsideWindow()));
                    }
                },
                this.engineContext()),
                range,
                Optional.of(SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS).setWindow(window)),
                this.parameters(),
                Optional.of(SpreadsheetDelta.with(Sets.of(b1, b2, b3, c1, c2, c3)).setWindow(window)));
    }

    private SpreadsheetCell b1() {
        return this.cell("B1", "1");
    }

    private SpreadsheetCell b2() {
        return this.cell("B2", "2");
    }

    private SpreadsheetCell b3() {
        return this.cell("B3", "3");
    }

    private SpreadsheetCell c1() {
        return this.cell("c1", "4");
    }

    private SpreadsheetCell c2() {
        return this.cell("c2", "5");
    }

    private SpreadsheetCell c3() {
        return this.cell("c3", "6");
    }

    private SpreadsheetCell z99() {
        return this.cell("z99", "99");
    }

    private SpreadsheetDelta delta(final SpreadsheetCell... cells) {
        return SpreadsheetDelta.with(Sets.of(cells));
    }

    private Optional<SpreadsheetDelta> result(final SpreadsheetCell... cells) {
        return Optional.of(SpreadsheetDelta.with(Sets.of(cells)));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.loadCell " + EVALUATION);
    }

    @Override
    SpreadsheetEngineLoadCellHateosHandler createHandler(final SpreadsheetEngine engine,
                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(EVALUATION,
                engine,
                context);
    }

    @Override
    public Optional<SpreadsheetCellReference> id() {
        return Optional.of(this.spreadsheetCellReference());
    }

    private SpreadsheetCellReference spreadsheetCellReference() {
        return SpreadsheetExpressionReference.parseCellReference("B2");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return SpreadsheetRange.parseRange("B1:C3").range();
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
        return new FakeSpreadsheetEngine() {
            @Override
            public SpreadsheetDelta loadCell(final SpreadsheetCellReference id,
                                             final SpreadsheetEngineEvaluation evaluation,
                                             final SpreadsheetEngineContext context) {
                Objects.requireNonNull(id, "id");
                Objects.requireNonNull(evaluation, "evaluation");
                Objects.requireNonNull(context, "context");

                assertEquals(SpreadsheetEngineLoadCellHateosHandlerTest.this.spreadsheetCellReference(), id, "spreadsheetCellReference");
                assertEquals(EVALUATION, evaluation, "evaluation");
                assertNotEquals(null, context, "context");

                return spreadsheetDelta();
            }
        };
    }

    private SpreadsheetDelta spreadsheetDelta() {
        return SpreadsheetDelta.with(Sets.of(this.cell()));
    }

    @Override
    public Class<SpreadsheetEngineLoadCellHateosHandler> type() {
        return SpreadsheetEngineLoadCellHateosHandler.class;
    }
}
