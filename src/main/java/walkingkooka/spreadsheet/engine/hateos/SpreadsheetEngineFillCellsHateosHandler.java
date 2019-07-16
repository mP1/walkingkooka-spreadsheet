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

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#fillCells(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineFillCellsHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetCellReference> {

    static SpreadsheetEngineFillCellsHateosHandler with(final SpreadsheetEngine engine,
                                                        final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineFillCellsHateosHandler(engine, context);
    }

    private SpreadsheetEngineFillCellsHateosHandler(final SpreadsheetEngine engine,
                                                    final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> handle(final Optional<SpreadsheetCellReference> id,
                                                                                 final Optional<SpreadsheetDelta<Optional<SpreadsheetCellReference>>> resource,
                                                                                 final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkIdNotNull(id);
        this.checkResource(resource);
        this.checkParameters(parameters);

        throw new UnsupportedOperationException();
    }


    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                                                        final Optional<SpreadsheetDelta<Range<SpreadsheetCellReference>>> resource,
                                                                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkRangeNotNull(ids);
        final SpreadsheetDelta<Range<SpreadsheetCellReference>> delta = this.checkResourceNotEmpty(resource);
        this.checkParameters(parameters);

        return Optional.of(this.engine.fillCells(delta.cells(),
                this.parameterValueOrFail(parameters, TO, SpreadsheetExpressionReference::parseRange),
                this.context));
    }

    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "fillCells"; // SpreadsheetEngine#fillCells
    }
}
