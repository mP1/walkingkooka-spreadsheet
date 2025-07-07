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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.TryingShortCircuitingConverter;
import walkingkooka.text.CharSequences;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * A {@link Converter} that converts uses the given {@link String pattern} to attempt to format given values.
 */
final class SpreadsheetConverterFormatPatternToString implements TryingShortCircuitingConverter<SpreadsheetConverterContext> {

    /**
     * Factory that creates a {@link SpreadsheetConverterFormatPatternToString}.
     */
    static SpreadsheetConverterFormatPatternToString with(final String pattern) {
        return new SpreadsheetConverterFormatPatternToString(
            CharSequences.failIfNullOrEmpty(pattern, "pattern")
        );
    }

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterFormatPatternToString(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return FORMATTER_TYPES.stream()
            .anyMatch(t -> context.canConvert(value, t));
    }

    /**
     * Values to be converted will be tested against this list assuming that a formatter is available and will format the value.
     */
    private final static List<Class<?>> FORMATTER_TYPES = Lists.of(
        LocalDate.class,
        LocalDateTime.class,
        LocalTime.class,
        Number.class,
        String.class
    );

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final SpreadsheetConverterContext context) {
        return SpreadsheetConverterFormatPatternToStringSpreadsheetValueVisitor.format(
            value,
            this.pattern,
            context
        );
    }

    /**
     * The pattern used to format values.
     */
    private final String pattern;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetConverterFormatPatternToString &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetConverterFormatPatternToString other) {
        return this.pattern.equals(other.pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
