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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;

import java.util.function.Function;

public final class SpreadsheetTemplateContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetTemplateContext}
     */
    public static SpreadsheetTemplateContext basic(final SpreadsheetParserContext spreadsheetParserContext,
                                                   final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                                   final Function<TemplateValueName, Expression> templateValueNameToExpression) {
        return BasicSpreadsheetTemplateContext.with(
                spreadsheetParserContext,
                spreadsheetExpressionEvaluationContext,
                templateValueNameToExpression
        );
    }

    /**
     * {@see FakeSpreadsheetTemplateContext}
     */
    public static SpreadsheetTemplateContext fake() {
        return new FakeSpreadsheetTemplateContext();
    }

    /**
     * {@see SpreadsheetTemplateContextTemplateContext}
     */
    public static TemplateContext templateContext(final SpreadsheetTemplateContext context) {
        return SpreadsheetTemplateContextTemplateContext.with(
                context
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetTemplateContexts() {
        throw new UnsupportedOperationException();
    }
}
