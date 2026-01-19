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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.FakeSpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextConverterTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContext>,
    DecimalNumberContextDelegator,
    SpreadsheetMetadataTesting {

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL128);

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-US"))
        .loadFromLocale(
            LocaleContexts.jre(Locale.forLanguageTag("EN-US"))
        ).set(SpreadsheetMetadataPropertyName.PRECISION, DECIMAL_NUMBER_CONTEXT.mathContext().getPrecision())
        .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, DECIMAL_NUMBER_CONTEXT.mathContext().getRoundingMode())
        .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, 0L)
        .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
        .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 20)
        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DEFAULT)
        .set(SpreadsheetMetadataPropertyName.FORMULA_CONVERTER, ConverterSelector.parse("collection(text, number, basic, spreadsheet-value)"))
        .set(SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS, SpreadsheetExpressionFunctions.parseAliasSet("test-concat-1, test-echo-2"))
        .set(SpreadsheetMetadataPropertyName.FUNCTIONS, SpreadsheetExpressionFunctions.parseAliasSet("test-concat-1, test-echo-2"))
        .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
        .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20)
        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
        .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("$#.##").spreadsheetFormatterSelector())
        .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SPREADSHEET_ID);

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
        public Optional<ExpressionFunctionName> name() {
            return Optional.of(
                SpreadsheetExpressionFunctions.name("test-concat-1")
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
     * A {@link ExpressionFunction} that expects a number and returns that.
     */
    private final static ExpressionFunction<ExpressionNumber, SpreadsheetExpressionEvaluationContext> ECHO = new FakeExpressionFunction<>() {
        @Override
        public ExpressionNumber apply(final List<Object> parameters,
                                      final SpreadsheetExpressionEvaluationContext context) {
            return NUMBER.getOrFail(parameters, 0);
        }

        @Override
        public Optional<ExpressionFunctionName> name() {
            return Optional.of(
                SpreadsheetExpressionFunctions.name("test-echo-2")
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

    private final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> EXPRESSION_FUNCTION_PROVIDER = new ExpressionFunctionProvider<>() {

        @Override
        public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionSelector selector,
                                                                                                final ProviderContext context) {
            Objects.requireNonNull(selector, "selector");
            Objects.requireNonNull(context, "context");

            return selector.evaluateValueText(
                this,
                context
            );
        }

        @Override
        public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName n,
                                                                                                final List<?> v,
                                                                                                final ProviderContext c) {
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
        public ExpressionFunctionInfoSet expressionFunctionInfos() {
            return SpreadsheetExpressionFunctions.infoSet(
                Sets.of(
                    SpreadsheetExpressionFunctions.info(
                        Url.parseAbsolute("https://example.com/test/" + CONCAT),
                        CONCAT.name().get()
                    ),
                    SpreadsheetExpressionFunctions.info(
                        Url.parseAbsolute("https://example.com/test/" + ECHO),
                        ECHO.name().get()
                    )
                )
            );
        }

        @Override
        public CaseSensitivity expressionFunctionNameCaseSensitivity() {
            return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
        }
    };

    // tests............................................................................................................

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextConverter.with(
                null,
                SpreadsheetExpressionEvaluationContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextConverter.with(
                Converters.fake(),
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final SpreadsheetExpressionEvaluationContextConverter created = SpreadsheetExpressionEvaluationContextConverter.with(
            converter,
            context
        );

        assertSame(converter, created.converter, "converter");
        assertSame(context, created.context, "context");
    }

    @Test
    public void testWithDoubleWrap() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        final SpreadsheetExpressionEvaluationContextConverter first = SpreadsheetExpressionEvaluationContextConverter.with(
            Converters.fake(),
            context
        );

        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final SpreadsheetExpressionEvaluationContextConverter doubleWrapped = SpreadsheetExpressionEvaluationContextConverter.with(
            converter,
            first
        );

        assertSame(converter, doubleWrapped.converter, "converter");
        assertSame(context, doubleWrapped.context, "context");
    }

    // evaluate.........................................................................................................

    @Override
    public void testEvaluateWithEmptyStringReturnsError() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateWithWhitespaceStringReturnsError() {
        throw new UnsupportedOperationException();
    }

    // evaluateExpression...............................................................................................

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullParametersFails() {
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
            "111,222"
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
            "111"
        );
    }

    @Test
    public void testEvaluateExpression() {
        final ExpressionNumber value = EXPRESSION_NUMBER_KIND.create(111);

        this.evaluateExpressionAndCheck(
            Expression.value(
                value
            ),
            value
        );
    }

    @Test
    public void testEvaluateExpression2() {
        this.evaluateExpressionAndCheck(
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

    private <T> void executeNamedFunctionAndCheck(final ExpressionFunctionName functionName,
                                                  final List<Expression> parameters,
                                                  final T expected) {
        final SpreadsheetExpressionEvaluationContextConverter context = this.createContext();
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

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    // helpers..........................................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContextConverter createContext() {
        final Converter<SpreadsheetExpressionEvaluationContext> converter = new Converter<>() {
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
                if (this.canConvert(value, type, context)) {
                    if (type == String.class) {
                        return this.successfulConversion(
                            "!!!" + value,
                            type
                        );
                    }
                }
                return this.failConversion(value, type);
            }
        };

        final ConverterProvider converterProvider = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            (ProviderContext p) -> METADATA.dateTimeConverter(
                SpreadsheetFormatterProviders.spreadsheetFormatters(),
                SpreadsheetParserProviders.spreadsheetParsePattern(
                    SpreadsheetFormatterProviders.fake()
                ),
                p
            )
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(SPREADSHEET_ID);

        return SpreadsheetExpressionEvaluationContextConverter.with(
            converter,
            SpreadsheetExpressionEvaluationContexts.spreadsheetContext(
                SpreadsheetMetadataMode.FORMULA,
                SpreadsheetExpressionEvaluationContext.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SpreadsheetLabelNameResolvers.fake(),
                SpreadsheetContexts.fixedSpreadsheetId(
                    SpreadsheetEngines.basic(),
                    new FakeSpreadsheetStoreRepository() {
                        @Override
                        public SpreadsheetMetadataStore metadatas() {
                            return new FakeSpreadsheetMetadataStore() {
                                @Override
                                public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
                                    return Optional.ofNullable(
                                        id.equals(SPREADSHEET_ID) ?
                                            METADATA :
                                            null
                                    );
                                }
                            };
                        }
                    },
                    (c) -> {
                        throw new UnsupportedOperationException();
                    }, // Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory
                    (c) -> {
                        throw new UnsupportedOperationException();
                    }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
                    spreadsheetEnvironmentContext,
                    LOCALE_CONTEXT,
                    SpreadsheetProviders.basic(
                        converterProvider,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SpreadsheetComparatorProviders.empty(),
                        SpreadsheetExporterProviders.empty(),
                        SpreadsheetFormatterProviders.empty(),
                        FormHandlerProviders.empty(),
                        SpreadsheetImporterProviders.empty(),
                        SpreadsheetParserProviders.empty(),
                        ValidatorProviders.empty()
                    ),
                    ProviderContexts.basic(
                        ConverterContexts.fake(),
                        SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                        PluginStores.fake()
                    )
                ),
                TerminalContexts.fake()
            )
        );
    }

    @Override
    public void testLoadLabelWithNullLabelFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNullValueFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    // ToString........................................................................................................

    @Test
    public void testToString() {
        final Converter<SpreadsheetExpressionEvaluationContext> converter = Converters.fake();
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
            SpreadsheetExpressionEvaluationContextConverter.with(
                converter,
                context
            ),
            converter + " " + context
        );
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return Cast.to(SpreadsheetExpressionEvaluationContextConverter.class);
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
