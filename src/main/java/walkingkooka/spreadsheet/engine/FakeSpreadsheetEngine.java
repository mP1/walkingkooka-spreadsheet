package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetEngine implements SpreadsheetEngine, Fake {
    @Override
    public SpreadsheetId id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetEngineLoading loading,
                                              final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> saveCell(final SpreadsheetCell cell,
                                         final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> deleteColumns(final SpreadsheetColumnReference column,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> deleteRows(final SpreadsheetRowReference row,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> insertColumns(final SpreadsheetColumnReference column,
                                              final int count,
                                              final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> insertRows(final SpreadsheetRowReference row,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> copyCells(final Collection<SpreadsheetCell> from,
                                          final SpreadsheetRange to,
                                          final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }
}
