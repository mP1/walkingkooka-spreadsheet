package walkingkooka.spreadsheet.store.reference;

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetReferenceStore} implementations.
 */
public final class SpreadsheetReferenceStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetReferenceStore}
     */
    public static SpreadsheetReferenceStore fake() {
        return new FakeSpreadsheetReferenceStore();
    }

    /**
     * {@see TreeMapSpreadsheetReferenceStore}
     */
    public static <T extends ExpressionReference & Comparable<T>> SpreadsheetReferenceStore<T> treeMap() {
        return TreeMapSpreadsheetReferenceStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetReferenceStores() {
        throw new UnsupportedOperationException();
    }
}
