package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;

public enum SpreadsheetEngineLoading {

    /**
     * Will return the cell in its current form without any attempt to parse or evaluate the formula.
     */
    SKIP_EVALUATE {
        @Override
        SpreadsheetCell process(final SpreadsheetCell cell, final BasicSpreadsheetEngine engine) {
            return cell;
        }
    },
    /**
     * Will parse if required and then evaluate the formula.
     */
    FORCE_RECOMPUTE {
        @Override
        SpreadsheetCell process(final SpreadsheetCell cell, final BasicSpreadsheetEngine engine) {
            return engine.evaluateIfPossible(
                   engine.parseIfNecessary(
                   cell.setValue(SpreadsheetCell.NO_VALUE)));
        }
    },
    /**
     * Will only parse and evaluate as required if a value is absent.
     */
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetCell process(final SpreadsheetCell cell, final BasicSpreadsheetEngine engine) {
            return engine.evaluateIfPossible(
                   engine.parseIfNecessary(cell));
        }
    };

    abstract SpreadsheetCell process(final SpreadsheetCell cell, final BasicSpreadsheetEngine engine);
}
