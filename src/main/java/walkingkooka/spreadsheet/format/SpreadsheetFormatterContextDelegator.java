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

import walkingkooka.color.Color;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

/**
 * Simplifies implementing a delegate to a wrapped {@link SpreadsheetFormatterContext}.
 */
public interface SpreadsheetFormatterContextDelegator extends SpreadsheetFormatterContext,
    SpreadsheetConverterContextDelegator {

    @Override
    default SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetFormatterContext();
    }

    @Override
    default int cellCharacterWidth() {
        return this.spreadsheetFormatterContext()
            .cellCharacterWidth();
    }

    @Override
    default Optional<Color> colorNumber(final int number) {
        return this.spreadsheetFormatterContext()
            .colorNumber(number);
    }

    @Override
    default Optional<Color> colorName(final SpreadsheetColorName name) {
        return this.spreadsheetFormatterContext()
            .colorName(name);
    }

    @Override
    default Optional<TextNode> format(final Optional<Object> value) {
        return this.spreadsheetFormatterContext()
            .format(value);
    }

    @Override
    default TextNode formatOrEmptyText(final Optional<Object> value) {
        return this.spreadsheetFormatterContext()
            .formatOrEmptyText(value);
    }

    @Override
    default int generalFormatNumberDigitCount() {
        return this.spreadsheetFormatterContext()
            .generalFormatNumberDigitCount();
    }

    @Override
    default SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value) {
        return this.spreadsheetFormatterContext()
            .spreadsheetExpressionEvaluationContext(value);
    }

    @Override
    default SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        return this.spreadsheetFormatterContext()
            .spreadsheetFormatter(selector);
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetFormatterContext()
            .spreadsheetMetadata();
    }

    @Override
    default SpreadsheetExpressionReference validationReference() {
        return SpreadsheetFormatterContext.super.validationReference();
    }

    SpreadsheetFormatterContext spreadsheetFormatterContext();
}
