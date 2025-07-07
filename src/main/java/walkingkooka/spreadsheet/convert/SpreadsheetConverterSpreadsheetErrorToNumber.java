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
import walkingkooka.convert.Converters;
import walkingkooka.math.Maths;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionNumber;

/**
 * A {@link Converter} that converts {@link SpreadsheetError} with a {@link SpreadsheetErrorKind#NAME} for a missing cell
 * to a value of zero.
 */
final class SpreadsheetConverterSpreadsheetErrorToNumber extends SpreadsheetConverterSpreadsheetError {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetErrorToNumber INSTANCE = new SpreadsheetConverterSpreadsheetErrorToNumber();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterSpreadsheetErrorToNumber() {
    }

    @Override
    boolean canConvertSpreadsheetError(final SpreadsheetError error,
                                       final Class<?> type,
                                       final SpreadsheetConverterContext context) {
        return ExpressionNumber.isClass(type) &&
            (
                error.isMissingCell() ||
                    error.kind() == SpreadsheetErrorKind.NULL
            );
    }

    @Override
    <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                  final Class<T> type,
                                                  final SpreadsheetConverterContext context) {
        return Maths.isNumberClass(type) ?
            NUMBER_TO_NUMBER.convert(
                0,
                type,
                context
            ) :
            this.successfulConversion(
                context.missingCellNumberValue(),
                type
            );
    }

    private final static Converter<SpreadsheetConverterContext> NUMBER_TO_NUMBER = Converters.numberToNumber();

    @Override
    public String toString() {
        return "SpreadsheetError to Number";
    }
}
