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

import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract {@link Parser} that requires a {@link SpreadsheetParserContext} that provides some template methods.
 */
abstract class SpreadsheetParsePattern2Parser implements Parser<SpreadsheetParserContext> {

    /**
     * @see SpreadsheetParsePattern2ParserDecimalSeparator
     */
    static SpreadsheetParsePattern2ParserDecimalSeparator decimalSeparator() {
        return SpreadsheetParsePattern2ParserDecimalSeparator.instance();
    }

    /**
     * @see SpreadsheetParsePattern2ParserMilliseconds
     */
    static SpreadsheetParsePattern2ParserMilliseconds milliseconds(final String pattern) {
        return SpreadsheetParsePattern2ParserMilliseconds.with(pattern);
    }

    /**
     * @see SpreadsheetParsePattern2ParserString
     */
    static SpreadsheetParsePattern2ParserString stringChoices(final Function<SpreadsheetParserContext, List<String>> values,
                                                              final BiFunction<Integer, String, SpreadsheetParserToken> tokenFactory,
                                                              final String pattern) {
        return SpreadsheetParsePattern2ParserString.with(
                values,
                tokenFactory,
                pattern
        );
    }

    SpreadsheetParsePattern2Parser() {
        super();
    }

    @Override
    public final Optional<ParserToken> parse(final TextCursor cursor,
                                             final SpreadsheetParserContext context) {
        return cursor.isEmpty() ?
                Optional.empty() :
                this.parseNotEmpty(cursor, context);
    }

    private Optional<ParserToken> parseNotEmpty(final TextCursor cursor,
                                                final SpreadsheetParserContext context) {
        final TextCursorSavePoint save = cursor.save();

        final SpreadsheetParserToken token = this.parseNotEmpty0(cursor, context, save);
        if (null == token) {
            save.restore();
        }

        return Optional.ofNullable(token);
    }

    /**
     * This method is only called when the {@link TextCursor} is not empty, with at least one character.
     */
    abstract SpreadsheetParserToken parseNotEmpty0(final TextCursor cursor,
                                                   final SpreadsheetParserContext context,
                                                   final TextCursorSavePoint start);

    @Override
    public abstract String toString();
}
