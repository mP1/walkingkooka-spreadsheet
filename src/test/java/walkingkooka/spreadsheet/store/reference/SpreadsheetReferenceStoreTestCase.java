package walkingkooka.spreadsheet.store.reference;

import walkingkooka.tree.expression.ExpressionReference;

public abstract class SpreadsheetReferenceStoreTestCase<S extends SpreadsheetReferenceStore<T>, T extends ExpressionReference & Comparable<T>>
        implements SpreadsheetReferenceStoreTesting<S, T> {

    SpreadsheetReferenceStoreTestCase() {
        super();
    }
}
