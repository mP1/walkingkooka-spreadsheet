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

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

/**
 * A {@link Converter} that converts an ExpressionFunctionSelector as a {@link String} into a {@link ExpressionFunctionSelector}.
 */
final class SpreadsheetConverterTextToExpressionFunctionSelector extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToExpressionFunctionSelector INSTANCE = new SpreadsheetConverterTextToExpressionFunctionSelector();

    private SpreadsheetConverterTextToExpressionFunctionSelector() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return ExpressionFunctionSelector.class == type;
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return SpreadsheetExpressionFunctions.parseSelector(value);
    }

    @Override
    public String toString() {
        return TEXT + " to " + ExpressionFunctionSelector.class.getSimpleName();
    }
}
