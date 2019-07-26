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
import walkingkooka.collect.list.Lists;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.CharSequences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface with default methods to assist testing of an {@link SpreadsheetTextFormatter}.
 */
public interface SpreadsheetTextFormatterTesting<F extends SpreadsheetTextFormatter>
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
    default void testFormatUnsupportedValueFails() {
        final List<Object> values = Lists.of(true,
                BigDecimal.TEN,
                BigInteger.valueOf(11),
                Byte.MAX_VALUE,
                123.5,
                123.5f,
                123,
                LocalDate.of(2000, 12, 31),
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                LocalTime.of(12, 58, 59),
                123L,
                Short.valueOf((short) 123),
                "abc123",
                this);

        final F formatter = this.createFormatter();
        final SpreadsheetTextFormatContext context = this.createContext();

        assertEquals(Lists.empty(),
                values.stream()
                        .filter(formatter::canFormat)
                        .filter((v) -> {
                            boolean failed = false;

                            try {
                                formatter.format(v, context);
                            } catch (final Throwable expected) {
                                failed = true;
                            }
                            return failed;
                        })
                        .map(v -> v.getClass().getName() + "=" + CharSequences.quoteIfChars(v))
                        .collect(Collectors.toList()),
                () -> "canFormat return false and format didnt fail");
    }

    @Test
    default void testCanFormatTrue() {
        this.canFormatAndCheck(this.value(), true);
    }

    @Test
    default void testCanFormatFalse() {
        this.canFormatAndCheck(this, false);
    }

    F createFormatter();

    Object value();

    SpreadsheetTextFormatContext createContext();

    default void canFormatAndCheck(final Object value, final boolean expected) {
        this.canFormatAndCheck(this.createFormatter(),
                value,
                expected);
    }

    default void canFormatAndCheck(final SpreadsheetTextFormatter formatter,
                                   final Object value,
                                   final boolean expected) {
        assertEquals(expected,
                formatter.canFormat(value),
                () -> formatter + " canFormat " + CharSequences.quoteIfChars(value));
    }

    default void formatAndCheck(final Object value,
                                final String formattedText) {
        this.formatAndCheck(value, this.formattedText(formattedText));
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(this.createFormatter(), value, formattedText);
    }

    default void formatAndCheck(final SpreadsheetTextFormatter formatter,
                                final Object value,
                                final String formattedText) {
        this.formatAndCheck(formatter, value, this.formattedText(formattedText));
    }

    default void formatAndCheck(final SpreadsheetTextFormatter formatter,
                                final Object value,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(formatter, value, this.createContext(), formattedText);
    }

    default void formatAndCheck(final SpreadsheetTextFormatter formatter,
                                final Object value,
                                final SpreadsheetTextFormatContext context,
                                final String formattedText) {
        this.formatAndCheck(formatter, value, context, this.formattedText(formattedText));
    }

    default void formatAndCheck(final SpreadsheetTextFormatter formatter,
                                final Object value,
                                final SpreadsheetTextFormatContext context,
                                final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(formatter, value, context, Optional.of(formattedText));
    }

    default SpreadsheetFormattedText formattedText(final String text) {
        return SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text);
    }

    // format fail and check

    default void formatFailAndCheck(final Object value) {
        this.formatFailAndCheck(value, this.createContext());
    }

    default void formatFailAndCheck(final Object value,
                                    final SpreadsheetTextFormatContext context) {
        this.formatFailAndCheck(this.createFormatter(), value, context);
    }

    default void formatFailAndCheck(final SpreadsheetTextFormatter formatter,
                                    final Object value) {
        this.formatFailAndCheck(formatter, value, this.createContext());
    }

    default void formatFailAndCheck(final SpreadsheetTextFormatter formatter,
                                    final Object value,
                                    final SpreadsheetTextFormatContext context) {
        this.formatAndCheck(formatter, value, context, Optional.empty());
    }

    // general format and check

    default void formatAndCheck(final SpreadsheetTextFormatter formatter,
                                final Object value,
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
