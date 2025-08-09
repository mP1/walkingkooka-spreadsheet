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
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LocalReferencesSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<LocalReferencesSpreadsheetExpressionEvaluationContext>,
    ToStringTesting<LocalReferencesSpreadsheetExpressionEvaluationContext> {

    private final static String NAME = "Name1234";

    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName(NAME);
    private final static String LABEL_LOCAL_VALUE = "LabelLocalValue";

    private final static TemplateValueName TEMPLATE_VALUE_NAME = TemplateValueName.with("TemplateValue");
    private final static String TEMPLATE_LOCAL_VALUE = "TemplateLocalValue123";

    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final static String CURRENCY_SYMBOL = "AUD";
    private final static char DECIMAL_SEPARATOR = '/';
    private final static String EXPONENT_SYMBOL = "HELLO";
    private final static char GROUP_SEPARATOR = '/';
    private final static String INFINITY_SYMBOL = "Infinity!";
    private final static char MONETARY_DECIMAL_SEPARATOR = '/';
    private final static String NAN_SYMBOL = "Nan!";
    private final static char NEGATIVE_SYMBOL = 'N';
    private final static char PERCENT_SYMBOL = 'R';
    private final static char PERMILL_SYMBOL = '^';
    private final static char POSITIVE_SYMBOL = 'P';
    private final static char ZERO = '0';

    private final Function<ExpressionReference, Optional<Optional<Object>>> REFERENCE_TO_VALUES = new Function<>() {
        @Override
        public Optional<Optional<Object>> apply(final ExpressionReference reference) {
            return this.map.containsKey(reference) ?
                Optional.of(
                    Optional.ofNullable(this.map.get(reference))
                ) :
                Optional.empty();
        }

        @Override
        public String toString() {
            return ToStringBuilder.empty()
                .value(this.map)
                .build();
        }

        private final Map<ExpressionReference, Object> map = Maps.of(
            LABEL,
            LABEL_LOCAL_VALUE,
            TEMPLATE_VALUE_NAME,
            TEMPLATE_LOCAL_VALUE
        );
    };

    @Test
    public void testWithNullReferenceToValuesFails() {
        assertThrows(
            NullPointerException.class,
            () -> LocalReferencesSpreadsheetExpressionEvaluationContext.with(
                null,
                SpreadsheetExpressionEvaluationContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> LocalReferencesSpreadsheetExpressionEvaluationContext.with(
                REFERENCE_TO_VALUES,
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
    public void testEvaluateFunctionContextReferenceWithLocalLabel() {
        this.checkEquals(
            LABEL_LOCAL_VALUE,
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
    public void testEvaluateFunctionContextReferenceWithLocalTemplateValue() {
        this.checkEquals(
            TEMPLATE_LOCAL_VALUE,
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
                            return context.referenceOrFail(TEMPLATE_VALUE_NAME);
                        }
                    },
                    ExpressionFunction.NO_PARAMETER_VALUES
                )
        );
    }

    @Test
    public void testEvaluateFunctionParameterWithLocalLabel() {
        this.checkEquals(
            LABEL_LOCAL_VALUE,
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

                        private final ExpressionFunctionParameter<String> PARAMETER = ExpressionFunctionParameter.STRING.setKinds(
                            ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES
                        );
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
                    SpreadsheetExpressionFunctions.name(NAME)
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
                SpreadsheetExpressionFunctions.name(NAME)
            )
        );
        this.checkEquals(
            "Function name Name1234 is a parameter and not an actual function",
            thrown.getMessage()
        );
    }

    // reference........................................................................................................

    @Test
    public void testReferenceWithLocalLabelNonNullValue() {
        this.referenceAndCheck3(
            REFERENCE_TO_VALUES,
            LABEL,
            Optional.of(LABEL_LOCAL_VALUE)
        );
    }

    @Test
    public void testReferenceWithLocalLabelNullValue() {
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
    public void testReferenceWithLocalLabelAbsent() {
        final Optional<Optional<Object>> value = Optional.of(
            Optional.of(LABEL_LOCAL_VALUE)
        );

        this.referenceAndCheck2(
            LocalReferencesSpreadsheetExpressionEvaluationContext.with(
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

    private void referenceAndCheck3(final Function<ExpressionReference, Optional<Optional<Object>>> referenceToValues,
                                    final ExpressionReference reference,
                                    final Optional<Object> expected) {
        this.referenceAndCheck(
            LocalReferencesSpreadsheetExpressionEvaluationContext.with(
                referenceToValues,
                SpreadsheetExpressionEvaluationContexts.fake()
            ),
            reference,
            expected
        );
    }

    // resolveIfLabelOrFail.............................................................................................

    @Test
    public void testResolveIfLabelLocalLabelFails() {
        final LocalReferencesSpreadsheetExpressionEvaluationContext context = this.createContext();

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> context.resolveIfLabelOrFail(SpreadsheetSelection.labelName(NAME))
        );

        this.checkEquals(
            "Label Name1234 has a value",
            thrown.getMessage()
        );
    }

    @Test
    public void testResolveLocalLabelFails() {
        final LocalReferencesSpreadsheetExpressionEvaluationContext context = this.createContext();

        this.checkEquals(
            Optional.of(
                Optional.of(LABEL_LOCAL_VALUE)
            ),
            context.reference(SpreadsheetSelection.labelName(NAME))
        );
    }

    @Override
    public LocalReferencesSpreadsheetExpressionEvaluationContext createContext() {
        return LocalReferencesSpreadsheetExpressionEvaluationContext.with(
            REFERENCE_TO_VALUES,
            new FakeSpreadsheetExpressionEvaluationContext() {

                @Override
                public Optional<SpreadsheetCell> cell() {
                    return Optional.empty();
                }

                @Override
                public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
                    Objects.requireNonNull(cell, "cell");

                    throw new UnsupportedOperationException();
                }

                @Override
                public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
                    Objects.requireNonNull(range, "range");

                    throw new UnsupportedOperationException();
                }

                @Override
                public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
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
                public String infinitySymbol() {
                    return INFINITY_SYMBOL;
                }

                @Override
                public MathContext mathContext() {
                    return MATH_CONTEXT;
                }

                @Override
                public char monetaryDecimalSeparator() {
                    return MONETARY_DECIMAL_SEPARATOR;
                }

                @Override
                public String nanSymbol() {
                    return NAN_SYMBOL;
                }

                @Override
                public char negativeSign() {
                    return NEGATIVE_SYMBOL;
                }

                @Override
                public char percentSymbol() {
                    return PERCENT_SYMBOL;
                }

                @Override
                public char permillSymbol() {
                    return PERMILL_SYMBOL;
                }

                @Override
                public char positiveSign() {
                    return POSITIVE_SYMBOL;
                }

                @Override
                public char zeroDigit() {
                    return ZERO;
                }

                @Override
                public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
                    Objects.requireNonNull(reference, "reference");
                    throw new UnsupportedOperationException();
                }

                @Override
                public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
                    Objects.requireNonNull(fields, "fields");
                    throw new UnsupportedOperationException();
                }

                @Override
                public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
                    Objects.requireNonNull(name, "name");
                    throw new UnsupportedOperationException();
                }

                @Override
                public Optional<EmailAddress> user() {
                    return Optional.empty();
                }

                @Override
                public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
                    Objects.requireNonNull(reference, "reference");

                    throw new UnsupportedOperationException();
                }

                @Override
                public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
                    return this.localeContext.dateTimeSymbolsForLocale(locale);
                }

                @Override
                public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
                    return this.localeContext.decimalNumberSymbolsForLocale(locale);
                }

                private final LocaleContext localeContext = LocaleContexts.jre(Locale.ENGLISH);

                @Override
                public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
                    Objects.requireNonNull(cell, "cell");
                    return super.spreadsheetFormatterContext(cell);
                }

                @Override
                public <T> FakeSpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                                          final T value) {
                    Objects.requireNonNull(name, "name");
                    Objects.requireNonNull(value, "value");
                    throw new UnsupportedOperationException();
                }
            }
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
    public String infinitySymbol() {
        return INFINITY_SYMBOL;
    }

    @Override
    public MathContext mathContext() {
        return MATH_CONTEXT;
    }

    @Override
    public char monetaryDecimalSeparator() {
        return MONETARY_DECIMAL_SEPARATOR;
    }

    @Override
    public String nanSymbol() {
        return NAN_SYMBOL;
    }

    @Override
    public char negativeSign() {
        return NEGATIVE_SYMBOL;
    }

    @Override
    public char percentSymbol() {
        return PERCENT_SYMBOL;
    }

    @Override
    public char permillSymbol() {
        return PERMILL_SYMBOL;
    }

    @Override
    public char positiveSign() {
        return POSITIVE_SYMBOL;
    }

    @Override
    public char zeroDigit() {
        return ZERO;
    }

    @Override
    public void testFindByLocaleTextWithNullTextFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFindByLocaleTextWithNegativeOffsetFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFindByLocaleTextWithInvalidCountFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLocaleTextWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testResolveLabelWithNullFails() {
        throw new UnsupportedOperationException();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
            LocalReferencesSpreadsheetExpressionEvaluationContext.with(
                REFERENCE_TO_VALUES,
                context
            ),
            "Name1234=\"LabelLocalValue\", TemplateValue=\"TemplateLocalValue123\" " + context
        );
    }

    // class............................................................................................................

    @Override
    public Class<LocalReferencesSpreadsheetExpressionEvaluationContext> type() {
        return LocalReferencesSpreadsheetExpressionEvaluationContext.class;
    }
}
