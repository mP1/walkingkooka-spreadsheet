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

package walkingkooka.spreadsheet.parser;

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.tree.expression.ExpressionNumberContext;

public final class SpreadsheetParserContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetParserContext}
     */
    public static SpreadsheetParserContext basic(final DateTimeContext dateTimeContext,
                                                 final ExpressionNumberContext expressionNumberContext,
                                                 final char valueSeparator) {
        return BasicSpreadsheetParserContext.with(
                dateTimeContext,
                expressionNumberContext,
                valueSeparator
        );
    }

    /**
     * {@see FakeSpreadsheetParserContext}
     */
    public static SpreadsheetParserContext fake() {
        return new FakeSpreadsheetParserContext();
    }

    /**
     * Stop creation.
     */
    private SpreadsheetParserContexts() {
        throw new UnsupportedOperationException();
    }
}
