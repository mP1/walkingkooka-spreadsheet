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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetEngineSaveCellHateosHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineSaveCellHateosHandler,
        SpreadsheetCellReference> {

    @Test
    public void testSaveCellMissingFails() {
        this.handleFails(this.id(),
                Optional.empty(),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testSaveCell() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.saved()));
    }

    @Test
    public void testSaveCellCollectionFails() {
        this.handleCollectionUnsupported(this.createHandler(),
                Range.all(),
                this.collectionResource(),
                this.parameters());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.saveCell");
    }

    @Override
    SpreadsheetEngineSaveCellHateosHandler createHandler(final SpreadsheetEngine engine,
                                                         final SpreadsheetEngineContext context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
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
    public Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> resource() {
        final SpreadsheetCell cell = this.cell();
        return Optional.of(SpreadsheetDelta.withId(cell.id(), Sets.of(cell)));
    }

    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> collectionResource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public SpreadsheetDelta<Optional<SpreadsheetCellReference>> saveCell(final SpreadsheetCell cell,
                                                                                 final SpreadsheetEngineContext context) {
                Objects.requireNonNull(context, "context");

                assertEquals(SpreadsheetEngineSaveCellHateosHandlerTest.this.cell(), cell, "cell");
                assertNotEquals(null, context, "context");

                return saved();
            }
        };
    }

    private SpreadsheetDelta<Optional<SpreadsheetCellReference>> saved() {
        final SpreadsheetCell saved = savedCell();
        return SpreadsheetDelta.withId(Optional.of(saved.reference()), Sets.of(savedCell()));
    }

    private SpreadsheetCell savedCell() {
        return this.cell().setFormula(SpreadsheetFormula.with("1+2").setError(Optional.of(SpreadsheetError.with("Error something"))));
    }

    @Override
    public Class<SpreadsheetEngineSaveCellHateosHandler> type() {
        return Cast.to(SpreadsheetEngineSaveCellHateosHandler.class);
    }
}
