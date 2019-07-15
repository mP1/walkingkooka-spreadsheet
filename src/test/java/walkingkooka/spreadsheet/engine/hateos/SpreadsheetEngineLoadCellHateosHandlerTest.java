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
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineLoadCellHateosHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineLoadCellHateosHandler,
        SpreadsheetCellReference> {

    private final static SpreadsheetEngineEvaluation EVALUATION = SpreadsheetEngineEvaluation.FORCE_RECOMPUTE;

    @Test
    public void testWithNullEvaluationFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineLoadCellHateosHandler.with(null, this.engine(), this.engineContext());
        });
    }

    @Test
    public void testLoadCell() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.spreadsheetDelta()));
    }

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
        return SpreadsheetRange.parseRange("B2:C3").range();
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> resource() {
        return Optional.empty();
    }

    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> collectionResource() {
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
            public SpreadsheetDelta<Optional<SpreadsheetCellReference>> loadCell(final SpreadsheetCellReference id,
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

    private SpreadsheetDelta<Optional<SpreadsheetCellReference>> spreadsheetDelta() {
        return SpreadsheetDelta.withId(this.id(), Sets.of(this.cell()));
    }

    @Override
    public Class<SpreadsheetEngineLoadCellHateosHandler> type() {
        return Cast.to(SpreadsheetEngineLoadCellHateosHandler.class);
    }
}
