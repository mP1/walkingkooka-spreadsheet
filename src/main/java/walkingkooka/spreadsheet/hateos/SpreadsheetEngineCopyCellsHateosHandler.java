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

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#copyCells(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineCopyCellsHateosHandler extends SpreadsheetEngineHateosHandler2<SpreadsheetCellReference> {

    static SpreadsheetEngineCopyCellsHateosHandler with(final SpreadsheetEngine engine,
                                                        final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineCopyCellsHateosHandler(engine, context);
    }

    private SpreadsheetEngineCopyCellsHateosHandler(final SpreadsheetEngine engine,
                                                    final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    String id() {
        return "cellreference";
    }

    @Override
    SpreadsheetDelta handle0(final SpreadsheetCellReference id,
                             final SpreadsheetDelta resource,
                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    void checkRange(final Range<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    @Override
    SpreadsheetDelta handleCollection0(final Range<SpreadsheetCellReference> cells,
                                       final SpreadsheetDelta resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.engine.copyCells(resource.cells(),
                this.parameterValueOrFail(parameters, TO, SpreadsheetExpressionReference::parseRange),
                this.context);
    }

    // @VisibleForTesting
    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "copyCells"; // SpreadsheetEngine#copyCells
    }
}
