package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.text.CharSequences;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDataValidatorTestCase<V extends SpreadsheetDataValidator, T> extends ClassTestCase<V> {

    @Test
    public final void testValueType() {
        assertEquals(this.valueType(), this.createSpreadsheetDataValidator().valueType());
    }

    @Test
    public final void testValidateNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetDataValidator().validate(null, this.createContext());
        });
    }

    @Test
    public final void testValidateNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetDataValidator().validate(this.value(), null);
        });
    }

    protected abstract V createSpreadsheetDataValidator();

    protected abstract T value();

    protected abstract Class<T> valueType();

    protected abstract SpreadsheetDataValidatorContext createContext();

    // validatePassCheck............................................................................................

    protected void validatePassCheck(final T value) {
        this.validatePassCheck(value, this.createContext());
    }

    protected void validatePassCheck(final T value,
                                     final SpreadsheetDataValidatorContext context) {
        this.validatePassCheck(this.createSpreadsheetDataValidator(), value, context);
    }

    protected <TT> void validatePassCheck(final SpreadsheetDataValidator<TT> validator,
                                          final TT value) {
        this.validatePassCheck(validator, value, this.createContext());
    }

    protected <TT> void validatePassCheck(final SpreadsheetDataValidator<TT> validator,
                                          final TT value,
                                          final SpreadsheetDataValidatorContext context) {
        this.validateAndCheck(validator, value, context, true);
    }

    // validateFailAndCheck............................................................................................

    protected void validateFailCheck(final T value) {
        this.validateFailCheck(value, this.createContext());
    }

    protected void validateFailCheck(final T value,
                                     final SpreadsheetDataValidatorContext context) {
        this.validateFailCheck(this.createSpreadsheetDataValidator(), value, context);
    }

    protected <TT> void validateFailCheck(final SpreadsheetDataValidator<TT> validator,
                                          final TT value) {
        this.validateFailCheck(validator, value, this.createContext());
    }

    protected <TT> void validateFailCheck(final SpreadsheetDataValidator<TT> validator,
                                          final TT value,
                                          final SpreadsheetDataValidatorContext context) {
        this.validateAndCheck(validator, value, context, false);
    }

    protected <TT> void validateAndCheck(final SpreadsheetDataValidator<TT> validator,
                                         final TT value,
                                         final SpreadsheetDataValidatorContext context,
                                         final boolean expected) {
        assertEquals(expected,
                validator.validate(value, context),
                validator + " " + CharSequences.quoteIfChars(value));
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
