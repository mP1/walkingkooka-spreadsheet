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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ConverterSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContext> {

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL128);

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("B2");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
            CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("=1+2"))
    );

    private final static SpreadsheetCellStore CELL_STORE = SpreadsheetCellStores.fake();

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-US"))
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.PRECISION, DECIMAL_NUMBER_CONTEXT.mathContext().getPrecision())
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, DECIMAL_NUMBER_CONTEXT.mathContext().getRoundingMode())
            .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, 0L)
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 20)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DEFAULT)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER, ConverterSelector.parse("general"))
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("$#.##").spreadsheetFormatterSelector());

    /**
     * Concats all the given parameters.
     */
    private final static ExpressionFunction<String, SpreadsheetExpressionEvaluationContext> CONCAT = new FakeExpressionFunction<>() {
        @Override
        public String apply(final List<Object> objects,
                            final SpreadsheetExpressionEvaluationContext context) {
            return CharacterConstant.COMMA.toSeparatedString(
                    objects,
                    Object::toString
            );
        }

        @Override
        public Optional<FunctionExpressionName> name() {
            return Optional.of(
                    FunctionExpressionName.with("test-concat-1")
            );
        }

        @Override
        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
            return Lists.of(
                    ExpressionFunctionParameterName.with("strings")
                            .variable(String.class)
                            .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES)
            );
        }

        @Override
        public Class<String> returnType() {
            return String.class;
        }
    };

    /**
     * A namedFunction that expects a number and returns that.
     */
    private final static ExpressionFunction<ExpressionNumber, SpreadsheetExpressionEvaluationContext> ECHO = new FakeExpressionFunction<>() {
        @Override
        public ExpressionNumber apply(final List<Object> parameters,
                                      final SpreadsheetExpressionEvaluationContext context) {
            return NUMBER.getOrFail(parameters, 0);
        }

        @Override
        public Optional<FunctionExpressionName> name() {
            return Optional.of(
                    FunctionExpressionName.with("test-echo-2")
            );
        }

        @Override
        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
            return Lists.of(NUMBER);
        }

        private final ExpressionFunctionParameter<ExpressionNumber> NUMBER = ExpressionFunctionParameterName.with("number")
                .required(ExpressionNumber.class)
                .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES);

        @Override
        public Class<ExpressionNumber> returnType() {
            return ExpressionNumber.class;
        }
    };

    private final ExpressionFunctionProvider EXPRESSION_FUNCTION_PROVIDER = new ExpressionFunctionProvider() {
        @Override
        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final FunctionExpressionName n) {
            Objects.requireNonNull(n, "name");

            if (CONCAT.name().get().equals(n)) {
                return Cast.to(CONCAT);
            }

            if (ECHO.name().get().equals(n)) {
                return Cast.to(ECHO);
            }

            throw new UnknownExpressionFunctionException(n);
        }

        @Override
        public Set<ExpressionFunctionInfo> expressionFunctionInfos() {
            return Sets.of(
                    ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/" + CONCAT),
                            CONCAT.name().get()
                    ),
                    ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/" + ECHO),
                            ECHO.name().get()
                    )
            );
        }
    };

    private final static Function<ExpressionReference, Optional<Optional<Object>>> REFERENCES = (r) -> {
        Objects.requireNonNull(r, "reference");
        throw new UnsupportedOperationException();
    };

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    // tests............................................................................................................

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
                NullPointerException.class,
                () -> ConverterSpreadsheetExpressionEvaluationContext.with(
                        null,
                        SpreadsheetExpressionEvaluationContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> ConverterSpreadsheetExpressionEvaluationContext.with(
                        Converters.fake(),
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final ConverterSpreadsheetExpressionEvaluationContext created = ConverterSpreadsheetExpressionEvaluationContext.with(
                converter,
                context
        );

        assertSame(converter, created.converter, "converter");
        assertSame(context, created.context, "context");
    }

    @Test
    public void testWithDoubleWrap() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        final ConverterSpreadsheetExpressionEvaluationContext first = ConverterSpreadsheetExpressionEvaluationContext.with(
                Converters.fake(),
                context
        );

        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final ConverterSpreadsheetExpressionEvaluationContext doubleWrapped = ConverterSpreadsheetExpressionEvaluationContext.with(
                converter,
                first
        );

        assertSame(converter, doubleWrapped.converter, "converter");
        assertSame(context, doubleWrapped.context, "context");
    }

    // evaluate........................................................................................................

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionNullParametersFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testEvaluateFunction() {
        this.evaluateFunctionAndCheck(
                CONCAT,
                Lists.of(
                        EXPRESSION_NUMBER_KIND.create(111),
                        EXPRESSION_NUMBER_KIND.create(222)
                ),
                "$111.,$222."
        );
    }

    @Test
    public void testEvaluateFunctionNestedFunctionNotConverted() {
        this.evaluateFunctionAndCheck(
                CONCAT,
                Lists.of(
                        Expression.call(
                                Expression.namedFunction(
                                        ECHO.name()
                                                .get()
                                ),
                                Lists.of(
                                        Expression.value(
                                                EXPRESSION_NUMBER_KIND.create(111)
                                        )
                                )
                        )
                ),
                "$111."
        );
    }

    @Test
    public void testEvaluateExpression() {
        final ExpressionNumber value = EXPRESSION_NUMBER_KIND.create(111);

        this.evaluateAndCheck(
                Expression.value(
                        value
                ),
                value
        );
    }

    @Test
    public void testEvaluateExpression2() {
        this.evaluateAndCheck(
                Expression.add(
                        Expression.value(EXPRESSION_NUMBER_KIND.create(111)),
                        Expression.value(EXPRESSION_NUMBER_KIND.create(222))
                ),
                EXPRESSION_NUMBER_KIND.create(333)
        );
    }

    @Test
    public void testEvaluateExpressionFunctionParametersConverted() {
        this.executeNamedFunctionAndCheck(
                CONCAT.name()
                        .get(),
                Lists.of(
                        Expression.value(EXPRESSION_NUMBER_KIND.create(111)),
                        Expression.value(EXPRESSION_NUMBER_KIND.create(222))
                ),
                "!!!111,!!!222"
        );
    }

    @Test
    public void testEvaluateExpressionFunctionParametersConverted2() {
        this.executeNamedFunctionAndCheck(
                CONCAT.name()
                        .get(),
                Lists.of(
                        Expression.value(EXPRESSION_NUMBER_KIND.create(111)),
                        Expression.add(
                                Expression.value(EXPRESSION_NUMBER_KIND.create(222)),
                                Expression.value(EXPRESSION_NUMBER_KIND.create(333))
                        )
                ),
                "!!!111,!!!555"
        );
    }

    @Test
    public void testEvaluateExpressionFunctionParametersConverted3() {
        this.executeNamedFunctionAndCheck(
                CONCAT.name()
                        .get(),
                Lists.of(
                        Expression.add(
                                Expression.value(EXPRESSION_NUMBER_KIND.create(111)),
                                Expression.value(EXPRESSION_NUMBER_KIND.create(222))
                        ),
                        Expression.value(EXPRESSION_NUMBER_KIND.create(444))
                ),
                "!!!333,!!!444"
        );
    }

    @Test
    public void testEvaluateExpressionFunctionParametersConvertedNestedFunctionParameterNotConverted() {
        this.executeNamedFunctionAndCheck(
                CONCAT.name()
                        .get(),
                Lists.of(
                        Expression.call(
                                Expression.namedFunction(
                                        ECHO.name()
                                                .get()
                                ),
                                Lists.of(
                                        Expression.value(EXPRESSION_NUMBER_KIND.create(111))
                                )
                        )
                ),
                "!!!111"
        );
    }

    @Test
    public void testEvaluateExpressionFunctionParametersConvertedNestedFunctionParameterNotConverted2() {
        this.executeNamedFunctionAndCheck(
                CONCAT.name()
                        .get(),
                Lists.of(
                        Expression.value(EXPRESSION_NUMBER_KIND.create(111)),
                        Expression.call(
                                Expression.namedFunction(
                                        ECHO.name()
                                                .get()
                                ),
                                Lists.of(
                                        Expression.value(
                                                EXPRESSION_NUMBER_KIND.create(222)
                                        )
                                )
                        ),
                        Expression.value(EXPRESSION_NUMBER_KIND.create(333)),
                        Expression.call(
                                Expression.namedFunction(
                                        ECHO.name()
                                                .get()
                                ),
                                Lists.of(
                                        Expression.value(EXPRESSION_NUMBER_KIND.create(444))
                                )
                        )
                ),
                "!!!111,!!!222,!!!333,!!!444"
        );
    }

    private <T> void executeNamedFunctionAndCheck(final FunctionExpressionName functionName,
                                                  final List<Expression> parameters,
                                                  final T expected) {
        final ConverterSpreadsheetExpressionEvaluationContext context = this.createContext();
        final ExpressionFunction<T, ExpressionEvaluationContext> function2 =
                Cast.to(
                        context.expressionFunction(functionName)
                );

        this.checkEquals(
                expected,
                function2.apply(
                        context.prepareParameters(
                                function2,
                                Cast.to(parameters)
                        ),
                        context
                ),
                () -> "" + Expression.call(
                        Expression.namedFunction(functionName),
                        parameters
                )
        );
    }

    // helpers..........................................................................................................

    @Override
    public ConverterSpreadsheetExpressionEvaluationContext createContext() {
        return ConverterSpreadsheetExpressionEvaluationContext.with(
                new Converter<>() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type,
                                              final SpreadsheetExpressionEvaluationContext context) {
                        return value instanceof ExpressionNumber && String.class == type;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> type,
                                                         final SpreadsheetExpressionEvaluationContext context) {
                        if(this.canConvert(value, type, context)) {
                            if (type == String.class) {
                                return this.successfulConversion(
                                        "!!!" + value,
                                        type
                                );
                            }
                        }
                        return this.failConversion(value, type);
                    }
                },
                SpreadsheetExpressionEvaluationContexts.basic(
                        CELL,
                        CELL_STORE,
                        SERVER_URL,
                        METADATA,
                        SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                                METADATA,
                                SpreadsheetFormatterProviders.spreadsheetFormatPattern(),
                                SpreadsheetParserProviders.spreadsheetParsePattern(
                                        SpreadsheetFormatterProviders.fake()
                                )
                        ),
                        EXPRESSION_FUNCTION_PROVIDER,
                        REFERENCES,
                        LABEL_NAME_RESOLVER,
                        LocalDateTime::now
                )
        );
    }

    // ToString........................................................................................................

    @Test
    public void testToString() {
        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
                ConverterSpreadsheetExpressionEvaluationContext.with(
                        converter,
                        context
                ),
                converter + " " + context
        );
    }

    // DecimalNumberContext............................................................................................

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
    public char groupSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return DECIMAL_NUMBER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_CONTEXT.positiveSign();
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return Cast.to(ConverterSpreadsheetExpressionEvaluationContext.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
