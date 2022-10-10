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
import walkingkooka.spreadsheet.SpreadsheetErrorException;

/**
 * A {@link Converter} that throws {@link SpreadsheetErrorException} with any given {@link SpreadsheetError}.
 * This is necessary so that functions which try and convert a {@link SpreadsheetError} fail early and eventually
 * return this {@link SpreadsheetError}.
 */
final class SpreadsheetErrorThrowingConverter extends SpreadsheetErrorConverter<ConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetErrorThrowingConverter INSTANCE = new SpreadsheetErrorThrowingConverter();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetErrorThrowingConverter() {
    }

    @Override
    boolean canConvertSpreadsheetError(final SpreadsheetError error,
                                       final Class<?> type,
                                       final ConverterContext context) {
        return true;
    }

    @Override
    <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                  final Class<T> type,
                                                  final ConverterContext context) {
        throw new SpreadsheetErrorException(
                error.isMissingCell() ?
                        error.setNameString() :
                        error
        );
    }

    @Override
    String toStringType() {
        return "throws";
    }
}
