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
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

/**
 * Context that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetEngineContext extends Context {

    /**
     * Parses the formula into an {@link ExpressionNode}.
     */
    SpreadsheetParserToken parseFormula(final String formula);

    /**
     * Evaluates the expression into a value.
     */
    Object evaluate(final ExpressionNode node);

    /**
     * Converts the value into the target type.
     */
    <T> T convert(Object value, Class<T> target);

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
