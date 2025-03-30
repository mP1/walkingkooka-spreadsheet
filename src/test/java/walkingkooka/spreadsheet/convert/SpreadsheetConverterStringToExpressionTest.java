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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;

import java.util.Optional;

public final class SpreadsheetConverterStringToExpressionTest extends SpreadsheetConverterTestCase<SpreadsheetConverterStringToExpression>
        implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertEmptyString() {
        this.convertFails(
                SpreadsheetConverterStringToExpression.INSTANCE,
                "",
                Expression.class,
                this.createContext(),
                "Empty \"text\""
        );
    }

    @Test
    public void testConvertInvalidExpression() {
        this.convertFails(
                SpreadsheetConverterStringToExpression.INSTANCE,
                "1+xyz(",
                Expression.class,
                this.createContext(),
                "Invalid character \'(\' at 5 in \"1+xyz(\""
        );
    }

    @Test
    public void testConvertAddExpression() {
        this.convertStringAndEvaluateExpression(
                "1+2.5",
                EXPRESSION_NUMBER_KIND.create(1 + 2.5)
        );
    }

    @Test
    public void testConvertComplexExpression() {
        this.convertStringAndEvaluateExpression(
                "1+2*3",
                EXPRESSION_NUMBER_KIND.create(1 + 2 * 3)
        );
    }

    private void convertStringAndEvaluateExpression(final String string,
                                                    final Object expected) {
        final Expression expression = this.convert(
                string,
                Expression.class
        ).leftValue();

        this.checkEquals(
                expected,
                expression.toValue(
                        SpreadsheetExpressionEvaluationContexts.basic(
                                Optional.empty(), // cell
                                SpreadsheetExpressionReferenceLoaders.fake(),
                                Url.parseAbsolute("https://example.com"), // serverUrl
                                METADATA_EN_AU,
                                SpreadsheetStoreRepositories.fake(),
                                SPREADSHEET_FORMATTER_CONTEXT,
                                ExpressionFunctionProviders.fake(),
                                ProviderContexts.fake()
                        )
                )
        );
    }

    @Override
    public SpreadsheetConverterStringToExpression createConverter() {
        return SpreadsheetConverterStringToExpression.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetConverterStringToExpression.INSTANCE,
                "String to Expression"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterStringToExpression> type() {
        return SpreadsheetConverterStringToExpression.class;
    }
}
