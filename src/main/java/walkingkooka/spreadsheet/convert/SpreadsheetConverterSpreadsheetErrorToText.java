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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;

/**
 * A {@link Converter} that can convert {@link SpreadsheetError} to a {@link String} value.
 * This basically returns the {@link SpreadsheetErrorKind#text()}, giving text like <code>#ERROR</code>.
 */
final class SpreadsheetConverterSpreadsheetErrorToText extends SpreadsheetConverterSpreadsheetError {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetErrorToText INSTANCE = new SpreadsheetConverterSpreadsheetErrorToText();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterSpreadsheetErrorToText() {
    }

    @Override
    boolean canConvertSpreadsheetError(final SpreadsheetError error,
                                       final Class<?> type,
                                       final SpreadsheetConverterContext context) {
        return context.canConvert(
                null,
                String.class
        );
    }

    @Override
    <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                  final Class<T> type,
                                                  final SpreadsheetConverterContext context) {
        return context.convert(
                error.kind()
                        .text(),
                type
        );
    }

    @Override
    public String toString() {
        return "SpreadsheetError to text";
    }
}
