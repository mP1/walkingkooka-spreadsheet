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

/**
 * A {@link Converter} that only supports converting values to {@link String}. Numbers are formatted to {@link String}
 * using a simple pattern, ignoring any number format pattern.
 */
final class UnformattedNumberSpreadsheetConverter implements Converter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static UnformattedNumberSpreadsheetConverter INSTANCE = new UnformattedNumberSpreadsheetConverter();

    private UnformattedNumberSpreadsheetConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return String.class == type;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return this.canConvert(value, type, context) ?
                this.successfulConversion(
                        UnformattedNumberSpreadsheetConverterSpreadsheetValueVisitor.convertToString(
                                value,
                                context
                        ),
                        type
                ) :
                this.failConversion(value, type);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
