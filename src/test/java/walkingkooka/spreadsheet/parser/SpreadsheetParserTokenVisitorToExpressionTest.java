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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserTokenVisitorToExpressionTest extends SpreadsheetParserTokenVisitorTestCase<SpreadsheetParserTokenVisitorToExpression> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    @Test
    public void testNullExpressionNumberKindFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetParserTokenVisitorToExpression.toExpression(
                        SpreadsheetParserToken.number(
                                Lists.of(
                                        SpreadsheetParserToken.digits("1", "1")
                                ),
                                "1"),
                        null));
    }

    @Test
    public void testCellReference() {
        this.toExpressionAndCheck(
                SpreadsheetParserToken.cellReference(
                        Lists.of(
                                SpreadsheetParserToken.columnReference(
                                        SpreadsheetSelection.parseColumn("A"),
                                        "A"
                                ),
                                SpreadsheetParserToken.rowReference(
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
                SpreadsheetParserToken.cellRange(
                        Lists.of(
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("A"),
                                                        "A"
                                                ),
                                                SpreadsheetParserToken.rowReference(
                                                        SpreadsheetSelection.parseRow("1"),
                                                        "1"
                                                )
                                        ),
                                        "A1"
                                ),
                                SpreadsheetParserToken.cellReference(
                                        Lists.of(
                                                SpreadsheetParserToken.columnReference(
                                                        SpreadsheetSelection.parseColumn("B"),
                                                        "B"
                                                ),
                                                SpreadsheetParserToken.rowReference(
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
                SpreadsheetParserToken.error(
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
                SpreadsheetParserToken.error(
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
                            public Optional<ExpressionFunction<?, ExpressionEvaluationContext>> expressionFunction(final FunctionExpressionName name) {
                                Objects.requireNonNull(FunctionExpressionName.with("error"), "name");
                                return Cast.to(
                                        SpreadsheetExpressionFunctions.error()
                                );
                            }

                            @Override
                            public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                                           final List<Object> parameters) {
                                final List<Object> prepared = parameters.stream()
                                        .map(v -> {
                                            final Expression ve = (Expression) v;
                                            return ve.toValue(this);
                                        }).collect(Collectors.toList());

                                return function.apply(
                                        prepared,
                                        Cast.to(this)
                                );
                            }
                        }),
                "expression.toValue"
        );
    }

    private void toExpressionAndCheck(final SpreadsheetParserToken token,
                                      final Expression expression) {
        this.checkEquals(
                Optional.of(
                        expression
                ),
                toExpression(token)
        );
    }

    private static Optional<Expression> toExpression(final SpreadsheetParserToken token) {
        return SpreadsheetParserTokenVisitorToExpression.toExpression(
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
    public SpreadsheetParserTokenVisitorToExpression createVisitor() {
        return new SpreadsheetParserTokenVisitorToExpression(null);
    }

    @Override
    public Class<SpreadsheetParserTokenVisitorToExpression> type() {
        return SpreadsheetParserTokenVisitorToExpression.class;
    }
}
