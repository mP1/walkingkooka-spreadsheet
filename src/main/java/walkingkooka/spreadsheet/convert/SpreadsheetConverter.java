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
 * A {@link Converter} that includes a canConvert guard before calling an abstract template convertXXX method.
 */
abstract class SpreadsheetConverter implements Converter<SpreadsheetConverterContext> {

    SpreadsheetConverter() {
        super();
    }

    @Override
    public final <T> Either<T, String> convert(final Object value,
                                               final Class<T> targetType,
                                               final SpreadsheetConverterContext context) {
        return this.canConvert(
                value,
                targetType,
                context
        ) ?
                this.convert0(
                        value,
                        targetType,
                        context
                ) :
                this.failConversion(
                        value,
                        targetType
                );
    }

    abstract <T> Either<T, String> convert0(final Object value,
                                            final Class<T> targetType,
                                            final SpreadsheetConverterContext context);

    @Override
    public abstract String toString();
}
