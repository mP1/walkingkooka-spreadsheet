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
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;

/**
 * A {@link Converter} that converts an expression as a {@link String} into a {@link Expression}.
 */
final class SpreadsheetConverterStringToExpression extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterStringToExpression INSTANCE = new SpreadsheetConverterStringToExpression();

    private SpreadsheetConverterStringToExpression() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof String && Expression.class == type;
    }

    @Override
    <T> Either<T, String> convert0(final Object value,
                                   final Class<T> type,
                                   final SpreadsheetConverterContext context) {
        Either<T, String> result;

        try {
            result = this.successfulConversion(
                    SpreadsheetFormulaParsers.expression()
                            .parseText(
                                    value.toString(),
                                    SpreadsheetParserContexts.basic(
                                            context,
                                            context,
                                            ';' // valueSeparator
                                    )
                            ).cast(SpreadsheetFormulaParserToken.class)
                            .toExpression(
                                    new FakeSpreadsheetExpressionEvaluationContext() {
                                        @Override
                                        public ExpressionNumberKind expressionNumberKind() {
                                            return context.expressionNumberKind();
                                        }
                                    }
                            ).get()
                    ,
                    type
            );
        } catch (final RuntimeException cause) {
            result = Either.right(cause.getMessage());
        }

        return result;
    }

    @Override
    public String toString() {
        return "String to " + Expression.class.getSimpleName();
    }
}
