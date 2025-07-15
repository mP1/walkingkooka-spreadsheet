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

package walkingkooka.spreadsheet.format;

import walkingkooka.Context;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.HasSpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

/**
 * A {@link Context} that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetFormatterContext extends SpreadsheetConverterContext,
    HasSpreadsheetCell {

    /**
     * The width of the "cell" in characters.
     * This value affects STAR operator.
     */
    int cellCharacterWidth();

    /**
     * Returns the {@link Color} with the given number.
     */
    Optional<Color> colorNumber(final int number);

    /**
     * Returns the {@link Color} with the given name.
     */
    Optional<Color> colorName(final SpreadsheetColorName name);

    /**
     * Formats the given value using the default {@link SpreadsheetFormatter}.
     */
    Optional<TextNode> formatValue(final Optional<Object> value);

    /**
     * Formats the given {@link Object value} or if formatting fails returns {@link SpreadsheetText#EMPTY}.
     */
    default TextNode formatValueOrEmptyText(final Optional<Object> value) {
        return this.formatValue(value)
            .orElse(TextNode.EMPTY_TEXT);
    }

    /**
     * A useful default number of digits constant
     */
    int DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT = 9;

    /**
     * Returns the number of digits when formatting a number.
     */
    int generalFormatNumberDigitCount();

    /**
     * Creates a {@link SpreadsheetExpressionEvaluationContext} which will hold the given value supporting
     * the execution of an {@link walkingkooka.tree.expression.Expression}.
     */
    SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value);

    /**
     * Helper that gets a {@link SpreadsheetFormatter} given its a {@link SpreadsheetFormatterSelector}.
     */
    SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector);

    /**
     * A {@link SpreadsheetFormatterContext} will never need the {@link SpreadsheetExpressionReference} being validated.
     */
    @Override
    default SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetConverterContext......................................................................................

    @Override
    SpreadsheetFormatterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);
}
