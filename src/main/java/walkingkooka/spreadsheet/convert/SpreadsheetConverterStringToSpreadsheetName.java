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
import walkingkooka.spreadsheet.SpreadsheetName;

/**
 * A {@link Converter} that converts an SpreadsheetName as a {@link String} into a {@link SpreadsheetName}.
 */
final class SpreadsheetConverterStringToSpreadsheetName extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterStringToSpreadsheetName INSTANCE = new SpreadsheetConverterStringToSpreadsheetName();

    private SpreadsheetConverterStringToSpreadsheetName() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof String && SpreadsheetName.class == type;
    }

    @Override
    <T> Either<T, String> convert0(final Object value,
                                   final Class<T> type,
                                   final SpreadsheetConverterContext context) {
        Either<T, String> result;

        try {
            result = this.successfulConversion(
                    SpreadsheetName.with(
                        context.convertOrFail(
                                value,
                                String.class
                        )
                    ),
                    type
            );
        } catch (final RuntimeException cause) {
            result = Either.right(cause.getMessage());
        }

        return result;
    }

    @Override
    public String toString() {
        return "String to " + SpreadsheetName.class.getSimpleName();
    }
}
