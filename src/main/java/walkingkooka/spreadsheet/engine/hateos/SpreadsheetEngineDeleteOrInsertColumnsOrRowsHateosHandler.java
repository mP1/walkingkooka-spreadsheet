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
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;

import java.util.Map;
import java.util.Optional;

/**
 * A template that also filters using any requested {@link SpreadsheetDelta#window()}} if present.
 */
abstract class SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler<R extends SpreadsheetColumnOrRowReference<R>> extends SpreadsheetEngineHateosHandler<R> {

    SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler(final SpreadsheetEngine engine,
                                                              final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public final Optional<SpreadsheetDelta<Optional<R>>> handle(final Optional<R> id,
                                                                final Optional<SpreadsheetDelta<Optional<R>>> resource,
                                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        final R columnOrRow = this.checkIdRequired(id);
        this.checkResource(resource);
        this.checkParameters(parameters);

        return Optional.of(this.executeAndWindowFilter(columnOrRow,
                1,
                resource)
                .setId(id)); //setId necessary as $resource may be empty and we dont want the id=range of the result.
    }


    @Override
    public final Optional<SpreadsheetDelta<Range<R>>> handleCollection(final Range<R> columnOrRow,
                                                                       final Optional<SpreadsheetDelta<Range<R>>> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkRangeBounded(columnOrRow, this.rangeLabel());
        this.checkResource(resource);
        this.checkParameters(parameters);

        final R lower = columnOrRow.lowerBound().value().get();
        final R upper = columnOrRow.upperBound().value().get();

        return Optional.of(this.executeAndWindowFilter(lower,
                upper.value() - lower.value() + 1,
                resource));
    }

    abstract String rangeLabel();

    private <I> SpreadsheetDelta<I> executeAndWindowFilter(final R lower,
                                                           final int count,
                                                           final Optional<SpreadsheetDelta<I>> in) {
        checkWithoutCells(in);

        return applyWindow(this.execute(lower, count), in);
    }

    /**
     * Sub classes must perform the delete or insert option.
     */
    abstract <I> SpreadsheetDelta<I> execute(final R lower, final int count);
}
