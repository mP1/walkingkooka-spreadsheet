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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link walkingkooka.convert.Converter} that handles converting {@link Boolean}, {@link LocalDate}, {@link LocalDateTime},
 * {@link String} to a {@link Number}.
 * Note that converting {@link Number} to another {@link Number} type is not supported by this {@link Converter}.
 */
final class SpreadsheetConverterToNumber extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterToNumber INSTANCE = new SpreadsheetConverterToNumber();

    private SpreadsheetConverterToNumber() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (value instanceof Boolean ||
            value instanceof LocalDate ||
            value instanceof LocalDateTime ||
            value instanceof CharSequence) &&
            (Number.class == type || ExpressionNumber.isClass(type));
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverterToNumberSpreadsheetValueVisitor.converter(value);
        if(null == converter) {
            throw new IllegalArgumentException("Converter missing for " + value);
        }
        return converter.convert(
            value,
            type,
            context
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "to number";
    }
}
