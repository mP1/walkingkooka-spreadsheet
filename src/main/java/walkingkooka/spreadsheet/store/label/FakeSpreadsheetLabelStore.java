package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Collection;
import java.util.Set;

public class FakeSpreadsheetLabelStore extends FakeStore<SpreadsheetLabelName, SpreadsheetLabelMapping> implements SpreadsheetLabelStore, Fake {

    @Override
    public Collection<SpreadsheetLabelMapping> all() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }
}
