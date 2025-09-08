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

import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContextDelegator;

import java.util.Locale;

public interface SpreadsheetParserContextDelegator extends SpreadsheetParserContext,
    DateTimeContextDelegator,
    ExpressionNumberContextDelegator {

    @Override
    default DateTimeContext dateTimeContext() {
        return this.spreadsheetParserContext();
    }

    @Override
    default char valueSeparator() {
        return this.spreadsheetParserContext()
            .valueSeparator();
    }

    @Override
    default ExpressionNumberContext expressionNumberContext() {
        return this.spreadsheetParserContext();
    }

    @Override
    default boolean isGroupSeparatorWithinNumbersSupported() {
        return this.spreadsheetParserContext()
            .isGroupSeparatorWithinNumbersSupported();
    }

    @Override
    default InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                final TextCursor cursor) {
        return this.spreadsheetParserContext()
            .invalidCharacterException(
                parser,
                cursor
            );
    }

    @Override
    default Locale locale() {
        return this.spreadsheetParserContext()
            .locale();
    }

    SpreadsheetParserContext spreadsheetParserContext();
}
