package walkingkooka.spreadsheet.datavalidation;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetDataValidator} that tests {@link String text} against a {@link Predicate condition}.
 */
final class PredicateSpreadsheetDataValidator<T> extends SpreadsheetDataValidatorTemplate<T> {

    static <T> PredicateSpreadsheetDataValidator<T> with(final Class<T> valueType, final Predicate<? super T> condition) {
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(condition, "condition");

        return new PredicateSpreadsheetDataValidator<>(valueType, condition);
    }

    /**
     * Private ctor
     */
    private PredicateSpreadsheetDataValidator(final Class<T> valueType, final Predicate<? super T> condition) {
        super();
        this.valueType = valueType;
        this.condition = condition;
    }

    @Override
    public Class<T> valueType() {
        return this.valueType;
    }

    private final Class<T> valueType;

    @Override
    boolean validate0(final T value, final SpreadsheetDataValidatorContext context) {
        return this.condition.test(value);
    }

    @Override
    public String toString() {
        return this.condition.toString();
    }

    private final Predicate<? super T> condition;
}
