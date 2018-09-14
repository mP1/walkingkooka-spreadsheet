package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Optional;

public class FakeSpreadsheetEngine implements SpreadsheetEngine, Fake {
    @Override
    public SpreadsheetId id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell, final SpreadsheetEngineLoading loading) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLabel(final SpreadsheetLabelName label, final SpreadsheetCellReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLabel(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCellReference> label(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }
}
