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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface with default methods to assist testing of an {@link SpreadsheetFormatter}.
 */
public interface SpreadsheetFormatterTesting2<F extends SpreadsheetFormatter>
        extends SpreadsheetFormatterTesting,
        ToStringTesting<F>,
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
        final SpreadsheetFormatterContext context = this.createContext();

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

    SpreadsheetFormatterContext createContext();

    default void canFormatAndCheck(final Object value, final boolean expected) {
        this.canFormatAndCheck(this.createFormatter(),
                value,
                expected);
    }

    default void canFormatAndCheck(final SpreadsheetFormatter formatter,
                                   final Object value,
                                   final boolean expected) {
        assertEquals(expected,
                formatter.canFormat(value),
                () -> formatter + " canFormat " + CharSequences.quoteIfChars(value));
    }

    default void formatAndCheck(final Object value,
                                final String text) {
        this.formatAndCheck(this.createFormatter(),
                value,
                text);
    }

    default void formatAndCheck(final Object value,
                                final SpreadsheetText text) {
        this.formatAndCheck(this.createFormatter(),
                value,
                text);
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final String text) {
        this.formatAndCheck(formatter,
                value,
                this.createContext(),
                text);
    }

    default void formatAndCheck(final SpreadsheetFormatter formatter,
                                final Object value,
                                final SpreadsheetText text) {
        this.formatAndCheck(formatter,
                value,
                this.createContext(),
                text);
    }

    // format fail and check

    default void formatFailAndCheck(final Object value) {
        this.formatFailAndCheck(value, this.createContext());
    }

    default void formatFailAndCheck(final Object value,
                                    final SpreadsheetFormatterContext context) {
        this.formatFailAndCheck(this.createFormatter(),
                value,
                context);
    }

    default void formatFailAndCheck(final SpreadsheetFormatter formatter,
                                    final Object value) {
        this.formatFailAndCheck(formatter,
                value,
                this.createContext());
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetFormatter.class.getSimpleName();
    }
}
