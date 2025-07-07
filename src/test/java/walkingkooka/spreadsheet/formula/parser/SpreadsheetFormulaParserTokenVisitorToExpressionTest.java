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

package walkingkooka.spreadsheet.formula.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunction;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormulaParserTokenVisitorToExpressionTest extends SpreadsheetFormulaParserTokenVisitorTestCase<SpreadsheetFormulaParserTokenVisitorToExpression> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    @Test
    public void testNullExpressionNumberKindFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormulaParserTokenVisitorToExpression.toExpression(
                SpreadsheetFormulaParserToken.number(
                    Lists.of(
                        SpreadsheetFormulaParserToken.digits("1", "1")
                    ),
                    "1"),
                null));
    }

    @Test
    public void testCell() {
        this.toExpressionAndCheck(
            SpreadsheetFormulaParserToken.cell(
                Lists.of(
                    SpreadsheetFormulaParserToken.column(
                        SpreadsheetSelection.parseColumn("A"),
                        "A"
                    ),
                    SpreadsheetFormulaParserToken.row(
                        SpreadsheetSelection.parseRow("1"),
                        "1"
                    )
                ),
                "A1"
            ),
            Expression.reference(
                SpreadsheetSelection.A1
            )
        );
    }

    @Test
    public void testCellRange() {
        this.toExpressionAndCheck(
            SpreadsheetFormulaParserToken.cellRange(
                Lists.of(
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            SpreadsheetFormulaParserToken.column(
                                SpreadsheetSelection.parseColumn("A"),
                                "A"
                            ),
                            SpreadsheetFormulaParserToken.row(
                                SpreadsheetSelection.parseRow("1"),
                                "1"
                            )
                        ),
                        "A1"
                    ),
                    SpreadsheetFormulaParserToken.cell(
                        Lists.of(
                            SpreadsheetFormulaParserToken.column(
                                SpreadsheetSelection.parseColumn("B"),
                                "B"
                            ),
                            SpreadsheetFormulaParserToken.row(
                                SpreadsheetSelection.parseRow("2"),
                                "2"
                            )
                        ),
                        "A1"
                    )
                ),
                "A1:B2"
            ),
            Expression.reference(
                SpreadsheetSelection.parseCellRange("A1:B2")
            )
        );
    }

    @Test
    public void testError() {
        final SpreadsheetError error = SpreadsheetErrorKind.NAME.toError();

        this.toExpressionAndCheck(
            SpreadsheetFormulaParserToken.error(
                error,
                error.kind().text()
            ),
            Expression.value(error)
        );
    }

    @Test
    public void testErrorAndToValue() {
        final SpreadsheetError error = SpreadsheetErrorKind.NAME.toError();
        final Optional<Expression> maybeExpression = toExpression(
            SpreadsheetFormulaParserToken.error(
                error,
                error.kind().text()
            )
        );

        this.checkNotEquals(
            Optional.empty(),
            maybeExpression
        );

        final Expression expression = maybeExpression.get();

        this.checkEquals(
            error,
            expression.toValue(
                new FakeSpreadsheetExpressionEvaluationContext() {

                    @Override
                    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
                        Objects.requireNonNull(
                            SpreadsheetExpressionFunctions.name("error"),
                            "name"
                        );
                        return Cast.to(
                            SpreadsheetExpressionFunctions.error()
                        );
                    }
                }),
            "expression.toValue"
        );
    }

    @Test
    public void testTemplateValueName() {
        final String text = "template-value-name123";
        final TemplateValueName template = TemplateValueName.with(text);

        this.toExpressionAndCheck(
            SpreadsheetFormulaParserToken.templateValueName(
                template,
                text
            ),
            Expression.reference(template)
        );
    }

    private void toExpressionAndCheck(final SpreadsheetFormulaParserToken token,
                                      final Expression expression) {
        this.checkEquals(
            Optional.of(
                expression
            ),
            toExpression(token)
        );
    }

    private static Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        return SpreadsheetFormulaParserTokenVisitorToExpression.toExpression(
            token,
            new FakeExpressionEvaluationContext() {
                @Override
                public ExpressionNumberKind expressionNumberKind() {
                    return EXPRESSION_NUMBER_KIND;
                }
            }
        );
    }

    @Override
    public SpreadsheetFormulaParserTokenVisitorToExpression createVisitor() {
        return new SpreadsheetFormulaParserTokenVisitorToExpression(null);
    }

    @Override
    public Class<SpreadsheetFormulaParserTokenVisitorToExpression> type() {
        return SpreadsheetFormulaParserTokenVisitorToExpression.class;
    }
}
