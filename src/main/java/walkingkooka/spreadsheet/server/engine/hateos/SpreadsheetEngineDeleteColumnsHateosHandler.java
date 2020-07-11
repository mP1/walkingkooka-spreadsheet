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
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteColumns(SpreadsheetColumnReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteColumnsHateosHandler extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler<SpreadsheetColumnReference> {

    static SpreadsheetEngineDeleteColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final SpreadsheetEngineContext context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosHandler(engine, context);
    }

    private SpreadsheetEngineDeleteColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final SpreadsheetEngineContext context) {
        super(engine, context);
    }

    @Override
    String rangeLabel() {
        return "columns";
    }

    @Override
    SpreadsheetDelta execute(final SpreadsheetColumnReference column, final int count) {
        return this.engine.deleteColumns(column, count, this.context);
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
