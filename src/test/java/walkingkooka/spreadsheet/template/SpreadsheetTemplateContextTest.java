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

package walkingkooka.spreadsheet.template;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.template.TemplateContextTesting2;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTemplateContextTest implements TemplateContextTesting2<SpreadsheetTemplateContext>,
    SpreadsheetMetadataTesting {

    private static final SpreadsheetExpressionEvaluationContext SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT = SpreadsheetExpressionEvaluationContexts.fake();

    private final static Function<TemplateValueName, Expression> NAME_TO_EXPRESSION = (n) -> {
        throw new UnsupportedOperationException();
    };

    private final static TemplateValueName TEMPLATE_NAME_1 = TemplateValueName.with("TemplateValue111");

    private final static Expression TEMPLATE_VALUE_1 = Expression.value(
        EXPRESSION_NUMBER_KIND.create(111)
    );

    private final static TemplateValueName TEMPLATE_NAME_2 = TemplateValueName.with("TemplateValue222");

    private final static Expression TEMPLATE_VALUE_2 = Expression.value("Hello");

    private final static TemplateValueName TEMPLATE_NAME_3 = TemplateValueName.with("TemplateValue333");

    // 99 + 111
    private final static Expression TEMPLATE_VALUE_3 = Expression.add(
        Expression.value(99),
        Expression.reference(
            TEMPLATE_NAME_1
        )
    );

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetParserContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetTemplateContext.with(
                null,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                NAME_TO_EXPRESSION
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetExpressionEvaluationContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetTemplateContext.with(
                SPREADSHEET_PARSER_CONTEXT,
                null,
                NAME_TO_EXPRESSION
            )
        );
    }

    @Test
    public void testWithNullTemplateValueNameToExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetTemplateContext.with(
                SPREADSHEET_PARSER_CONTEXT,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                null
            )
        );
    }

    // parseTemplateAndRenderToString...................................................................................

    @Test
    public void testSpreadsheetMetadataTestingSpreadsheetFormatterContextFormatValueNumber() {
        this.checkEquals(
            Optional.of(
                TextNode.text("111.")
            ),
            SPREADSHEET_FORMATTER_CONTEXT.formatValue(
                Optional.of(
                    EXPRESSION_NUMBER_KIND.create(111)
                )
            )
        );
    }

    @Test
    public void testParseTemplateAndRenderWithNumberParameter() {
        this.parseTemplateAndRenderToStringAndCheck(
            "Apple ${TemplateValue111} Banana",
            "Apple 111 Banana"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithStringParameter() {
        this.parseTemplateAndRenderToStringAndCheck(
            "Apple ${TemplateValue222} Banana",
            "Apple Hello Banana"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithStringParameterWithTrailingSpace() {
        this.parseTemplateAndRenderToStringAndCheck(
            "Apple ${  TemplateValue222 } Banana",
            "Apple Hello Banana"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithTemplateValueReferencingAnother() {
        this.parseTemplateAndRenderToStringAndCheck(
            "Apple ${TemplateValue333} Carrot",
            "Apple 210 Carrot"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithNumber() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${123}",
            "123"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithNumberTrailingWhitespace() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${123   }",
            "123"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithMathExpression() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${1+2}",
            "3"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithMathExpressionIncludesExtraWhitespace() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${  1  +  2  }",
            "3"
        );
    }

    @Test
    public void testParseTemplateAndRenderWithFunctionCall() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${hello(\"a1\", \"b2\", \"c3\")}",
            "a1, b2, c3"
        );
    }

    // verifies that '}' with double quote doesnt confuse expression parsing
    @Test
    public void testParseTemplateAndRenderWithFunctionCall2() {
        this.parseTemplateAndRenderToStringAndCheck(
            "${hello(\"}}\", \"b2\", \"c3\")}",
            "}}, b2, c3"
        );
    }

    // TemplateContext..................................................................................................

    @Override
    public SpreadsheetTemplateContext createContext() {
        return SpreadsheetTemplateContext.with(
            SPREADSHEET_PARSER_CONTEXT,
            SpreadsheetExpressionEvaluationContexts.basic(
                Url.parseAbsolute("https://example.com"), // serverUrl
                METADATA_EN_AU.set(
                    SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                    SpreadsheetExpressionFunctions.parseAliasSet("hello")
                ),
                SpreadsheetMetadataMode.FORMULA,
                new FakeSpreadsheetStoreRepository() {

                    @Override
                    public Storage<StorageExpressionEvaluationContext> storage() {
                        return this.storage;
                    }

                    private final Storage<StorageExpressionEvaluationContext> storage = Storages.tree();
                },
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                SpreadsheetExpressionEvaluationContext.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SpreadsheetProviders.basic(
                    SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                        (ProviderContext p) -> METADATA_EN_AU.dateTimeConverter(
                            SPREADSHEET_FORMATTER_PROVIDER,
                            SPREADSHEET_PARSER_PROVIDER,
                            p
                        )
                    ),
                    new FakeExpressionFunctionProvider<>() {
                        @Override
                        public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                                final List<?> values,
                                                                                                                final ProviderContext context) {
                            checkEquals(
                                "hello",
                                name.value(),
                                "function name"
                            );

                            return new FakeExpressionFunction<String, SpreadsheetExpressionEvaluationContext>() {
                                @Override
                                public String apply(final List<Object> values,
                                                    final SpreadsheetExpressionEvaluationContext context) {
                                    return values.stream()
                                        .map(Objects::toString)
                                        .collect(Collectors.joining(", "));
                                }

                                @Override
                                public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                    return Collections.nCopies(
                                        3,
                                        ExpressionFunctionParameter.STRING.setKinds(
                                            Sets.of(ExpressionFunctionParameterKind.EVALUATE)
                                        )
                                    );
                                }

                                @Override
                                public Class<String> returnType() {
                                    return String.class;
                                }

                                @Override
                                public Optional<ExpressionFunctionName> name() {
                                    return Optional.of(name);
                                }
                            };
                        }

                        @Override
                        public ExpressionFunctionInfoSet expressionFunctionInfos() {
                            return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/Hello Hello");
                        }

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
                PROVIDER_CONTEXT
            ),
            (t) -> {
                if (t.equals(TEMPLATE_NAME_1)) {
                    return TEMPLATE_VALUE_1;
                }
                if (t.equals(TEMPLATE_NAME_2)) {
                    return TEMPLATE_VALUE_2;
                }
                if (t.equals(TEMPLATE_NAME_3)) {
                    return TEMPLATE_VALUE_3;
                }

                throw new AssertionError("Unknown template value name: " + t);
            }
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetTemplateContext> type() {
        return SpreadsheetTemplateContext.class;
    }
}
