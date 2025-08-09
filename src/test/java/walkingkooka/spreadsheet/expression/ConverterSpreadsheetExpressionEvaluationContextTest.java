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
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.StorageStore;
import walkingkooka.storage.StorageStores;
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
import walkingkooka.validation.form.FakeFormHandlerContext;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting.LOCALE_CONTEXT;
import static walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting.STORAGE_STORE_CONTEXT;

public final class ConverterSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContext>,
    DecimalNumberContextDelegator {

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL128);

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("B2");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
        CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("=1+2"))
    );

    private final static SpreadsheetExpressionReferenceLoader SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT = SpreadsheetExpressionReferenceLoaders.fake();

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.EMPTY
        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-US"))
        .loadFromLocale(
            LocaleContexts.jre(Locale.forLanguageTag("EN-US"))
        ).set(SpreadsheetMetadataPropertyName.PRECISION, DECIMAL_NUMBER_CONTEXT.mathContext().getPrecision())
        .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, DECIMAL_NUMBER_CONTEXT.mathContext().getRoundingMode())
        .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, 0L)
        .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 20)
        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DEFAULT)
        .set(SpreadsheetMetadataPropertyName.FORMULA_CONVERTER, ConverterSelector.parse("general"))
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
     * A namedFunction that expects a number and returns that.
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

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

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
            (ProviderContext p) -> METADATA.generalConverter(
                SpreadsheetFormatterProviders.spreadsheetFormatters(),
                SpreadsheetParserProviders.spreadsheetParsePattern(
                    SpreadsheetFormatterProviders.fake()
                ),
                p
            )
        );

        return ConverterSpreadsheetExpressionEvaluationContext.with(
            converter,
            SpreadsheetExpressionEvaluationContexts.basic(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                METADATA,
                new FakeSpreadsheetStoreRepository() {
                    @Override
                    public StorageStore storage() {
                        return this.storage;
                    }

                    private final StorageStore storage = StorageStores.tree(STORAGE_STORE_CONTEXT);
                },
                METADATA.spreadsheetConverterContext(
                    CELL,
                    SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                    SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                    LABEL_NAME_RESOLVER,
                    converterProvider,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                ),
                ((Optional<SpreadsheetCell> cell) -> {
                    Objects.requireNonNull(cell, "cell");
                    throw new UnsupportedOperationException();
                }),
                new FakeFormHandlerContext<>() {
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
                },
                EXPRESSION_FUNCTION_PROVIDER,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
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
    public void testSetEnvironmentValueNameWithNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueNameWithNullValueFails() {
        throw new UnsupportedOperationException();
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

    // DecimalNumberContext.............................................................................................

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return Cast.to(ConverterSpreadsheetExpressionEvaluationContext.class);
    }
}
