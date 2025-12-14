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
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;

/**
 * A {@link Converter} that converts an expression as a {@link String} into a {@link Expression}.
 */
final class SpreadsheetConverterTextToExpression extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToExpression INSTANCE = new SpreadsheetConverterTextToExpression();

    private SpreadsheetConverterTextToExpression() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return Expression.class == type;
    }

    @Override
    public Expression parseText(final String value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return SpreadsheetFormulaParsers.expression()
            .parseText(
                value,
                SpreadsheetParserContexts.basic(
                    InvalidCharacterExceptionFactory.POSITION,
                    context, // DateTimeContext
                    context, // DecimalNumberContext
                    context.valueSeparator()
                )
            ).cast(SpreadsheetFormulaParserToken.class)
            .toExpressionOrFail(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public ExpressionNumberKind expressionNumberKind() {
                        return context.expressionNumberKind();
                    }
                }
            );
    }

    @Override
    public String toString() {
        return "String to " + Expression.class.getSimpleName();
    }
}
