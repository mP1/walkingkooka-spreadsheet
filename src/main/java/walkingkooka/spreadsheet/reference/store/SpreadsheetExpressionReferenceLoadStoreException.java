package walkingkooka.spreadsheet.reference.store;

import walkingkooka.store.LoadStoreException;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.HasExpressionReference;

import java.util.Objects;

/**
 * This exception is thrown whenever a reference load fails.
 */
public class SpreadsheetExpressionReferenceLoadStoreException extends LoadStoreException implements HasExpressionReference {

    private static final long serialVersionUID = 1;

    protected SpreadsheetExpressionReferenceLoadStoreException() {
        super();
        this.expressionReference = null;
    }

    public SpreadsheetExpressionReferenceLoadStoreException(final String message,
                                                            final ExpressionReference expressionReference) {
        super(message);
        this.expressionReference = Objects.requireNonNull(expressionReference, "experienceReference");
    }

    public SpreadsheetExpressionReferenceLoadStoreException(final String message,
                                                            final ExpressionReference expressionReference,
                                                            final Throwable cause) {
        super(message, cause);
        this.expressionReference = Objects.requireNonNull(expressionReference, "experienceReference");
    }

    @Override
    public ExpressionReference expressionReference() {
        return this.expressionReference;
    }

    private final ExpressionReference expressionReference;
}
