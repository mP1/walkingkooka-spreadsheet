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

import walkingkooka.collect.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#saveCell(SpreadsheetCell, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetCellReference> {

    static SpreadsheetEngineLoadCellHateosHandler with(final SpreadsheetEngineEvaluation evaluation,
                                                       final SpreadsheetEngine engine,
                                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(evaluation, "evaluation");

        check(engine, context);
        return new SpreadsheetEngineLoadCellHateosHandler(evaluation,
                engine,
                context);
    }

    private SpreadsheetEngineLoadCellHateosHandler(final SpreadsheetEngineEvaluation evaluation,
                                                   final SpreadsheetEngine engine,
                                                   final SpreadsheetEngineContext context) {
        super(engine, context);
        this.evaluation = evaluation;
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final Optional<SpreadsheetCellReference> id,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetCellReference reference = this.checkIdRequired(id);
        this.checkResource(resource);
        this.checkParameters(parameters);

        checkWithoutCells(resource);

        return Optional.of(applyWindow(this.loadCell(reference), resource));
    }

    SpreadsheetDelta loadCell(final SpreadsheetCellReference reference) {
        return this.engine.loadCell(reference,
                this.evaluation,
                this.context);
    }

    private final SpreadsheetEngineEvaluation evaluation;

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetCellReference> cells,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkRangeNotNull(cells);
        this.checkResource(resource);
        this.checkParameters(parameters);

        checkWithoutCells(resource);

        return Optional.ofNullable(applyWindow(SpreadsheetEngineLoadCellHateosHandlerBatchLoader.with(this).batchLoad(cells), resource));
    }

    @Override
    String operation() {
        return "loadCell " + this.evaluation;
    }
}
