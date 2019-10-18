/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetDataValidatorTesting<V extends SpreadsheetDataValidator, T> extends ClassTesting<V>,
        ToStringTesting<V> {

    @Test
    default void testValueType() {
        assertEquals(this.valueType(), this.createSpreadsheetDataValidator().valueType());
    }

    @SuppressWarnings("unchecked")
    @Test
    default void testValidateNullValueFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDataValidator().validate(null, this.createContext()));
    }

    @SuppressWarnings("unchecked")
    @Test
    default void testValidateNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetDataValidator().validate(this.value(), null));
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
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
