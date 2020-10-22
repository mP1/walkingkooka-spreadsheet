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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Context;
import walkingkooka.Either;
import walkingkooka.convert.ConvertOrFailFunction;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;

/**
 * Context that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetEngineContext extends Context, ConvertOrFailFunction {

    /**
     * Parses the formula into an {@link Expression}.
     */
    SpreadsheetParserToken parseFormula(final String formula);

    /**
     * Evaluates the expression into a value.
     */
    Object evaluate(final Expression node);

    /**
     * Converts the value into the target type.
     */
    <T> Either<T, String> convert(Object value, Class<T> target);

    /**
     * Converts the given value to the {@link Class target type} or throws a {@link SpreadsheetEngineException}
     */
    default <T> T convertOrFail(final Object value,
                                final Class<T> target) {
        final Either<T, String> converted = this.convert(value, target);
        if (converted.isRight()) {
            throw new SpreadsheetEngineException(converted.rightValue());
        }

        return converted.leftValue();
    }

    /**
     * Accepts a pattern and returns the equivalent {@link SpreadsheetFormatter}.
     */
    SpreadsheetFormatter parsePattern(final String pattern);

    /**
     * The default {@link SpreadsheetFormatter} when no pattern is available for a cell.
     */
    SpreadsheetFormatter defaultSpreadsheetFormatter();

    /**
     * Formats the given value using the provided formatter.
     */
    Optional<SpreadsheetText> format(final Object value,
                                     final SpreadsheetFormatter formatter);
}
