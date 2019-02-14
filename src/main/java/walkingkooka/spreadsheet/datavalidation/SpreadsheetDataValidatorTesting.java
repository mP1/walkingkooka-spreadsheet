package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetDataValidatorTesting<V extends SpreadsheetDataValidator, T> extends ClassTesting<V>,
        ToStringTesting<V> {

    @Test
    default void testValueType() {
        assertEquals(this.valueType(), this.createSpreadsheetDataValidator().valueType());
    }

    @Test
    default void testValidateNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetDataValidator().validate(null, this.createContext());
        });
    }

    @Test
    default void testValidateNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetDataValidator().validate(this.value(), null);
        });
    }

    V createSpreadsheetDataValidator();

    T value();

    Class<T> valueType();

    SpreadsheetDataValidatorContext createContext();

    // validatePassCheck............................................................................................

    default void validatePassCheck(final T value) {
        this.validatePassCheck(value, this.createContext());
    }

    default void validatePassCheck(final T value,
                                   final SpreadsheetDataValidatorContext context) {
        this.validatePassCheck(this.createSpreadsheetDataValidator(), value, context);
    }

    default <TT> void validatePassCheck(final SpreadsheetDataValidator<TT> validator,
                                        final TT value) {
        this.validatePassCheck(validator, value, this.createContext());
    }

    default <TT> void validatePassCheck(final SpreadsheetDataValidator<TT> validator,
                                        final TT value,
                                        final SpreadsheetDataValidatorContext context) {
        this.validateAndCheck(validator, value, context, true);
    }

    // validateFailAndCheck............................................................................................

    default void validateFailCheck(final T value) {
        this.validateFailCheck(value, this.createContext());
    }

    default void validateFailCheck(final T value,
                                   final SpreadsheetDataValidatorContext context) {
        this.validateFailCheck(this.createSpreadsheetDataValidator(), value, context);
    }

    default <TT> void validateFailCheck(final SpreadsheetDataValidator<TT> validator,
                                        final TT value) {
        this.validateFailCheck(validator, value, this.createContext());
    }

    default <TT> void validateFailCheck(final SpreadsheetDataValidator<TT> validator,
                                        final TT value,
                                        final SpreadsheetDataValidatorContext context) {
        this.validateAndCheck(validator, value, context, false);
    }

    default <TT> void validateAndCheck(final SpreadsheetDataValidator<TT> validator,
                                       final TT value,
                                       final SpreadsheetDataValidatorContext context,
                                       final boolean expected) {
        assertEquals(expected,
                validator.validate(value, context),
                validator + " " + CharSequences.quoteIfChars(value));
    }

    @Override
    default MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
