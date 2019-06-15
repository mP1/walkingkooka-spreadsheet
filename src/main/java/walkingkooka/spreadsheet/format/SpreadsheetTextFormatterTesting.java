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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.CharSequences;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mixin interface with default methods to assist testing of an {@link SpreadsheetTextFormatter}.
 */
public interface SpreadsheetTextFormatterTesting<F extends SpreadsheetTextFormatter<V>, V>
        extends ToStringTesting<F>,
        TypeNameTesting<F> {

    @Test
    default void testFormatNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createFormatter().format(null, this.createContext());
        });
    }

    @Test
    default void testFormatNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createFormatter().format(this.value(), null);
        });
    }

    @Test
    default void testType() {
        final Class<V> type = this.createFormatter().type();
        assertNotEquals(null, type);

        final V value = this.value();
        assertTrue(type.isInstance(value), () -> type.getName() + " " + value.getClass().getName() + "=" + value);
    }

    F createFormatter();

    V value();

    SpreadsheetTextFormatContext createContext();

    default void formatAndCheck(final V value,
                                final String formattedText) {
        this.formatAndCheck(value, this.formattedText(formattedText));
    }

    default void formatAndCheck(final V value,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(this.createFormatter(), value, formattedText);
    }

    default void formatAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                final V value,
                                final String formattedText) {
        this.formatAndCheck(formatter, value, this.formattedText(formattedText));
    }

    default void formatAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                final V value,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(formatter, value, this.createContext(), formattedText);
    }

    default void formatAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                final V value,
                                final SpreadsheetTextFormatContext context,
                                final String formattedText) {
        this.formatAndCheck(formatter, value, context, this.formattedText(formattedText));
    }

    default void formatAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                final V value,
                                final SpreadsheetTextFormatContext context,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(formatter, value, context, Optional.of(formattedText));
    }

    default SpreadsheetFormattedText formattedText(final String text) {
        return SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text);
    }

    // format fail and check

    default void formatFailAndCheck(final V value) {
        this.formatFailAndCheck(value, this.createContext());
    }

    default void formatFailAndCheck(final V value,
                                    final SpreadsheetTextFormatContext context) {
        this.formatFailAndCheck(this.createFormatter(), value, context);
    }

    default void formatFailAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                    final V value) {
        this.formatFailAndCheck(formatter, value, this.createContext());
    }

    default void formatFailAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                    final V value,
                                    final SpreadsheetTextFormatContext context) {
        this.formatAndCheck(formatter, value, context, Optional.empty());
    }

    // general format and check

    default void formatAndCheck(final SpreadsheetTextFormatter<V> formatter,
                                final V value,
                                final SpreadsheetTextFormatContext context,
                                final Optional<SpreadsheetFormattedText> formattedText) {
        assertEquals(formattedText,
                formatter.format(value, context),
                () -> formatter + " " + CharSequences.quoteIfChars(value));
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetTextFormatter.class.getSimpleName();
    }
}
