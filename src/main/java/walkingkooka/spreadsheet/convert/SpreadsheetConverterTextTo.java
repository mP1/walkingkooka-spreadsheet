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
import walkingkooka.tree.expression.Expression;

/**
 * A {@link Converter} that converts an expression as a {@link String} into a {@link Expression}.
 */
abstract class SpreadsheetConverterTextTo extends SpreadsheetConverter {

    SpreadsheetConverterTextTo() {
        super();
    }

    @Override
    public final boolean canConvert(final Object value,
                                    final Class<?> type,
                                    final SpreadsheetConverterContext context) {
        // test isType is quicker so do first
        return this.isType(
                value,
                type,
                context
        ) &&
                context.canConvert(
                        value,
                        String.class
                );
    }

    abstract boolean isType(final Object value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context);

    @Override
    final <T> Either<T, String> convert0(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        Either<T, String> result;

        try {
            result = this.successfulConversion(
                    this.tryConvert(
                            context.convertOrFail(
                                    value,
                                    String.class
                            ),
                            type,
                            context
                    ),
                    type
            );
        } catch (final RuntimeException cause) {
            result = Either.right(cause.getMessage());
        }

        return result;
    }

    abstract Object tryConvert(final String value,
                               final Class<?> type,
                               final SpreadsheetConverterContext context);
}
