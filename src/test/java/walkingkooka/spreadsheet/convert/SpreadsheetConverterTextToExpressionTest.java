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
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;

public final class SpreadsheetConverterTextToExpressionTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToExpression>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertStringToStringFails() {
        this.convertFails(
            SpreadsheetConverterTextToExpression.INSTANCE,
            "1+1",
            String.class,
            this.createContext()
        );
    }

    @Test
    public void testConvertEmptyStringToExpression() {
        this.convertFails(
            SpreadsheetConverterTextToExpression.INSTANCE,
            "",
            Expression.class,
            this.createContext(),
            "Empty \"text\""
        );
    }

    @Test
    public void testConvertInvalidExpressionStringToExpression() {
        this.convertFails(
            SpreadsheetConverterTextToExpression.INSTANCE,
            "1+xyz(",
            Expression.class,
            this.createContext(),
            "Invalid character \'(\' at 5"
        );
    }

    @Test
    public void testConvertStringAddExpressionToExpression() {
        this.convertStringAndEvaluateExpression(
            "1+2.5",
            EXPRESSION_NUMBER_KIND.create(1 + 2.5)
        );
    }

    @Test
    public void testConvertStringBuilderToExpressionWithAddExpressionToExpression() {
        this.convertStringAndEvaluateExpression(
            new StringBuilder("1+2.5"),
            EXPRESSION_NUMBER_KIND.create(1 + 2.5)
        );
    }

    @Test
    public void testConvertStringComplexExpressionToExpression() {
        this.convertStringAndEvaluateExpression(
            "1+2*3",
            EXPRESSION_NUMBER_KIND.create(1 + 2 * 3)
        );
    }

    private void convertStringAndEvaluateExpression(final CharSequence string,
                                                    final Object expected) {
        final Expression expression = this.convert(
            string,
            Expression.class
        ).leftValue();

        this.checkEquals(
            expected,
            expression.toValue(
                SpreadsheetExpressionEvaluationContexts.basic(
                    METADATA_EN_AU,
                    SpreadsheetMetadataMode.FORMULA,
                    SpreadsheetStoreRepositories.fake(),
                    SPREADSHEET_ENVIRONMENT_CONTEXT,
                    SpreadsheetExpressionEvaluationContext.NO_CELL,
                    SpreadsheetExpressionReferenceLoaders.fake(),
                    SPREADSHEET_LABEL_NAME_RESOLVER,
                    LOCALE_CONTEXT,
                    TERMINAL_CONTEXT,
                    SPREADSHEET_PROVIDER,
                    ProviderContexts.fake()
                )
            )
        );
    }

    @Test
    public void testConvertStringWithValueSeparatorToExpression() {
        this.convertAndCheck(
            "list(\"a\",\"b\")",
            Expression.class,
            Expression.call(
                Expression.namedFunction(
                    ExpressionFunctionName.with("list")
                ),
                Lists.of(
                    Expression.value("a"),
                    Expression.value("b")
                )
            )
        );
    }

    @Override
    public SpreadsheetConverterTextToExpression createConverter() {
        return SpreadsheetConverterTextToExpression.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterTextToExpression.INSTANCE,
            "String to Expression"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterTextToExpression> type() {
        return SpreadsheetConverterTextToExpression.class;
    }
}
