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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.text.CharSequences;

import java.util.Optional;

/**
 * A {@link Converter} which formats a value to {@link String text} using the given {@link SpreadsheetFormatter}.
 * Note attempts by the {@link SpreadsheetFormatter} to evaluate an expression will fail.
 */
final class SpreadsheetFormatterConverter implements Converter<SpreadsheetConverterContext> {

    static SpreadsheetFormatterConverter with(final SpreadsheetFormatter formatter) {
        return new SpreadsheetFormatterConverter(formatter);
    }

    private SpreadsheetFormatterConverter(final SpreadsheetFormatter formatter) {
        super();

        this.formatter = formatter;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return this.convert(
                value,
                type,
                context
        ).isLeft();
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return this.formatter.format(
                        Optional.ofNullable(value),
                        SpreadsheetFormatterConverterSpreadsheetFormatterContext.with(context))
                .map(
                        t -> this.successfulConversion(
                                t.text(),
                                type
                        )
                )
                .orElse(Either.<T, String>right("Unable to convert " + CharSequences.quoteIfChars(value) + " to " + type.getName()));
    }

    private final SpreadsheetFormatter formatter;

    @Override
    public String toString() {
        return this.formatter.toString();
    }
}
