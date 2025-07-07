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

package walkingkooka.spreadsheet.engine;

import java.util.Set;

/**
 * Operations may be batched for efficiency reasons such as moving a range of cells, the updating of references should happen after all saves,
 * or immediately such as loading a cell reference which appears within the formula of another cell.
 */
enum BasicSpreadsheetEngineChangesMode {

    /**
     * Perform any associated actions immediately
     */
    IMMEDIATE,

    /**
     * The cell formula needs to be evaluated and saved once the batch operation completes.
     */
    BATCH;

    final BasicSpreadsheetEngineChanges changes(final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
        return this.changes(
            engine,
            SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
            SpreadsheetDeltaProperties.ALL,
            context
        );
    }

    /**
     * Factory that creates a {@link BasicSpreadsheetEngineChanges}
     */
    final BasicSpreadsheetEngineChanges changes(final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineEvaluation evaluation,
                                                final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                final SpreadsheetEngineContext context) {
        return BasicSpreadsheetEngineChanges.with(
            engine,
            evaluation,
            deltaProperties,
            this,
            context
        );
    }
}


