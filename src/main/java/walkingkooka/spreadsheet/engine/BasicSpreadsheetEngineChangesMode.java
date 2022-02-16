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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.TargetAndSpreadsheetCellReference;

/**
 * Controls what happens whenever a save to cell store happens.
 */
enum BasicSpreadsheetEngineChangesMode {

    /**
     * Perform any associated actions immediately
     */
    IMMEDIATE {
        @Override
        void onCellSaved(final SpreadsheetCell cell,
                         final BasicSpreadsheetEngineChanges changes) {
            changes.onCellSavedImmediate(cell);
        }

        @Override
        void onCellDeleted(final SpreadsheetCellReference cell,
                           final BasicSpreadsheetEngineChanges changes) {
            changes.onCellDeletedImmediate(cell);
        }

        @Override
        void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                    final BasicSpreadsheetEngineChanges changes) {
            changes.onCellReferenceDeletedImmediate(targetAndReference);
        }

        @Override
        void onLabelSaved(final SpreadsheetLabelMapping mapping,
                          final BasicSpreadsheetEngineChanges changes) {
            changes.onLabelSavedImmediate(mapping);
        }

        @Override
        void onLabelDeleted(final SpreadsheetLabelName label,
                            final BasicSpreadsheetEngineChanges changes) {
            changes.onLabelDeletedImmediate(label);
        }
    },

    /**
     * The cell formula needs to be evaluated and saved once the batch operation completes.
     */
    BATCH {
        @Override
        void onCellSaved(final SpreadsheetCell cell,
                         final BasicSpreadsheetEngineChanges changes) {
            changes.onCellSavedBatch(cell);
        }

        @Override
        void onCellDeleted(final SpreadsheetCellReference cell,
                           final BasicSpreadsheetEngineChanges changes) {
            changes.onCellDeletedBatch(cell);
        }

        @Override
        void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                    final BasicSpreadsheetEngineChanges changes) {
            changes.onCellReferenceDeletedBatch(targetAndReference);
        }

        @Override
        void onLabelSaved(final SpreadsheetLabelMapping mapping,
                          final BasicSpreadsheetEngineChanges changes) {
            changes.onLabelSavedBatch(mapping);
        }

        @Override
        void onLabelDeleted(final SpreadsheetLabelName label,
                            final BasicSpreadsheetEngineChanges changes) {
            changes.onLabelDeletedBatch(label);
        }
    };

    /**
     * Factory that creates a {@link BasicSpreadsheetEngineChanges}
     */
    final BasicSpreadsheetEngineChanges createChanges(final BasicSpreadsheetEngine engine,
                                                      final SpreadsheetEngineContext context) {
        return BasicSpreadsheetEngineChanges.with(engine, context, this);
    }

    abstract void onCellSaved(final SpreadsheetCell cell,
                              final BasicSpreadsheetEngineChanges changes);

    abstract void onCellDeleted(final SpreadsheetCellReference cell,
                                final BasicSpreadsheetEngineChanges changes);

    abstract void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference,
                                         final BasicSpreadsheetEngineChanges changes);

    abstract void onLabelSaved(final SpreadsheetLabelMapping mapping,
                               final BasicSpreadsheetEngineChanges changes);

    abstract void onLabelDeleted(final SpreadsheetLabelName label,
                                 final BasicSpreadsheetEngineChanges changes);
}
