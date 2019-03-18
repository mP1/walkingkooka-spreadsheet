package walkingkooka.spreadsheet.datavalidation;

import java.util.function.Predicate;

/**
 * A {@link Predicate like tester} that verifies an entered typed value against a condition,
 * including a custom formula.
 */
public interface SpreadsheetDataValidator<T> {

    /**
     * The type of the value being validated. This is intended as a hint to a support conversion prior to calling the validate method.
     */
    Class<T> valueType();

    /**
     * Accepts a value and returns true if it is a valid value.
     */
    boolean validate(final T value, final SpreadsheetDataValidatorContext context);
}
