package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;

public enum SpreadsheetEngineLoading {

    /**
     * Performs no new evaluation of the formula, leaves the original value or error alone and does not change the style.
     */
    SKIP_EVALUATE {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return cell;
        }
    },
    /**
     * Clears the value in the formula, evaluates the formula and value and applies styling.
     */
    FORCE_RECOMPUTE {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return engine.formulaEvaluateAndStyle(cell.setFormula(cell.formula().setValue(SpreadsheetFormula.NO_VALUE)),
                    context);
        }
    },

    /**
     * Evaluates the formula and value and applies styling.
     */
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return engine.formulaEvaluateAndStyle(cell, context);
        }
    };

    abstract SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetEngineContext context);
}
