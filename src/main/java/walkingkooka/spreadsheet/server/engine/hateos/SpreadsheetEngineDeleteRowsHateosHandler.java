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

package walkingkooka.spreadsheet.server.engine.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteRows(SpreadsheetRowReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteRowsHateosHandler extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler<SpreadsheetRowReference> {

    static SpreadsheetEngineDeleteRowsHateosHandler with(final SpreadsheetEngine engine,
                                                         final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosHandler(engine, context);
    }

    private SpreadsheetEngineDeleteRowsHateosHandler(final SpreadsheetEngine engine,
                                                     final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    String rangeLabel() {
        return "rows";
    }

    @Override
    SpreadsheetDelta execute(final SpreadsheetRowReference row, final int count) {
        return this.engine.deleteRows(row, count, this.context);
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
