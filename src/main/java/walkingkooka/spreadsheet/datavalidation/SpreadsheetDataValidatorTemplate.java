package walkingkooka.spreadsheet.datavalidation;

import java.util.Objects;

/**
 * Base class for all {@link SpreadsheetDataValidator} implementations in this package.
 */
abstract class SpreadsheetDataValidatorTemplate<T> implements SpreadsheetDataValidator<T> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDataValidatorTemplate() {
        super();
    }

    @Override
    public final boolean validate(final T value, final SpreadsheetDataValidatorContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.validate0(value, context);
    }

    abstract boolean validate0(final T value, final SpreadsheetDataValidatorContext context);

    @Override
    abstract public String toString();
}
