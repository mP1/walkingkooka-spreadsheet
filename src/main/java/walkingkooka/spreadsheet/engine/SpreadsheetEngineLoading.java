package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;

public enum SpreadsheetEngineLoading {

    FORCE_RECOMPUTE {
        @Override
        SpreadsheetCell prepare(final SpreadsheetCell cell) {
            return cell.setValue(SpreadsheetCell.NO_VALUE);
        }
    },
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetCell prepare(final SpreadsheetCell cell) {
            return cell;
        }
    };

    abstract SpreadsheetCell prepare(final SpreadsheetCell cell);
}
