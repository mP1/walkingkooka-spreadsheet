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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.FakeSpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ExpressionSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<ExpressionSpreadsheetFormatter>,
    HashCodeEqualsDefinedTesting2<ExpressionSpreadsheetFormatter> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testFormatNonNull() {
        this.formatAndCheck(
            EXPRESSION_NUMBER_KIND.create(200), // value being formatted
            TextNode.text("201")
        );
    }

    @Test
    public void testToken() {
        this.tokensAndCheck(
            this.createContext(),
            Lists.empty()
        );
    }

    private final static ExpressionFunctionName HELLO = SpreadsheetExpressionFunctions.name("hello");

    @Override
    public ExpressionSpreadsheetFormatter createFormatter() {

        // 1 + hello($FORMAT_VALUE)
        return ExpressionSpreadsheetFormatter.with(
            Expression.add(
                Expression.value(EXPRESSION_NUMBER_KIND.one()),
                Expression.call(
                    Expression.namedFunction(HELLO),
                    Lists.of(
                        Expression.reference(SpreadsheetExpressionEvaluationContext.FORMAT_VALUE)
                    )
                )
            )
        );
    }

    @Override
    public Object value() {
        return "Expression";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        final Locale locale = Locale.forLanguageTag("en-AU");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            locale
        ).loadFromLocale(
            LocaleContexts.jre(locale)
        ).set(
            SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );

        final SpreadsheetConverterContext converterContext = SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.collection(
                Lists.of(
                    Converters.simple(),
                    new Converter<>() {
                        @Override
                        public boolean canConvert(final Object value,
                                                  final Class<?> type,
                                                  final SpreadsheetConverterContext context) {
                            return value instanceof ExpressionNumber && TextNode.class == type;
                        }

                        @Override
                        public <T> Either<T, String> convert(final Object value,
                                                             final Class<T> type,
                                                             final SpreadsheetConverterContext context) {
                            return this.successfulConversion(
                                TextNode.text(
                                    value.toString()
                                ),
                                type
                            );
                        }
                    }
                )
            ), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        ',', // valueSeparator
                        Converters.fake(),
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.american(MathContext.UNLIMITED)
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.fake()
        );

        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converterContext.canConvert(
                    value,
                    type
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return converterContext.convert(
                    value,
                    target
                );
            }

            @Override
            public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value) {
                return SpreadsheetExpressionEvaluationContexts.basic(
                    metadata,
                    SpreadsheetMetadataMode.FORMULA,
                    SpreadsheetStoreRepositories.fake(),
                    SpreadsheetEnvironmentContexts.fake(),
                    SpreadsheetExpressionEvaluationContext.NO_CELL,
                    SpreadsheetExpressionReferenceLoaders.fake(),
                    SpreadsheetLabelNameResolvers.fake(),
                    LocaleContexts.fake(),
                    TerminalContexts.fake(),
                    SpreadsheetProviders.basic(
                        SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                            (p) -> Converters.never()
                        ),
                        new FakeSpreadsheetProvider() {

                            @Override
                            public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionSelector selector,
                                                                                                                    final ProviderContext context) {
                                return this.expressionFunctionProvider.expressionFunction(
                                    selector,
                                    context
                                );
                            }

                            @Override
                            public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                                    final List<?> values,
                                                                                                                    final ProviderContext context) {
                                return this.expressionFunctionProvider.expressionFunction(
                                    name,
                                    values,
                                    context
                                );
                            }

                            @Override
                            public ExpressionFunctionInfoSet expressionFunctionInfos() {
                                return this.expressionFunctionProvider.expressionFunctionInfos();
                            }

                            private final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider = ExpressionFunctionProviders.basic(
                                Url.parseAbsolute("https://example.com/function"),
                                SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY,
                                Sets.of(
                                    new FakeExpressionFunction<>() {

                                        @Override
                                        public Optional<ExpressionFunctionName> name() {
                                            return Optional.of(HELLO);
                                        }

                                        @Override
                                        public Class<Object> returnType() {
                                            return Object.class;
                                        }

                                        @Override
                                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                            return Lists.of(VALUE);
                                        }

                                        private final ExpressionFunctionParameter<Object> VALUE = ExpressionFunctionParameter.VALUE.setKinds(ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES);

                                        @Override
                                        public Object apply(final List<Object> parameters,
                                                            final SpreadsheetExpressionEvaluationContext context) {
                                            return VALUE.getOrFail(
                                                parameters,
                                                0
                                            );
                                        }
                                    }
                                )
                            );

                            @Override
                            public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                                return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                            }
                        },
                        SpreadsheetComparatorProviders.fake(),
                        SpreadsheetExporterProviders.fake(),
                        SpreadsheetFormatterProviders.fake(),
                        FormHandlerProviders.fake(),
                        SpreadsheetImporterProviders.fake(),
                        SpreadsheetParserProviders.fake(),
                        ValidatorProviders.fake()
                    ),
                    ProviderContexts.fake()
                ).addLocalVariable(
                    SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                    value
                );
            }
        };
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentExpression() {
        this.checkNotEquals(
            ExpressionSpreadsheetFormatter.with(
                Expression.value("Different2")
            )
        );
    }

    @Override
    public ExpressionSpreadsheetFormatter createObject() {
        return ExpressionSpreadsheetFormatter.with(
            Expression.value("Hello1")
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            ExpressionSpreadsheetFormatter.with(
                Expression.add(
                    Expression.value(1),
                    Expression.value(23)
                )
            ),
            "ExpressionSpreadsheetFormatter\n" +
                "  AddExpression\n" +
                "    ValueExpression 1 (java.lang.Integer)\n" +
                "    ValueExpression 23 (java.lang.Integer)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<ExpressionSpreadsheetFormatter> type() {
        return ExpressionSpreadsheetFormatter.class;
    }
}
