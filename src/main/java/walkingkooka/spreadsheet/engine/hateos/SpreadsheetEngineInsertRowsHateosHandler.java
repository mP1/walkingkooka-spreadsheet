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
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#insertRows(SpreadsheetRowReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineInsertRowsHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetRowReference> {

    static SpreadsheetEngineInsertRowsHateosHandler with(final SpreadsheetEngine engine,
                                                         final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineInsertRowsHateosHandler(engine, context);
    }

    private SpreadsheetEngineInsertRowsHateosHandler(final SpreadsheetEngine engine,
                                                     final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> handle(final Optional<SpreadsheetRowReference> id,
                                                                                final Optional<SpreadsheetDelta<Optional<SpreadsheetRowReference>>> resource,
                                                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetRowReference row = this.checkIdRequired(id);
        this.checkResourceEmpty(resource);
        this.checkParameters(parameters);

        return Optional.of(this.engine.insertRows(row, 1, this.context).setId(id));
    }


    @Override
    public Optional<SpreadsheetDelta<Range<SpreadsheetRowReference>>> handleCollection(final Range<SpreadsheetRowReference> rows,
                                                                                       final Optional<SpreadsheetDelta<Range<SpreadsheetRowReference>>> resource,
                                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkRangeBounded(rows, "rows");
        this.checkResourceEmpty(resource);
        this.checkParameters(parameters);

        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        return Optional.of(this.engine.insertRows(lower, upper.value() - lower.value() + 1, this.context));
    }

    @Override
    String operation() {
        return "insertRows";
    }
}
