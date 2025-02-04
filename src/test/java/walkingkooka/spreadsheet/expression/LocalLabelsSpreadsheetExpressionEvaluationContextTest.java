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
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetExpressionFunctionNames;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LocalLabelsSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<LocalLabelsSpreadsheetExpressionEvaluationContext>,
        ToStringTesting<LocalLabelsSpreadsheetExpressionEvaluationContext> {

    private final static String NAME = "Name1234";
    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName(NAME);

    private final static String LOCAL_VALUE = "abc123";

    private final static String CURRENCY_SYMBOL = "AUD";
    private final static char DECIMAL_SEPARATOR = '/';
    private final static String EXPONENT_SYMBOL = "HELLO";
    private final static char GROUP_SEPARATOR = '/';
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final static char NEGATIVE_SYMBOL = 'N';
    private final static char PERCENTAGE_SYMBOL = 'R';
    private final static char POSITIVE_SYMBOL = 'P';

    private final Function<SpreadsheetLabelName, Optional<Optional<Object>>> LABEL_TO_VALUES = new Function<>() {
        @Override
        public Optional<Optional<Object>> apply(final SpreadsheetLabelName label) {
            return this.map.containsKey(label) ?
                    Optional.of(
                            Optional.ofNullable(this.map.get(label))
                    ) :
                    Optional.empty();
        }

        @Override
        public String toString() {
            return ToStringBuilder.empty()
                    .value(this.map)
                    .build();
        }

        private final Map<SpreadsheetLabelName, Object> map = Maps.of(
                LABEL,
                LOCAL_VALUE
        );
    };

    @Test
    public void testWithNullNamesAndValuesFails() {
        assertThrows(
                NullPointerException.class,
                () -> LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        null,
                        SpreadsheetExpressionEvaluationContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        LABEL_TO_VALUES,
                        null
                )
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIsPureNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseFormulaNullFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testEvaluateFunctionContextReferenceInheritedLabel() {
        this.checkEquals(
                LOCAL_VALUE,
                this.createContext()
                        .evaluateFunction(
                                new FakeExpressionFunction<>() {

                                    @Override
                                    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                        return Lists.empty();
                                    }

                                    @Override
                                    public Object apply(final List<Object> values,
                                                        final ExpressionEvaluationContext context) {
                                        return context.referenceOrFail(LABEL);
                                    }
                                },
                                ExpressionFunction.NO_PARAMETER_VALUES
                        )
        );
    }

    @Test
    public void testEvaluateFunctionParameterInheritedLabel() {
        this.checkEquals(
                LOCAL_VALUE,
                this.createContext()
                        .evaluateFunction(
                                new FakeExpressionFunction<>() {

                                    @Override
                                    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                        return Lists.of(PARAMETER);
                                    }

                                    @Override
                                    public Object apply(final List<Object> values,
                                                        final ExpressionEvaluationContext context) {
                                        return this.parameters(1)
                                                .get(0)
                                                .getOrFail(values, 0);
                                    }

                                    private final ExpressionFunctionParameter<String> PARAMETER = ExpressionFunctionParameter.STRING.setKinds(ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES);
                                },
                                Lists.of(
                                        Expression.reference(LABEL)
                                )
                        )
        );
    }

    @Test
    public void testFunctionWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                        .expressionFunction(
                                ExpressionFunctionName.with(NAME)
                                        .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                        )
        );
        this.checkEquals(
                "Function name Name1234 is a parameter and not an actual function",
                thrown.getMessage()
        );
    }

    @Test
    public void testIsPureWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext().isPure(
                        ExpressionFunctionName.with(NAME)
                                .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                )
        );
        this.checkEquals(
                "Function name Name1234 is a parameter and not an actual function",
                thrown.getMessage()
        );
    }

    // reference........................................................................................................

    @Test
    public void testReferenceLocalLabelNonNullValue() {
        this.referenceAndCheck3(
                LABEL_TO_VALUES,
                LABEL,
                Optional.of(LOCAL_VALUE)
        );
    }

    @Test
    public void testReferenceLocalLabelNullValue() {
        this.referenceAndCheck3(
                (r) -> {
                    this.checkEquals(LABEL, r);
                    return Optional.of(Optional.empty());
                },
                LABEL,
                Optional.empty()
        );
    }

    @Test
    public void testReferenceLocalLabelAbsent() {
        final Optional<Optional<Object>> value = Optional.of(
                Optional.of("abc123")
        );

        this.referenceAndCheck2(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        (r) -> Optional.empty(),
                        new FakeSpreadsheetExpressionEvaluationContext() {
                            @Override
                            public Optional<Optional<Object>> reference(final ExpressionReference reference) {
                                checkEquals(LABEL, reference, "reference");
                                return value;
                            }
                        }
                ),
                LABEL,
                value
        );
    }

    private void referenceAndCheck3(final Function<SpreadsheetLabelName, Optional<Optional<Object>>> labelToValues,
                                    final ExpressionReference reference,
                                    final Optional<Object> expected) {
        this.referenceAndCheck(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        labelToValues,
                        SpreadsheetExpressionEvaluationContexts.fake()
                ),
                reference,
                expected
        );
    }

    // resolveIfLabel...................................................................................................

    @Test
    public void testResolveIfLabelLocalLabelFails() {
        final LocalLabelsSpreadsheetExpressionEvaluationContext context = this.createContext();

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> context.resolveIfLabel(SpreadsheetSelection.labelName(NAME))
        );

        this.checkEquals(
                "Label Name1234 has a value",
                thrown.getMessage()
        );
    }

    @Test
    public void testResolveLocalLabelFails() {
        final LocalLabelsSpreadsheetExpressionEvaluationContext context = this.createContext();

        this.checkEquals(
                Optional.of(
                        Optional.of(LOCAL_VALUE)
                ),
                context.reference(SpreadsheetSelection.labelName(NAME))
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        LABEL_TO_VALUES,
                        context
                ),
                "Name1234=\"abc123\" " + context
        );
    }

    @Override
    public LocalLabelsSpreadsheetExpressionEvaluationContext createContext() {
        return Cast.to(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        LABEL_TO_VALUES,
                        new FakeSpreadsheetExpressionEvaluationContext() {

                            @Override
                            public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
                                Objects.requireNonNull(cell, "cell");

                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public Optional<SpreadsheetLabelMapping> loadLabelMapping(final SpreadsheetLabelName labelName) {
                                Objects.requireNonNull(labelName, "labelName");

                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public String currencySymbol() {
                                return CURRENCY_SYMBOL;
                            }

                            @Override
                            public char decimalSeparator() {
                                return DECIMAL_SEPARATOR;
                            }

                            @Override
                            public String exponentSymbol() {
                                return EXPONENT_SYMBOL;
                            }

                            @Override
                            public char groupSeparator() {
                                return GROUP_SEPARATOR;
                            }

                            @Override
                            public MathContext mathContext() {
                                return MATH_CONTEXT;
                            }

                            @Override
                            public char negativeSign() {
                                return NEGATIVE_SYMBOL;
                            }

                            @Override
                            public char percentageSymbol() {
                                return PERCENTAGE_SYMBOL;
                            }

                            @Override
                            public char positiveSign() {
                                return POSITIVE_SYMBOL;
                            }
                        }
                )
        );
    }

    @Override
    public String currencySymbol() {
        return CURRENCY_SYMBOL;
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_SEPARATOR;
    }

    @Override
    public String exponentSymbol() {
        return EXPONENT_SYMBOL;
    }

    @Override
    public char groupSeparator() {
        return GROUP_SEPARATOR;
    }

    @Override
    public MathContext mathContext() {
        return MATH_CONTEXT;
    }

    @Override
    public char negativeSign() {
        return NEGATIVE_SYMBOL;
    }

    @Override
    public char percentageSymbol() {
        return PERCENTAGE_SYMBOL;
    }

    @Override
    public char positiveSign() {
        return POSITIVE_SYMBOL;
    }

    @Override
    public Class<LocalLabelsSpreadsheetExpressionEvaluationContext> type() {
        return LocalLabelsSpreadsheetExpressionEvaluationContext.class;
    }
}
