package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class FakeSpreadsheetLabelStore extends FakeStore<SpreadsheetLabelName, SpreadsheetLabelMapping> implements SpreadsheetLabelStore, Fake {

    @Override
    public Collection<SpreadsheetLabelMapping> all() {
        throw new UnsupportedOperationException();
    }
}
