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

import walkingkooka.math.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#saveCell(SpreadsheetCell, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineSaveCellHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetCellReference> {

    static SpreadsheetEngineSaveCellHateosHandler with(final SpreadsheetEngine engine,
                                                       final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineSaveCellHateosHandler(engine, context);
    }

    private SpreadsheetEngineSaveCellHateosHandler(final SpreadsheetEngine engine,
                                                   final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final Optional<SpreadsheetCellReference> id,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.checkIdNotNull(id);

        final SpreadsheetDelta delta = this.checkResourceNotEmpty(resource);
        final Set<SpreadsheetCell> cells = delta.cells();
        if (cells.size() != 1) {
            throw new IllegalArgumentException("Expected 1 cell got " + cells.size());
        }
        this.checkParameters(parameters);

        return Optional.of(applyWindow(this.engine.saveCell(cells.iterator().next(), this.context),
                resource));
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetRange range = SpreadsheetRange.with(ids);
        final SpreadsheetDelta delta = this.checkResourceNotEmpty(resource);
        this.checkParameters(parameters);

        return Optional.of(this.engine.fillCells(delta.cells(),
                range,
                range,
                this.context).setWindow(delta.window()));
    }

    @Override
    String operation() {
        return "saveCell";
    }
}
