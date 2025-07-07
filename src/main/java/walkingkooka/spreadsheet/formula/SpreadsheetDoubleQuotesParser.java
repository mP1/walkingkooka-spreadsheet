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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.formula.parser.DoubleQuoteSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RequiredParser;

import java.util.Objects;
import java.util.Optional;

/**
 * A parser that handles double-quoted strings within a formula, including support for embedding a double quote by inserting
 * two double quotes.
 * <a href="https://exceljet.net/formula/double-quotes-inside-a-formula">Double quotes inside a formula</a>
 * <pre>
 * Summary
 * To include double quotes inside a formula, you ca use additional double quotes. In the example shown, the formula in C5 is:
 *
 *  ="The movie """ &B5 &""" is good."
 * </pre>
 */
final class SpreadsheetDoubleQuotesParser implements Parser<SpreadsheetParserContext>,
    RequiredParser<SpreadsheetParserContext> {

    /**
     * Singleton instance
     */
    static final SpreadsheetDoubleQuotesParser INSTANCE = new SpreadsheetDoubleQuotesParser();

    private SpreadsheetDoubleQuotesParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        ParserToken token = null;

        if (cursor.isNotEmpty()) {
            // expect double quote
            final char first = cursor.at();
            if (DOUBLE_QUOTE == first) {
                final TextCursorSavePoint save = cursor.save();
                cursor.next();
                final StringBuilder content = new StringBuilder();

                while (cursor.isNotEmpty()) {
                    final char c = cursor.at();
                    cursor.next();

                    if (DOUBLE_QUOTE == c) {

                        // check if a double quote follows.
                        if (cursor.isEmpty()) {
                            // double quote was a string terminator
                            token = text(content, save);
                            break;
                        }

                        // must be a double quote
                        if (DOUBLE_QUOTE == cursor.at()) {
                            content.append(c);
                            cursor.next();
                            continue;
                        }

                        // was actually a terminator
                        token = text(content, save);
                        break;
                    }

                    content.append(c);
                }
            }
        }

        return Optional.ofNullable(token);
    }

    final static char DOUBLE_QUOTE = '"';

    /**
     * Factory that creates a {@link TextSpreadsheetFormulaParserToken} with three text literals, the surrounding double quotes,
     * and the content.
     */
    private static SpreadsheetFormulaParserToken text(final StringBuilder content, final TextCursorSavePoint save) {
        final String text = save.textBetween().toString();
        final String contentString = content.toString();

        return SpreadsheetFormulaParserToken.text(
            Lists.of(
                DOUBLE_QUOTE_TOKEN,
                SpreadsheetFormulaParserToken.textLiteral(
                    contentString, text.substring(1, text.length() - 1)
                ),
                DOUBLE_QUOTE_TOKEN
            ),
            text
        );
    }

    final static DoubleQuoteSymbolSpreadsheetFormulaParserToken DOUBLE_QUOTE_TOKEN = SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\"");

    @Override
    public String toString() {
        return "Text";
    }
}
