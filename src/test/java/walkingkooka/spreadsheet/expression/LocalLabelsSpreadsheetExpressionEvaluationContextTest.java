/*
 * Copyright 2022 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.FunctionExpressionName;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LocalLabelsSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<LocalLabelsSpreadsheetExpressionEvaluationContext>,
        ToStringTesting<LocalLabelsSpreadsheetExpressionEvaluationContext> {

    private final static String NAME = "Name1234";

    private final static String LOCAL_VALUE = "abc123";

    private final static String CURRENCY_SYMBOL = "AUD";
    private final static char DECIMAL_SEPARATOR = '/';
    private final static String EXPONENT_SYMBOL = "HELLO";
    private final static char GROUPING_SEPARATOR = '/';
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final static char NEGATIVE_SYMBOL = 'N';
    private final static char PERCENTAGE_SYMBOL = 'R';
    private final static char POSITIVE_SYMBOL = 'P';

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
                        Maps.empty(),
                        null
                )
        );
    }

    @Test
    public void testWithEmptyDoesntWrap() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        assertSame(
                context,
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        Maps.empty(),
                        context
                )
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFunctionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIsPureNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseExpressionNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testResolveIfLabelNullSelectionFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testFunctionWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    this.createContext().function(FunctionExpressionName.with(NAME));
                }
        );
        this.checkEquals(
                "Function name Name1234 is a named value and not an actual function",
                thrown.getMessage()
        );
    }

    @Test
    public void testIsPureWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    this.createContext().isPure(FunctionExpressionName.with(NAME));
                }
        );
        this.checkEquals(
                "Function name Name1234 is a named value and not an actual function",
                thrown.getMessage()
        );
    }

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
                Optional.of(LOCAL_VALUE),
                context.reference(SpreadsheetSelection.labelName(NAME))
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        Maps.of(
                                SpreadsheetSelection.labelName(NAME),
                                "abc"
                        ),
                        context
                ),
                "Name1234=\"abc\" " + context
        );
    }

    @Override
    public LocalLabelsSpreadsheetExpressionEvaluationContext createContext() {
        return Cast.to(
                LocalLabelsSpreadsheetExpressionEvaluationContext.with(
                        Maps.of(
                                SpreadsheetSelection.labelName(NAME),
                                LOCAL_VALUE
                        ),
                        new FakeSpreadsheetExpressionEvaluationContext() {

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
                            public char groupingSeparator() {
                                return GROUPING_SEPARATOR;
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
    public char groupingSeparator() {
        return GROUPING_SEPARATOR;
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
