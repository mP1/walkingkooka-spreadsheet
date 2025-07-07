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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

public abstract class SpreadsheetColumnOrRowReferenceSpreadsheetParserTestCase<P extends SpreadsheetColumnOrRowReferenceSpreadsheetParser> implements SpreadsheetParserTesting2<P>, ClassTesting2<P> {

    SpreadsheetColumnOrRowReferenceSpreadsheetParserTestCase() {
        super();
    }

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    final static char VALUE_SEPARATOR = ',';

    @Test
    public final void testMinCount() {
        this.minCountAndCheck(
            1
        );
    }

    @Test
    public final void testMaxCount() {
        this.maxCountAndCheck(
            1
        );
    }

    @Override
    public final SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.POSITION,
            this.dateTimeContext(),
            ExpressionNumberContexts.basic(
                EXPRESSION_NUMBER_KIND,
                this.decimalNumberContext()
            ),
            VALUE_SEPARATOR
        );
    }

    // tokens...........................................................................................................

    @Test
    public final void testTokens() {
        this.tokensAndCheck(
            this.createContext()
        );
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
