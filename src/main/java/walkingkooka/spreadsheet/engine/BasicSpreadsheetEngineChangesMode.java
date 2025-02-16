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
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.Set;

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
        void onColumnSaved(final SpreadsheetColumn column,
                           final BasicSpreadsheetEngineChanges changes) {
            changes.onColumnSavedImmediate(column);
        }

        @Override
        void onColumnDeleted(final SpreadsheetColumnReference column,
                             final BasicSpreadsheetEngineChanges changes) {
            changes.onColumnDeletedImmediate(column);
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

        @Override
        void onRowSaved(final SpreadsheetRow row,
                        final BasicSpreadsheetEngineChanges changes) {
            changes.onRowSavedImmediate(row);
        }

        @Override
        void onRowDeleted(final SpreadsheetRowReference row,
                          final BasicSpreadsheetEngineChanges changes) {
            changes.onRowDeletedImmediate(row);
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
        void onColumnSaved(final SpreadsheetColumn column,
                           final BasicSpreadsheetEngineChanges changes) {
            changes.onColumnSavedBatch(column);
        }

        @Override
        void onColumnDeleted(final SpreadsheetColumnReference column,
                             final BasicSpreadsheetEngineChanges changes) {
            changes.onColumnDeletedBatch(column);
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

        @Override
        void onRowSaved(final SpreadsheetRow row,
                        final BasicSpreadsheetEngineChanges changes) {
            changes.onRowSavedBatch(row);
        }

        @Override
        void onRowDeleted(final SpreadsheetRowReference row,
                          final BasicSpreadsheetEngineChanges changes) {
            changes.onRowDeletedBatch(row);
        }
    };

    /**
     * Factory that creates a {@link BasicSpreadsheetEngineChanges}, this should be called by all {@link SpreadsheetEngine} except for the loadCellXXX methods.
     */
    final BasicSpreadsheetEngineChanges createChanges(final BasicSpreadsheetEngine engine,
                                                      final SpreadsheetEngineContext context) {
        return this.createChanges(
                engine,
                SpreadsheetDeltaProperties.ALL,
                context
        );
    }

    /**
     * Factory that creates a {@link BasicSpreadsheetEngineChanges}
     */
    final BasicSpreadsheetEngineChanges createChanges(final BasicSpreadsheetEngine engine,
                                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                      final SpreadsheetEngineContext context) {
        return BasicSpreadsheetEngineChanges.with(
                engine,
                context,
                deltaProperties,
                this
        );
    }

    abstract void onCellSaved(final SpreadsheetCell cell,
                              final BasicSpreadsheetEngineChanges changes);

    abstract void onCellDeleted(final SpreadsheetCellReference cell,
                                final BasicSpreadsheetEngineChanges changes);

    abstract void onColumnSaved(final SpreadsheetColumn column,
                                final BasicSpreadsheetEngineChanges changes);

    abstract void onColumnDeleted(final SpreadsheetColumnReference column,
                                  final BasicSpreadsheetEngineChanges changes);

    abstract void onLabelSaved(final SpreadsheetLabelMapping mapping,
                               final BasicSpreadsheetEngineChanges changes);

    abstract void onLabelDeleted(final SpreadsheetLabelName label,
                                 final BasicSpreadsheetEngineChanges changes);

    abstract void onRowSaved(final SpreadsheetRow row,
                             final BasicSpreadsheetEngineChanges changes);

    abstract void onRowDeleted(final SpreadsheetRowReference row,
                               final BasicSpreadsheetEngineChanges changes);
}
