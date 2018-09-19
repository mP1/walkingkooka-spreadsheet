package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetFormula;

public enum SpreadsheetEngineLoading {

    /**
     * Will return the formula in its current form without any attempt to parse or evaluate the formula.
     */
    SKIP_EVALUATE {
        @Override
        SpreadsheetFormula process(final SpreadsheetFormula formula, final BasicSpreadsheetEngine engine) {
            return formula;
        }
    },
    /**
     * Will parse if required and then evaluate the formula.
     */
    FORCE_RECOMPUTE {
        @Override
        SpreadsheetFormula process(final SpreadsheetFormula formula, final BasicSpreadsheetEngine engine) {
            return engine.evaluateIfPossible(
                   engine.parseIfNecessary(
                   formula.setValue(SpreadsheetFormula.NO_VALUE)));
        }
    },
    /**
     * Will only parse and evaluate as required if a value is absent.
     */
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetFormula process(final SpreadsheetFormula formula, final BasicSpreadsheetEngine engine) {
            return engine.evaluateIfPossible(
                   engine.parseIfNecessary(formula));
        }
    };

    abstract SpreadsheetFormula process(final SpreadsheetFormula formula, final BasicSpreadsheetEngine engine);
}
