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

package walkingkooka.spreadsheet.expression;

import walkingkooka.Cast;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyValue;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.tree.expression.AddExpression;
import walkingkooka.tree.expression.AndExpression;
import walkingkooka.tree.expression.BinaryExpression;
import walkingkooka.tree.expression.CallExpression;
import walkingkooka.tree.expression.CompareExpression;
import walkingkooka.tree.expression.DivideExpression;
import walkingkooka.tree.expression.EqualsExpression;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionVisitor;
import walkingkooka.tree.expression.GreaterThanEqualsExpression;
import walkingkooka.tree.expression.GreaterThanExpression;
import walkingkooka.tree.expression.LambdaFunctionExpression;
import walkingkooka.tree.expression.LessThanEqualsExpression;
import walkingkooka.tree.expression.LessThanExpression;
import walkingkooka.tree.expression.ListExpression;
import walkingkooka.tree.expression.ModuloExpression;
import walkingkooka.tree.expression.MultiplyExpression;
import walkingkooka.tree.expression.NamedFunctionExpression;
import walkingkooka.tree.expression.NegativeExpression;
import walkingkooka.tree.expression.NotEqualsExpression;
import walkingkooka.tree.expression.NotExpression;
import walkingkooka.tree.expression.OrExpression;
import walkingkooka.tree.expression.PowerExpression;
import walkingkooka.tree.expression.ReferenceExpression;
import walkingkooka.tree.expression.SubtractExpression;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.VariableExpression;
import walkingkooka.tree.expression.XorExpression;
import walkingkooka.visit.Visiting;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Evaluates the given {@link Expression}, with special checks for {@link CurrencyValue}. If currency value is found for
 * any {@link walkingkooka.tree.expression.LogicalExpression}, {@link NotExpression}, and exception will be thrown.
 */
final class SpreadsheetExpressionEvaluationContextExpressionVisitor extends ExpressionVisitor {

    static Object evaluate(final Expression expression,
                           final SpreadsheetExpressionEvaluationContext context) {
        return new SpreadsheetExpressionEvaluationContextExpressionVisitor(context)
            .evaluateExpression(expression);
    }

    // @VisibleForTesting
    SpreadsheetExpressionEvaluationContextExpressionVisitor(final SpreadsheetExpressionEvaluationContext context) {
        super();
        this.context = context;
    }

    private Object evaluateExpression(final Expression expression) {
        this.accept(expression);
        return this.value;
    }

    // child............................................................................................................

    @Override
    protected void visit(final NamedFunctionExpression expression) {
        this.evaluateChild(expression);
    }

    @Override
    protected void visit(final ReferenceExpression expression) {
        this.evaluateChild(expression);
    }

    @Override
    protected void visit(final ValueExpression<?> expression) {
        this.evaluateChild(expression);
    }

    private void evaluateChild(final Expression expression) {
        this.value = expression.toValue(this.context);
    }

    // Unary............................................................................................................

    @Override
    protected Visiting startVisit(final LambdaFunctionExpression expression) {
        this.value = expression.apply(
            this.evaluateExpression(
                expression.value()
            ),
            this.context
        );
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NegativeExpression expression) {
        final Object in = this.evaluateExpression(
            expression.value()
        );

        final CurrencyValue currencyValue = in instanceof CurrencyValue ?
            (CurrencyValue) in :
            null;

        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object out = expression.apply(
            null != currencyValue ?
                currencyValue.value() :
                in,
            context
        );

        this.value = null != currencyValue ?
            currencyValue.setValue(
                this.toExpressionNumber(out)
            ) :
            out;

        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NotExpression expression) {
        final Object value = this.evaluateExpression(
            expression.value()
        );
        failIfCurrencyValue(value);

        this.value = expression.apply(
            value,
            this.context
        );

        return Visiting.SKIP;
    }

    private static void failIfCurrencyValue(final Object value) {
        if (value instanceof CurrencyValue) {
            throw SpreadsheetErrorKind.VALUE.setMessageAndValue("Invalid parameter type CurrencyValue", value)
                .exception();
        }
    }

    // BinaryNotCurrency................................................................................................

    @Override
    protected Visiting startVisit(final AndExpression expression) {
        this.evaluateBinaryNotCurrencyValue(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final OrExpression expression) {
        this.evaluateBinaryNotCurrencyValue(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final XorExpression expression) {
        this.evaluateBinaryNotCurrencyValue(expression);
        return Visiting.SKIP;
    }

    /**
     * Logical operators AND, OR and XOR will fail if any parameter is a {@link CurrencyValue}.
     */
    private void evaluateBinaryNotCurrencyValue(final BinaryExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        failIfCurrencyValue(left);

        final Object right = context.evaluateExpression(
            expression.right()
        );

        failIfCurrencyValue(right);

        this.value = expression.apply(
            left,
            right,
            context
        );
    }

    // add/sub..........................................................................................................

    @Override
    protected Visiting startVisit(final AddExpression expression) {
        this.evaluateBinaryNoneEitherBothCurrencyValue(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SubtractExpression expression) {
        this.evaluateBinaryNoneEitherBothCurrencyValue(expression);
        return Visiting.SKIP;
    }

    /**
     * Evaluate the left and right value values of the given {@link BinaryExpression}, unwrapping any {@link CurrencyValue}
     * and then invoking the {@link BinaryExpression#apply(Object, Object, ExpressionEvaluationContext)}, then
     * re-wrapping.
     */
    private void evaluateBinaryNoneEitherBothCurrencyValue(final BinaryExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        final Object right = context.evaluateExpression(
            expression.right()
        );

        final boolean leftCurrency = left instanceof CurrencyValue;
        final boolean rightCurrency = right instanceof CurrencyValue;

        Object value;

        if (leftCurrency || rightCurrency) {
            CurrencyValue leftCurrencyValue;
            CurrencyValue rightCurrencyValue;

            if (leftCurrency && rightCurrency) {
                leftCurrencyValue = (CurrencyValue) left;
                rightCurrencyValue = (CurrencyValue) right;
            } else {
                // convert the other to a CurrencyValue
                if (leftCurrency) {
                    leftCurrencyValue = (CurrencyValue) left;
                    rightCurrencyValue = CurrencyValue.with(
                        context.convertOrFail(
                            right,
                            Number.class
                        ),
                        context.currencyCode()
                    );
                } else {
                    rightCurrencyValue = (CurrencyValue) right;
                    leftCurrencyValue = CurrencyValue.with(
                        context.convertOrFail(
                            left,
                            Number.class
                        ),
                        context.currencyCode()
                    );
                }
            }

            final CurrencyCode leftCurrencyCode = leftCurrencyValue.currencyCode();
            final CurrencyCode rightCurrencyCode = rightCurrencyValue.currencyCode();

            final Number leftNumberValue = leftCurrencyValue.value();
            Number rightNumberValue;

            if (leftCurrencyCode.equals(rightCurrencyCode)) {
                rightNumberValue = rightCurrencyValue.value();
            } else {
                // convert right to left currency
                final Number exchangeRate = context.currencyExchangeRateOrFail(
                    CurrencyExchange.with(
                        rightCurrencyCode, // from
                        leftCurrencyCode // to
                    ),
                    Optional.empty() // no datetime
                );

                rightNumberValue = context.multiplyToExpressionNumber(
                    rightCurrencyValue.value(),
                    exchangeRate
                );
            }

            // both numbers are the same currency perform ADD/SUB
            value = leftCurrencyValue.setValue(
                (Number)
                    expression.apply(
                        leftNumberValue,
                        rightNumberValue,
                        context
                    )
            );

        } else {
            // neither is a CurrencyValue perform ADD/SUB
            value = expression.apply(
                left,
                right,
                context
            );
        }

        this.value = value;
    }

    // divide/modulo/power..............................................................................................

    @Override
    protected Visiting startVisit(final DivideExpression expression) {
        this.evaluateBinaryWithMaybeLeftCurrencyValue(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final ModuloExpression expression) {
        this.evaluateBinaryWithMaybeLeftCurrencyValue(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final PowerExpression expression) {
        this.evaluateBinaryWithMaybeLeftCurrencyValue(expression);
        return Visiting.SKIP;
    }

    /**
     * Supports unwrapping {@link CurrencyValue} and performing the divide or mod. The right value cannot be a {@link CurrencyValue}.
     */
    private void evaluateBinaryWithMaybeLeftCurrencyValue(final BinaryExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        final Object right = context.evaluateExpression(
            expression.right()
        );

        final boolean leftCurrency = left instanceof CurrencyValue;

        failIfCurrencyValue(right);

        Object value;

        if (leftCurrency) {
            value = evaluateBinaryWithOneCurrencyValue(
                expression,
                (CurrencyValue) left,
                right
            );
        } else {
            value = expression.apply(
                left,
                right,
                context
            );
        }

        this.value = value;
    }

    private Object evaluateBinaryWithOneCurrencyValue(final BinaryExpression expression,
                                                      final CurrencyValue currencyValue,
                                                      final Object otherValue) {
        return currencyValue.setValue(
            (Number)
                expression.apply(
                currencyValue.value(),
                otherValue,
                this.context
            )
        );
    }

    // multiply.........................................................................................................

    @Override
    protected Visiting startVisit(final MultiplyExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        final Object right = context.evaluateExpression(
            expression.right()
        );

        final boolean leftCurrency = left instanceof CurrencyValue;

        if(leftCurrency) {
            failIfCurrencyValue(right);
        }

        final boolean rightCurrency = right instanceof CurrencyValue;

        Object value;

        if(leftCurrency) {
            value = evaluateBinaryWithOneCurrencyValue(
                expression,
                (CurrencyValue) left,
                right
            );
        } else {
            if(rightCurrency) {
                value = evaluateBinaryWithOneCurrencyValue(
                    expression,
                    (CurrencyValue) right,
                    left
                );
            } else {
                value = expression.apply(
                    left,
                    right,
                    context
                );
            }
        }

        this.value = value;
        return Visiting.SKIP;
    }

    // EqualExpression | NotEqualExpression.............................................................................

    @Override
    protected Visiting startVisit(final EqualsExpression expression) {
        this.evaluateBinaryNoneEitherBothCurrencyValueBoolean(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NotEqualsExpression expression) {
        this.evaluateBinaryNoneEitherBothCurrencyValueBoolean(expression);
        return Visiting.SKIP;
    }

    private void evaluateBinaryNoneEitherBothCurrencyValueBoolean(final BinaryExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        final Object right = context.evaluateExpression(
            expression.right()
        );

        final boolean leftCurrency = left instanceof CurrencyValue;
        final boolean rightCurrency = right instanceof CurrencyValue;

        Object value;

        if (leftCurrency || rightCurrency) {
            CurrencyValue leftCurrencyValue;
            CurrencyValue rightCurrencyValue;

            if (leftCurrency && rightCurrency) {
                leftCurrencyValue = (CurrencyValue) left;
                rightCurrencyValue = (CurrencyValue) right;
            } else {
                // convert the other to a CurrencyValue
                if (leftCurrency) {
                    leftCurrencyValue = (CurrencyValue) left;
                    rightCurrencyValue = CurrencyValue.with(
                        context.convertOrFail(
                            right,
                            Number.class
                        ),
                        context.currencyCode()
                    );
                } else {
                    rightCurrencyValue = (CurrencyValue) right;
                    leftCurrencyValue = CurrencyValue.with(
                        context.convertOrFail(
                            left,
                            Number.class
                        ),
                        context.currencyCode()
                    );
                }
            }

            final CurrencyCode leftCurrencyCode = leftCurrencyValue.currencyCode();
            final CurrencyCode rightCurrencyCode = rightCurrencyValue.currencyCode();

            final Number leftNumberValue = leftCurrencyValue.value();
            Number rightNumberValue;

            if (leftCurrencyCode.equals(rightCurrencyCode)) {
                rightNumberValue = rightCurrencyValue.value();
            } else {
                // convert right to left currency
                final Number exchangeRate = context.currencyExchangeRateOrFail(
                    CurrencyExchange.with(
                        rightCurrencyCode, // from
                        leftCurrencyCode // to
                    ),
                    Optional.empty() // no datetime
                );

                rightNumberValue = context.multiplyToExpressionNumber(
                    rightCurrencyValue.value(),
                    exchangeRate
                );
            }

            // both numbers are the same currency perform comparison
            value = expression.apply(
                leftNumberValue,
                rightNumberValue,
                context
            );

        } else {
            // neither is a CurrencyValue perform eq/ne/gt/gte/lt/lte
            value = expression.apply(
                left,
                right,
                context
            );
        }

        this.value = value;
    }

    // GreaterXXXExpression | LessXXXExpression.........................................................................

    @Override
    protected Visiting startVisit(final GreaterThanEqualsExpression expression) {
        this.evaluateBinaryLogicalExpression(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GreaterThanExpression expression) {
        this.evaluateBinaryLogicalExpression(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsExpression expression) {
        this.evaluateBinaryLogicalExpression(expression);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanExpression expression) {
        this.evaluateBinaryLogicalExpression(expression);
        return Visiting.SKIP;
    }

    private void evaluateBinaryLogicalExpression(final CompareExpression expression) {
        final SpreadsheetExpressionEvaluationContext context = this.context;

        final Object left = context.evaluateExpression(
            expression.left()
        );

        final Object right = context.evaluateExpression(
            expression.right()
        );

        final boolean leftCurrency = left instanceof CurrencyValue;
        final boolean rightCurrency = right instanceof CurrencyValue;

        final Object leftValue;
        final Object rightValue;

        if (leftCurrency || rightCurrency) {
            final CurrencyValue leftCurrencyValue;
            final CurrencyValue rightCurrencyValue;

            if (leftCurrency && rightCurrency) {
                leftCurrencyValue = (CurrencyValue) left;
                rightCurrencyValue = (CurrencyValue) right;

                final CurrencyCode leftCurrencyCode = leftCurrencyValue.currencyCode();
                final CurrencyCode rightCurrencyCode = rightCurrencyValue.currencyCode();

                if (leftCurrencyCode.equals(rightCurrencyCode)) {
                    // ignore the currencyCode get just the number
                    leftValue = this.toExpressionNumber(leftCurrencyValue);
                    rightValue = this.toExpressionNumber(rightCurrencyValue);
                } else {
                    // convert right to left currency
                    final Number exchangeRate = context.currencyExchangeRateOrFail(
                        CurrencyExchange.with(
                            rightCurrencyCode, // from
                            leftCurrencyCode // to
                        ),
                        Optional.empty() // no datetime
                    );

                    leftValue = this.toExpressionNumber(leftCurrencyValue);

                    rightValue = context.multiplyToExpressionNumber(
                        rightCurrencyValue.value(),
                        exchangeRate
                    );

                    // leftNumber & rightNumber are the "same" currency and same Number type
                }

            } else {
                // convert the other to a CurrencyValue
                if (leftCurrency) {
                    leftValue = this.toExpressionNumber(left);
                    rightValue = this.toExpressionNumber(right);
                } else {
                    leftValue = this.toExpressionNumber(left);
                    rightValue = this.toExpressionNumber(right);
                }
            }
        } else {
            leftValue = left;
            rightValue = right;
        }

        this.value = expression.apply(
            leftValue,
            rightValue,
            context
        );
    }

    /**
     * Converts the given value to an {@link ExpressionNumber}, if its a {@link CurrencyValue} the number is used
     * ignoring the {@link CurrencyCode}.
     */
    private ExpressionNumber toExpressionNumber(final Object value) {
        return this.context.convertOrFail(
            value instanceof CurrencyValue ?
                ((CurrencyValue) value).value() :
                value,
            ExpressionNumber.class
        );
    }

    // Variable.........................................................................................................

    @Override
    protected Visiting startVisit(final CallExpression expression) {
        // dont evaluate expressions including references because some Excel functions accept references
        // and not values
        this.value = expression.apply(
            Cast.to(
                expression.children()
            ),
            this.context
        );

        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final ListExpression expression) {
        this.value = this.evaluateVariable(expression);
        return Visiting.SKIP;
    }

    private Object evaluateVariable(final VariableExpression expression) {
        return expression.apply(
            expression.children()
                .stream()
                .map(
                    this::evaluateExpression
                ).collect(Collectors.toList()),
            this.context
        );
    }

    final SpreadsheetExpressionEvaluationContext context;

    /**
     * Temporarily holds the value after evaluating an expression.
     */
    Object value;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
