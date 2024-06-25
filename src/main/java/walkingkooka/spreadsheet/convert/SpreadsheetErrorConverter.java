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
import walkingkooka.convert.ConverterContext;
import walkingkooka.spreadsheet.SpreadsheetError;

/**
 * Base class for converters that convert {@link SpreadsheetError} to some other value or type.
 */
abstract class SpreadsheetErrorConverter<C extends ConverterContext> implements Converter<C> {

    /**
     * Private ctor use singleton.
     */
    SpreadsheetErrorConverter() {
    }

    @Override
    public final boolean canConvert(final Object value,
                                    final Class<?> type,
                                    final C context) {
        return value instanceof SpreadsheetError &&
                this.canConvertSpreadsheetError(
                        (SpreadsheetError) value,
                        type
                );
    }

    abstract boolean canConvertSpreadsheetError(final SpreadsheetError error,
                                                final Class<?> type);

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final C context) {
        return this.canConvert(value, type, context) ?
                this.convertSpreadsheetError(
                        (SpreadsheetError) value,
                        type,
                        context
                ) :
                this.failConversion(value, type);
    }

    abstract <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                           final Class<T> type,
                                                           final C context);
}
