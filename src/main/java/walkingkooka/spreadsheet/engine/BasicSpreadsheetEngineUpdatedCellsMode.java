package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.reference.TargetAndSpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

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
}
