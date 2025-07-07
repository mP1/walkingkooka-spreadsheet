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
import walkingkooka.spreadsheet.SpreadsheetError;

/**
 * Base class for converters that convert {@link SpreadsheetError} to some other value or type.
 */
abstract class SpreadsheetConverterSpreadsheetError extends SpreadsheetConverter {

    /**
     * Private ctor use singleton.
     */
    SpreadsheetConverterSpreadsheetError() {
    }

    @Override
    public final boolean canConvert(final Object value,
                                    final Class<?> type,
                                    final SpreadsheetConverterContext context) {
        return value instanceof SpreadsheetError &&
            this.canConvertSpreadsheetError(
                (SpreadsheetError) value,
                type,
                context
            );
    }

    abstract boolean canConvertSpreadsheetError(final SpreadsheetError error,
                                                final Class<?> type,
                                                final SpreadsheetConverterContext context);

    @Override
    public final <T> Either<T, String> doConvert(final Object value,
                                                 final Class<T> targetType,
                                                 final SpreadsheetConverterContext context) {
        return this.convertSpreadsheetError(
            (SpreadsheetError) value,
            targetType,
            context
        );
    }

    abstract <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                           final Class<T> type,
                                                           final SpreadsheetConverterContext context);
}
