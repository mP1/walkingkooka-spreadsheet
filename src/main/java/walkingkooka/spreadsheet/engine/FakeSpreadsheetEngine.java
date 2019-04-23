package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Collection;
import java.util.Optional;

public class FakeSpreadsheetEngine implements SpreadsheetEngine, Fake {
    @Override
    public SpreadsheetId id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteCell(final SpreadsheetCellReference cell,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta copyCells(final Collection<SpreadsheetCell> from,
                                      final SpreadsheetRange to,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name) {
        throw new UnsupportedOperationException();
    }
}
