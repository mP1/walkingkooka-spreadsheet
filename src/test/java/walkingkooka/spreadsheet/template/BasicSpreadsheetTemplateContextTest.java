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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionReference;

import java.math.MathContext;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetTemplateContextTest implements SpreadsheetTemplateContextTesting<BasicSpreadsheetTemplateContext>,
        SpreadsheetMetadataTesting,
        DecimalNumberContextDelegator {

    private final static Function<TemplateValueName, Expression> NAME_TO_EXPRESSION = (n) -> {
        throw new UnsupportedOperationException();
    };
    private static final FakeSpreadsheetExpressionEvaluationContext SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT = new FakeSpreadsheetExpressionEvaluationContext() {

        @Override
        public SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
            return SpreadsheetExpressionEvaluationContexts.fake();
        }
    };

    @Override
    public void testLoadCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadCellRangeWithNullRangeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadLabelWithNullLabelFails() {
        throw new UnsupportedOperationException();
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

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetParserContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetTemplateContext.with(
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
                () -> BasicSpreadsheetTemplateContext.with(
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
                () -> BasicSpreadsheetTemplateContext.with(
                        SPREADSHEET_PARSER_CONTEXT,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        null
                )
        );
    }

    // SpreadsheetTemplateContext.......................................................................................

    @Override
    public BasicSpreadsheetTemplateContext createContext() {
        return BasicSpreadsheetTemplateContext.with(
                SPREADSHEET_PARSER_CONTEXT,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                NAME_TO_EXPRESSION
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return SPREADSHEET_PARSER_CONTEXT;
    }

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_PARSER_CONTEXT.mathContext();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetTemplateContext> type() {
        return BasicSpreadsheetTemplateContext.class;
    }
}
