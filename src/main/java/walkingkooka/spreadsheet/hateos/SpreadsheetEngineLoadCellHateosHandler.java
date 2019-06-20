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

import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#loadCell(SpreadsheetCellReference, SpreadsheetEngineEvaluation, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> {

    static SpreadsheetEngineLoadCellHateosHandler with(final SpreadsheetEngine engine,
                                                       final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineLoadCellHateosHandler(engine, context);
    }

    private SpreadsheetEngineLoadCellHateosHandler(final SpreadsheetEngine engine,
                                                   final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final SpreadsheetCellReference cellReference,
                                             final Optional<SpreadsheetCell> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(cellReference, "cellReference");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return this.engine.loadCell(cellReference,
                this.parameterValueOrFail(parameters, EVALUATION, SpreadsheetEngineEvaluation::valueOf),
                this.context)
                .map(this::handleLoadCell);
    }

    private SpreadsheetDelta handleLoadCell(final SpreadsheetCell cell) {
        return SpreadsheetDelta.with(this.engine.id(), Sets.of(cell));
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                       final Optional<SpreadsheetCell> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResource(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    private final static UrlParameterName EVALUATION = UrlParameterName.with("evaluation");

    @Override
    String operation() {
        return "loadCell";
    }
}
