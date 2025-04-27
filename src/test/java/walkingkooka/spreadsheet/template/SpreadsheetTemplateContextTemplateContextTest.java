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
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.storage.StorageStore;
import walkingkooka.storage.StorageStores;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateContextTesting2;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.FormHandlerContexts;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTemplateContextTemplateContextTest implements TemplateContextTesting2<SpreadsheetTemplateContextTemplateContext>,
        SpreadsheetMetadataTesting {

    private final static TemplateValueName TEMPLATE_NAME_1 = TemplateValueName.with("TemplateValue111");

    private final static Expression TEMPLATE_VALUE_1 = Expression.value(
            EXPRESSION_NUMBER_KIND.create(111)
    );

    private final static TemplateValueName TEMPLATE_NAME_2 = TemplateValueName.with("TemplateValue222");

    private final static Expression TEMPLATE_VALUE_2 = Expression.value("Hello");

    private final static TemplateValueName TEMPLATE_NAME_3 = TemplateValueName.with("TemplateValue333");

    private final static Expression TEMPLATE_VALUE_3 = Expression.subtract(
            Expression.reference(
                    TEMPLATE_NAME_1
            ),
            Expression.value(99)
    );

    // with.............................................................................................................

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetTemplateContextTemplateContext.with(
                        null
                )
        );
    }

    // parseAndRenderToString...........................................................................................

    @Test
    public void testSpreadsheetMetadataTestingSpreadsheetFormatterContextFormatNumber() {
        this.checkEquals(
                Optional.of(
                        TextNode.text("111.")
                ),
                SPREADSHEET_FORMATTER_CONTEXT.format(
                        Optional.of(
                                EXPRESSION_NUMBER_KIND.create(111)
                        )
                )
        );
    }

    @Test
    public void testParseAndRenderWithNumberParameter() {
        this.parseAndRenderToStringAndCheck(
                "Apple ${TemplateValue111} Banana",
                "Apple 111. Banana"
        );
    }

    @Test
    public void testParseAndRenderWithStringParameter() {
        this.parseAndRenderToStringAndCheck(
                "Apple ${TemplateValue222} Banana",
                "Apple Hello Banana"
        );
    }

    @Test
    public void testParseAndRenderWithStringParameterWithTrailingSpace() {
        this.parseAndRenderToStringAndCheck(
                "Apple ${  TemplateValue222 } Banana",
                "Apple Hello Banana"
        );
    }

    @Test
    public void testParseAndRenderWithTemplateValueReferencingAnother() {
        this.parseAndRenderToStringAndCheck(
                "Apple ${TemplateValue333} Carrot",
                "Apple 12. Carrot"
        );
    }

    @Test
    public void testParseAndRenderWithNumber() {
        this.parseAndRenderToStringAndCheck(
                "${123}",
                "123."
        );
    }

    @Test
    public void testParseAndRenderWithNumberTrailingWhitespace() {
        this.parseAndRenderToStringAndCheck(
                "${123   }",
                "123."
        );
    }

    @Test
    public void testParseAndRenderWithMathExpression() {
        this.parseAndRenderToStringAndCheck(
                "${1+2}",
                "3."
        );
    }

    @Test
    public void testParseAndRenderWithMathExpressionIncludesExtraWhitespace() {
        this.parseAndRenderToStringAndCheck(
                "${  1  +  2  }",
                "3."
        );
    }

    @Test
    public void testParseAndRenderWithFunctionCall() {
        this.parseAndRenderToStringAndCheck(
                "${hello(\"a1\", \"b2\", \"c3\")}",
                "a1, b2, c3"
        );
    }

    // verifies that '}' with double quote doesnt confuse expression parsing
    @Test
    public void testParseAndRenderWithFunctionCall2() {
        this.parseAndRenderToStringAndCheck(
                "${hello(\"}}\", \"b2\", \"c3\")}",
                "}}, b2, c3"
        );
    }


    // TemplateContext..................................................................................................

    @Override
    public SpreadsheetTemplateContextTemplateContext createContext() {
        return SpreadsheetTemplateContextTemplateContext.with(
                SpreadsheetTemplateContexts.basic(
                        SPREADSHEET_PARSER_CONTEXT,
                        SpreadsheetExpressionEvaluationContexts.basic(
                                Optional.empty(), // no cell
                                SpreadsheetExpressionReferenceLoaders.fake(),
                                Url.parseAbsolute("https://example.com"), // serverUrl
                                SpreadsheetMetadata.EMPTY,
                                new FakeSpreadsheetStoreRepository() {
                                    @Override
                                    public StorageStore storage() {
                                        return this.storage;
                                    }

                                    private final StorageStore storage = StorageStores.tree(STORAGE_STORE_CONTEXT);
                                },
                                SPREADSHEET_FORMATTER_CONTEXT, // SpreadsheetConverterContext
                                FormHandlerContexts.fake(),
                                new FakeExpressionFunctionProvider() {
                                    @Override
                                    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                                 final List<?> values,
                                                                                                                 final ProviderContext context) {
                                        checkEquals(
                                                "hello",
                                                name.value(),
                                                "function name"
                                        );

                                        return Cast.to(
                                                new FakeExpressionFunction<String, SpreadsheetExpressionEvaluationContext>() {
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
                                                }
                                        );
                                    }
                                },
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
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetTemplateContextTemplateContext> type() {
        return SpreadsheetTemplateContextTemplateContext.class;
    }

    @Override
    public String typeNameSuffix() {
        return TemplateContext.class.getSimpleName();
    }
}
