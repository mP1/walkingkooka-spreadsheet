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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.reference.TargetAndSpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

/**
 * Controls what happens whenever a save to cell store happens.
 */
enum BasicSpreadsheetEngineUpdatedCellsMode {

    /**
     * Perform any associated actions immediately
     */
    IMMEDIATE {
        @Override
        void onCellSaved(final SpreadsheetCell cell,
                         final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellSavedImmediate(cell);
        }

        @Override
        void onCellDeleted(final SpreadsheetCellReference cell,
                           final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellDeletedImmediate(cell);
        }

        @Override
        void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                    final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellReferenceDeletedImmediate(targetAndReference);
        }

        @Override
        void onLabelSaved(final SpreadsheetLabelMapping mapping,
                          final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onLabelSavedImmediate(mapping);
        }

        @Override
        void onLabelDeleted(final SpreadsheetLabelName label,
                            final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onLabelDeletedImmediate(label);
        }
    },

    /**
     * The cell formula needs to be evaluated and saved once the batch operation completes.
     */
    BATCH {
        @Override
        void onCellSaved(final SpreadsheetCell cell,
                         final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellSavedBatch(cell);
        }

        @Override
        void onCellDeleted(final SpreadsheetCellReference cell,
                           final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellDeletedBatch(cell);
        }

        @Override
        void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                    final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onCellReferenceDeletedBatch(targetAndReference);
        }

        @Override
        void onLabelSaved(final SpreadsheetLabelMapping mapping,
                          final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onLabelSavedBatch(mapping);
        }

        @Override
        void onLabelDeleted(final SpreadsheetLabelName label,
                            final BasicSpreadsheetEngineUpdatedCells cells) {
            cells.onLabelDeletedBatch(label);
        }
    };

    /**
     * Factory that creates a {@link BasicSpreadsheetEngineUpdatedCells}
     */
    final BasicSpreadsheetEngineUpdatedCells createUpdatedCells(final BasicSpreadsheetEngine engine,
                                                                final SpreadsheetEngineContext context) {
        return BasicSpreadsheetEngineUpdatedCells.with(engine, context, this);
    }

    abstract void onCellSaved(final SpreadsheetCell cell,
                              final BasicSpreadsheetEngineUpdatedCells cells);

    abstract void onCellDeleted(final SpreadsheetCellReference cell,
                                final BasicSpreadsheetEngineUpdatedCells cells);

    abstract void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                         final BasicSpreadsheetEngineUpdatedCells cells);

    abstract void onLabelSaved(final SpreadsheetLabelMapping mapping,
                               final BasicSpreadsheetEngineUpdatedCells cells);

    abstract void onLabelDeleted(final SpreadsheetLabelName label,
                                 final BasicSpreadsheetEngineUpdatedCells cells);
}
