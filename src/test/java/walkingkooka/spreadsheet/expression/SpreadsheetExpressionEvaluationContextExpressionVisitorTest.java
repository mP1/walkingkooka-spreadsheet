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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyContextTesting;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyValue;
import walkingkooka.currency.HasCurrencyCodeTesting;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.HasMathContextTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ExpressionVisitorTesting;
import walkingkooka.tree.expression.HasExpressionNumberKindTesting;
import walkingkooka.tree.expression.convert.TreeConverters;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterCardinality;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;

import java.math.MathContext;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextExpressionVisitorTest implements ExpressionVisitorTesting<SpreadsheetExpressionEvaluationContextExpressionVisitor>,
    CurrencyContextTesting,
    DecimalNumberContextTesting,
    HasCurrencyCodeTesting,
    HasExpressionNumberKindTesting,
    HasMathContextTesting,
    ThrowableTesting {

    private final static ExpressionFunctionName FUNCTION_NAME = ExpressionFunctionName.with("hello");

    private final static SpreadsheetLabelName LABEL_NAME_NOT_NULL = SpreadsheetSelection.labelName("LabelNotNull");

    private final static Object LABEL_NAME_NOT_NULL_VALUE = "LabelNotNullValue";

    private final static SpreadsheetLabelName LABEL_NAME_MISSING = SpreadsheetSelection.labelName("LabelMissing");

    private final static SpreadsheetLabelName X = SpreadsheetSelection.labelName("X");

    private final static Integer X_VALUE = 11;

    private final static SpreadsheetLabelName Y = SpreadsheetSelection.labelName("Y");

    private final static Integer Y_VALUE = 222;

    private final static SpreadsheetExpressionEvaluationContext SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT = new FakeSpreadsheetExpressionEvaluationContext() {

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference reference) {
            return Optional.of(
                Optional.ofNullable(
                    reference.equals(X) ?
                        X_VALUE :
                        reference.equals(Y) ?
                            Y_VALUE :
                            reference.equals(LABEL_NAME_NOT_NULL) ?
                                LABEL_NAME_NOT_NULL_VALUE :
                                null
                )
            );
        }


        @Override
        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
            Objects.requireNonNull(name, "name");

            if(FUNCTION_NAME.equals(name)) {
                return new FakeExpressionFunction<>() {
                    @Override
                    public Optional<ExpressionFunctionName> name() {
                        return Optional.of(FUNCTION_NAME);
                    }

                    @Override
                    public Class<Object> returnType() {
                        return Object.class;
                    }

                    @Override
                    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                        return Lists.of(
                            ExpressionFunctionParameter.NUMBER.setCardinality(ExpressionFunctionParameterCardinality.OPTIONAL)
                                .setKinds(
                                    Sets.of(
                                        ExpressionFunctionParameterKind.CONVERT,
                                        ExpressionFunctionParameterKind.EVALUATE
                                    )
                                )
                        );
                    }

                    @Override
                    public Object apply(final List<Object> parameters,
                                        final ExpressionEvaluationContext expressionFunctionContext) {
                        return "HelloWorld!" + parameters.stream()
                            .map(
                                (Object value) -> value.getClass().getSimpleName() + value
                            ).collect(Collectors.joining());
                    }
                };
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(
                value,
                type,
                this
            );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return this.converter.convert(
                value,
                target,
                this
            );
        }

        private final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.collection(
            Lists.of(
                Converters.simple(),
                TreeConverters.toNumberOrExpressionNumber(
                    Converters.textToNumber(
                        (x) -> (DecimalFormat) DecimalFormat.getInstance(SpreadsheetExpressionEvaluationContextExpressionVisitorTest.LOCALE)
                    )
                ),
                TreeConverters.numberToNumber(),
                TreeConverters.numberOrExpressionNumberToNumber(),
                Converters.currencyValueToNumber(),
                Converters.numberToCurrencyValue()
            )
        );

        @Override
        public CurrencyCode currencyCode() {
            return CURRENCY_CODE;
        }

        @Override
        public String currencySymbol() {
            return DECIMAL_NUMBER_CONTEXT.currencySymbol();
        }

        @Override
        public char decimalSeparator() {
            return DECIMAL_NUMBER_CONTEXT.decimalSeparator();
        }

        @Override
        public String exponentSymbol() {
            return DECIMAL_NUMBER_CONTEXT.exponentSymbol();
        }

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public char groupSeparator() {
            return DECIMAL_NUMBER_CONTEXT.groupSeparator();
        }

        @Override
        public Locale locale() {
            return SpreadsheetExpressionEvaluationContextExpressionVisitorTest.LOCALE;
        }

        @Override
        public MathContext mathContext() {
            return MATH_CONTEXT;
        }

        @Override
        public char negativeSign() {
            return DECIMAL_NUMBER_CONTEXT.negativeSign();
        }

        @Override
        public char percentSymbol() {
            return DECIMAL_NUMBER_CONTEXT.percentSymbol();
        }

        @Override
        public char positiveSign() {
            return DECIMAL_NUMBER_CONTEXT.positiveSign();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return CURRENCY_CONTEXT.currencyForLocale(locale);
        }

        @Override
        public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                     final Optional<LocalDateTime> dateTime) {
            return CURRENCY_CONTEXT.currencyExchangeRate(
                currencyExchange,
                dateTime
            );
        }

        @Override
        public <N extends Number> N multiply(final Number left,
                                             final Number right,
                                             final Class<N> type) {
            return this.convertOrFail(
                this.convertOrFail(
                    left,
                    ExpressionNumber.class
                ).multiply(
                    this.convertOrFail(
                        right,
                        ExpressionNumber.class
                    ),
                    this
                ),
                type
            );
        }

        @Override
        public CaseSensitivity stringEqualsCaseSensitivity() {
            return SpreadsheetStrings.CASE_SENSITIVITY;
        }

        @Override
        public boolean isText(final Object value) {
            return value instanceof CharSequence;
        }

        @Override
        public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                      final Object value) {
            return parameter.convertOrFail(
                value,
                this
            );
        }

        @Override
        public Object handleException(final RuntimeException exception) {
            return SpreadsheetErrorKind.translate(exception);
        }
    };

    private static final CurrencyValue CURRENCY_VALUE = CurrencyValue.with(
        999,
        CURRENCY_CODE
    );

    // child............................................................................................................

    @Test
    public void testNamedFunctionExpression() {
        this.evaluateAndCheck(
            Expression.namedFunction(FUNCTION_NAME),
            FUNCTION_NAME
        );
    }

    @Test
    public void testReferenceExpression() {
        this.evaluateAndCheck(
            Expression.reference(LABEL_NAME_NOT_NULL),
            LABEL_NAME_NOT_NULL_VALUE
        );
    }

    @Test
    public void testReferenceExpressionMissing() {
        this.evaluateAndCheck(
            Expression.reference(LABEL_NAME_MISSING),
            null
        );
    }

    @Test
    public void testValueExpressionWithNull() {
        this.evaluateAndCheck(
            Expression.value(null),
            null
        );
    }

    @Test
    public void testValueExpressionWithNumber() {
        final Number number = 123;

        this.evaluateAndCheck(
            Expression.value(number),
            number
        );
    }

    @Test
    public void testValueExpressionWithCurrencyValue() {
        final CurrencyValue currencyValue = CurrencyValue.with(
            123,
            CURRENCY_CODE
        );

        this.evaluateAndCheck(
            Expression.value(currencyValue),
            currencyValue
        );
    }

    // unary............................................................................................................

    @Test
    public void testLambdaFunctionExpression() {
        this.evaluateAndCheck(
            Expression.lambdaFunction(
                Lists.of(
                    parameter(X.value()),
                    parameter(Y.value())
                ),
                Expression.add(
                    Expression.reference(X),
                    Expression.reference(Y)
                )
            ),
            EXPRESSION_NUMBER_KIND.create(
                X_VALUE + Y_VALUE
            )
        );
    }

    @Test
    public void testNegativeExpressionWithString() {
        this.evaluateAndCheck(
            Expression.negative(
                Expression.value("123")
            ),
            EXPRESSION_NUMBER_KIND.create(
                -123
            )
        );
    }

    @Test
    public void testNegativeExpressionWithNumber() {
        this.evaluateAndCheck(
            Expression.negative(
                Expression.value(123)
            ),
            EXPRESSION_NUMBER_KIND.create(
                -123
            )
        );
    }

    @Test
    public void testNegativeExpressionWithCurrencyValue() {
        this.evaluateAndCheck(
            Expression.negative(
                Expression.value(
                    CurrencyValue.with(
                        EXPRESSION_NUMBER_KIND.create(123),
                        CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(-123),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testNegativeExpressionWithCurrencyValueAndDifferentCurrencyContext() {
        final ExpressionNumber number = EXPRESSION_NUMBER_KIND.create(234);

        this.evaluateAndCheck(
            Expression.negative(
                Expression.value(
                    CurrencyValue.with(
                        number,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                number.negate(SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT),
                DIFFERENT_CURRENCY_CODE
            )
        );
    }

    @Test
    public void testNotExpressionWithNumber() {
        this.evaluateAndCheck(
            Expression.not(
                Expression.value(123)
            ),
            EXPRESSION_NUMBER_KIND.create(~123)
        );
    }

    // add..............................................................................................................

    @Test
    public void testAddExpressionWithStringAndString() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            "LEFTright"
        );
    }

    @Test
    public void testAddExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value(11),
                Expression.value(22)
            ),
            EXPRESSION_NUMBER_KIND.create(
                11 + 22
            )
        );
    }

    @Test
    public void testAddExpressionWithCurrencyValueAndNumber() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 + 22
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testAddExpressionWithNumberAndCurrencyValue() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 + 22
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testAddExpressionWithCurrencyValueAndCurrencyValue() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    22 + 22 * DIFFERENT_CURRENCY_EXCHANGE_RATE
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testAddExpressionWithCurrencyValueAndCurrencyValueAndDifferentCurrencyCode() {
        this.evaluateAndCheck(
            Expression.add(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        DIFFERENT_CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 + 22
                ),
                DIFFERENT_CURRENCY_CODE
            )
        );
    }

    // and..............................................................................................................

    @Test
    public void testAndExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.and(
                Expression.value(6),
                Expression.value(3)
            ),
            EXPRESSION_NUMBER_KIND.create(
                6 & 3
            )
        );
    }

    @Test
    public void testAndExpressionWithCurrencyValueAndNumberFails() {
        this.evaluateFails(
            Expression.and(
                Expression.value(
                    CURRENCY_VALUE
                ),
                Expression.value(3)
            )
        );
    }

    @Test
    public void testAndExpressionWithNumberAndCurrencyValueFails() {
        this.evaluateFails(
            Expression.and(
                Expression.value(6),
                Expression.value(
                    CURRENCY_VALUE
                )
            )
        );
    }

    // call.............................................................................................................

    @Test
    public void testCallExpression() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(FUNCTION_NAME),
                Lists.empty()
            ),
            "HelloWorld!"
        );
    }

    @Test
    public void testCallExpressionWithInteger() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(FUNCTION_NAME),
                Lists.of(
                    Expression.value(12)
                )
            ),
            "HelloWorld!ExpressionNumberBigDecimal12"
        );
    }

    @Test
    public void testCallExpressionWithExpressionNumber() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(FUNCTION_NAME),
                Lists.of(
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.create(12)
                    )
                )
            ),
            "HelloWorld!ExpressionNumberBigDecimal12"
        );
    }

    @Test
    public void testCallExpressionWithCurrencyValue() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(FUNCTION_NAME),
                Lists.of(
                    Expression.value(
                        CurrencyValue.with(
                            EXPRESSION_NUMBER_KIND.create(12),
                            CURRENCY_CODE
                        )
                    )
                )
            ),
            "HelloWorld!ExpressionNumberBigDecimal12"
        );
    }

    // divide...........................................................................................................

    @Test
    public void testDivideExpressionWithStringAndStringFails() {
        this.evaluateFails(
            Expression.divide(
                Expression.value("33"),
                Expression.value("11")
            ),
            "33/11"
        );
    }

    @Test
    public void testDivideExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.divide(
                Expression.value(33),
                Expression.value(11)
            ),
            EXPRESSION_NUMBER_KIND.create(
                33 / 11
            )
        );
    }

    @Test
    public void testDivideExpressionWithLeftCurrencyValueAndNumber() {
        this.evaluateAndCheck(
            Expression.divide(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 / 11
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testDivideExpressionWithLeftCurrencyValueDifferentCurrencyCodeAndNumber() {
        this.evaluateAndCheck(
            Expression.divide(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 / 11
                ),
                DIFFERENT_CURRENCY_CODE
            )
        );
    }

    @Test
    public void testDivideExpressionWithRightCurrencyValueFails() {
        this.evaluateFails(
            Expression.divide(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                )
            )
        );
    }

    // equals............................................................................................................

    @Test
    public void testEqualsExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndNumberLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(11),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(11),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(22),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndNumberLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.equalsExpression(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    // greaterThanEquals................................................................................................

    @Test
    public void testGreaterThanEqualsExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndNumberGreater() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(11),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(11),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(22),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndNumberGreater() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.greaterThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    // greaterThan......................................................................................................

    @Test
    public void testGreaterThanExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndNumberLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(11),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(11),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(22),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndNumberLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testGreaterThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.greaterThan(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }
    
    // lessThanEquals...................................................................................................

    @Test
    public void testLessThanEqualsExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndNumberLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(11),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(11),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(22),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndNumberLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.lessThanEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    // lessThan.........................................................................................................

    @Test
    public void testLessThanExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndNumberLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(11),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(11),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(22),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndNumberLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testLessThanExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.lessThan(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    // list.............................................................................................................

    @Test
    public void testListExpression() {
        this.evaluateAndCheck(
            Expression.list(
                Lists.empty()
            ),
            Lists.empty()
        );
    }

    @Test
    public void testListExpressionWithCurrencyValue() {
        this.evaluateAndCheck(
            Expression.list(
                Lists.of(
                    Expression.value(
                        CurrencyValue.with(
                            11,
                            CURRENCY_CODE
                        )
                    )
                )
            ),
            Lists.of(
                CurrencyValue.with(
                    11,
                    CURRENCY_CODE
                )
            )
        );
    }

    // modulo...........................................................................................................

    @Test
    public void testModuloExpressionWithStringAndStringFails() {
        this.evaluateFails(
            Expression.modulo(
                Expression.value("33"),
                Expression.value("11")
            ),
            "33%11"
        );
    }

    @Test
    public void testModuloExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.modulo(
                Expression.value(33),
                Expression.value(11)
            ),
            EXPRESSION_NUMBER_KIND.create(
                33 % 11
            )
        );
    }

    @Test
    public void testModuloExpressionWithLeftCurrencyValueAndNumber() {
        this.evaluateAndCheck(
            Expression.modulo(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 % 11
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testModuloExpressionWithLeftCurrencyValueDifferentCurrencyCodeAndNumber() {
        this.evaluateAndCheck(
            Expression.modulo(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 % 11
                ),
                DIFFERENT_CURRENCY_CODE
            )
        );
    }

    @Test
    public void testModuloExpressionWithRightCurrencyValueFails() {
        this.evaluateFails(
            Expression.modulo(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                )
            )
        );
    }

    // notEquals........................................................................................................

    @Test
    public void testNotEqualsExpressionWithStringAndStringLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value("LEFT"),
                Expression.value("right")
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithStringAndStringSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value("LEFT"),
                Expression.value("left")
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithStringAndStringMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value("Zebra"),
                Expression.value("left")
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndNumberLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(11),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndNumberSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(11),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndNumberMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(22),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndNumberLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(22)
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndNumberSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndNumberMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(11)
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithNumberAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(22),
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeLess() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeSame() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        22,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            false
        );
    }

    @Test
    public void testNotEqualsExpressionWithCurrencyValueAndCurrencyValueDifferentCurrencyCodeMore() {
        this.evaluateAndCheck(
            Expression.notEquals(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        22 * CURRENCY_EXCHANGE_RATE,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            true
        );
    }

    // or...............................................................................................................

    @Test
    public void testOrExpressionWithNumberOrNumber() {
        this.evaluateAndCheck(
            Expression.or(
                Expression.value(6),
                Expression.value(3)
            ),
            EXPRESSION_NUMBER_KIND.create(
                6 | 3
            )
        );
    }

    @Test
    public void testOrExpressionWithCurrencyValueOrNumberFails() {
        this.evaluateFails(
            Expression.or(
                Expression.value(
                    CURRENCY_VALUE
                ),
                Expression.value(3)
            )
        );
    }

    @Test
    public void testOrExpressionWithNumberOrCurrencyValueFails() {
        this.evaluateFails(
            Expression.or(
                Expression.value(6),
                Expression.value(
                    CURRENCY_VALUE
                )
            )
        );
    }

    // power............................................................................................................

    @Test
    public void testPowerExpressionWithStringAndStringFails() {
        this.evaluateFails(
            Expression.power(
                Expression.value("33"),
                Expression.value("2")
            ),
            "33^^2"
        );
    }

    @Test
    public void testPowerExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.power(
                Expression.value(33),
                Expression.value(2)
            ),
            EXPRESSION_NUMBER_KIND.create(
                33 * 33
            )
        );
    }

    @Test
    public void testPowerExpressionWithLeftCurrencyValueAndNumber() {
        this.evaluateAndCheck(
            Expression.power(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(2)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 * 33
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testPowerExpressionWithLeftCurrencyValueDifferentCurrencyCodeAndNumber() {
        this.evaluateAndCheck(
            Expression.power(
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                ),
                Expression.value(2)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    33 * 33
                ),
                DIFFERENT_CURRENCY_CODE
            )
        );
    }

    @Test
    public void testPowerExpressionWithRightCurrencyValueFails() {
        this.evaluateFails(
            Expression.power(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                )
            )
        );
    }

    // subtract..............................................................................................................

    @Test
    public void testSubtractExpressionWithNumberAndNumber() {
        this.evaluateAndCheck(
            Expression.subtract(
                Expression.value(11),
                Expression.value(33)
            ),
            EXPRESSION_NUMBER_KIND.create(
                11 - 33
            )
        );
    }

    @Test
    public void testSubtractExpressionWithCurrencyValueAndNumber() {
        this.evaluateAndCheck(
            Expression.subtract(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(33)
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 - 33
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testSubtractExpressionWithNumberAndCurrencyValue() {
        this.evaluateAndCheck(
            Expression.subtract(
                Expression.value(11),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 - 33
                ),
                CURRENCY_CODE
            )
        );
    }

    @Test
    public void testSubtractExpressionWithCurrencyValueAndCurrencyValue() {
        this.evaluateAndCheck(
            Expression.subtract(
                Expression.value(
                    CurrencyValue.with(
                        11,
                        CURRENCY_CODE
                    )
                ),
                Expression.value(
                    CurrencyValue.with(
                        33,
                        DIFFERENT_CURRENCY_CODE
                    )
                )
            ),
            CurrencyValue.with(
                EXPRESSION_NUMBER_KIND.create(
                    11 - 33 * DIFFERENT_CURRENCY_EXCHANGE_RATE
                ),
                CURRENCY_CODE
            )
        );
    }

    // xor..............................................................................................................

    @Test
    public void testXorExpressionWithNumberXorNumber() {
        this.evaluateAndCheck(
            Expression.xor(
                Expression.value(6),
                Expression.value(3)
            ),
            EXPRESSION_NUMBER_KIND.create(
                6 ^ 3
            )
        );
    }

    @Test
    public void testXorExpressionWithCurrencyValueXorNumberFails() {
        this.evaluateFails(
            Expression.xor(
                Expression.value(
                    CURRENCY_VALUE
                ),
                Expression.value(3)
            )
        );
    }

    @Test
    public void testXorExpressionWithNumberXorCurrencyValueFails() {
        this.evaluateFails(
            Expression.xor(
                Expression.value(6),
                Expression.value(
                    CURRENCY_VALUE
                )
            )
        );
    }

    private static ExpressionFunctionParameter<ExpressionNumber> parameter(final String name) {
        return ExpressionFunctionParameterName.with(name)
            .required(ExpressionNumber.class)
            .setKinds(ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES);
    }

    private void evaluateAndCheck(final Expression expression,
                                  final Object expected) {
        this.evaluateAndCheck(
            expression,
            SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
            expected
        );
    }

    private void evaluateAndCheck(final Expression expression,
                                  final SpreadsheetExpressionEvaluationContext context,
                                  final Object expected) {
        this.checkEquals(
            expected,
            SpreadsheetExpressionEvaluationContextExpressionVisitor.evaluate(
                expression,
                context
            ),
            () -> expression.toString()
        );
    }

    private void evaluateFails(final Expression expression) {
        this.evaluateFails(
            expression,
            "Invalid parameter type CurrencyValue"
        );
    }

    private void evaluateFails(final Expression expression,
                               final String expected) {
        this.evaluateFails(
            expression,
            SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
            expected
        );
    }

    private void evaluateFails(final Expression expression,
                               final SpreadsheetExpressionEvaluationContext context,
                               final String expected) {
        this.getMessageAndCheck(
            assertThrows(
                RuntimeException.class,
                () -> SpreadsheetExpressionEvaluationContextExpressionVisitor.evaluate(
                    expression,
                    SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT
                )
            ),
            expected
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContextExpressionVisitor createVisitor() {
        return new SpreadsheetExpressionEvaluationContextExpressionVisitor(null);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextExpressionVisitor> type() {
        return SpreadsheetExpressionEvaluationContextExpressionVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetExpressionEvaluationContext.class.getSimpleName();
    }

}
