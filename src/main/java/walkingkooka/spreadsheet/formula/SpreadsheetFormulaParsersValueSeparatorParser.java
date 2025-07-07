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

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ValueSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RequiredParser;

import java.util.Optional;

/**
 * This {@link Parser} attempts to match the {@link SpreadsheetParserContext#valueSeparator()} and then creates a {@link ValueSeparatorSymbolSpreadsheetFormulaParserToken}.
 */
final class SpreadsheetFormulaParsersValueSeparatorParser implements Parser<SpreadsheetParserContext>,
    RequiredParser<SpreadsheetParserContext> {

    /**
     * Singleton
     */
    final static SpreadsheetFormulaParsersValueSeparatorParser INSTANCE = new SpreadsheetFormulaParsersValueSeparatorParser();

    private SpreadsheetFormulaParsersValueSeparatorParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetParserContext context) {

        ValueSeparatorSymbolSpreadsheetFormulaParserToken token = null;
        if (cursor.isNotEmpty()) {
            final char valueSeparator = context.valueSeparator();
            final char c = cursor.at();
            if (c == valueSeparator) {
                final String text = Character.toString(valueSeparator);
                token = SpreadsheetFormulaParserToken.valueSeparatorSymbol(text, text);

                cursor.next();
            }
        }

        return Optional.ofNullable(token);
    }

    @Override
    public String toString() {
        return ",";
    }
}
