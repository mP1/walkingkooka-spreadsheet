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
 * A {@link Converter} that converts {@link Boolean} to {@link String}.
 */
final class SpreadsheetConverterBooleanToText extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterBooleanToText INSTANCE = new SpreadsheetConverterBooleanToText();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterBooleanToText() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof Boolean &&
            String.class == type;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        return this.convertBooleanToString(
            (Boolean) value,
            type,
            context
        );
    }

    public <T> Either<T, String> convertBooleanToString(final Boolean booleanValue,
                                                        final Class<T> type,
                                                        final SpreadsheetConverterContext context) {
        return this.successfulConversion(
            Boolean.TRUE.equals(booleanValue) ?
                "TRUE" :
                "FALSE",
            type
        );
    }

    @Override
    public String toString() {
        return "Boolean to String";
    }
}
