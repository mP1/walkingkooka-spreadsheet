package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class FakeSpreadsheetLabelStore implements SpreadsheetLabelStore, Fake {

    @Override
    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<SpreadsheetLabelMapping> all() {
        throw new UnsupportedOperationException();
    }
}
