package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelStoreTesting<S extends SpreadsheetLabelStore> extends StoreTesting<S, SpreadsheetLabelName, SpreadsheetLabelMapping> {

    @Test
    default void testLoadCellReferencesOrRangesNullLabelFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().loadCellReferencesOrRanges(null);
        });
    }

    default void loadCellReferencesOrRangesAndCheck(final SpreadsheetLabelStore store,
                                                    final SpreadsheetLabelName label,
                                                    final Set<? super ExpressionReference> referencesOrRanges) {
        assertEquals(referencesOrRanges,
                store.loadCellReferencesOrRanges(label),
                ()-> "loadCellReferencesOrRanges for " + label);
    }
}
