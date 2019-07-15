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
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#insertColumns(SpreadsheetColumnReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineInsertColumnsHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetColumnReference> {

    static SpreadsheetEngineInsertColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineInsertColumnsHateosHandler(engine, context);
    }

    private SpreadsheetEngineInsertColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetColumnReference>>> handle(final Optional<SpreadsheetColumnReference> id,
                                                                                   final Optional<SpreadsheetDelta<Optional<SpreadsheetColumnReference>>> resource,
                                                                                   final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetColumnReference column = this.checkIdRequired(id);
        this.checkResourceEmpty(resource);
        this.checkParameters(parameters);

        return Optional.of(this.engine.insertColumns(column, 1, this.context).setId(id));
    }


    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetColumnReference>>> handleCollection(final Range<SpreadsheetColumnReference> columns,
                                                                                          final Optional<SpreadsheetDelta<Range<SpreadsheetColumnReference>>> resource,
                                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkRangeBounded(columns, "columns");
        this.checkResourceEmpty(resource);
        this.checkParameters(parameters);

        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        return Optional.of(this.engine.insertColumns(lower, upper.value() - lower.value() + 1, this.context));
    }

    @Override
    String operation() {
        return "insertColumns";
    }
}
