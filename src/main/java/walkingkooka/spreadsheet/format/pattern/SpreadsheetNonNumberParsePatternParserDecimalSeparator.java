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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.formula.DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;

/**
 * A {@link Parser} that matches the {@link SpreadsheetParserContext#decimalSeparator()} and returns a {@link DecimalSeparatorSymbolSpreadsheetFormulaParserToken}
 */
final class SpreadsheetNonNumberParsePatternParserDecimalSeparator extends SpreadsheetNonNumberParsePatternParser {

    /**
     * Singleton
     */
    final static SpreadsheetNonNumberParsePatternParserDecimalSeparator INSTANCE = new SpreadsheetNonNumberParsePatternParserDecimalSeparator();

    private SpreadsheetNonNumberParsePatternParserDecimalSeparator() {
        super();
    }

    @Override
    DecimalSeparatorSymbolSpreadsheetFormulaParserToken parseNotEmpty0(final TextCursor cursor,
                                                                       final SpreadsheetParserContext context,
                                                                       final TextCursorSavePoint start) {
        DecimalSeparatorSymbolSpreadsheetFormulaParserToken token = null;

        final char decimal = context.decimalSeparator();
        if (cursor.at() == decimal) {
            cursor.next();

            final String text = String.valueOf(decimal);
            token = SpreadsheetFormulaParserToken.decimalSeparatorSymbol(text, text);
        }

        return token;
    }

    @Override
    public String toString() {
        return ".";
    }
}
