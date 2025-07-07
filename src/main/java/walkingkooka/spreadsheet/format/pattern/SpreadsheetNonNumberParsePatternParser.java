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

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RequiredParser;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract {@link Parser} that requires a {@link SpreadsheetParserContext} that provides some template methods.
 */
abstract class SpreadsheetNonNumberParsePatternParser implements Parser<SpreadsheetParserContext>,
    RequiredParser<SpreadsheetParserContext> {

    /**
     * @see SpreadsheetNonNumberParsePatternParserDecimalSeparator
     */
    static SpreadsheetNonNumberParsePatternParserDecimalSeparator decimalSeparator() {
        return SpreadsheetNonNumberParsePatternParserDecimalSeparator.INSTANCE;
    }

    /**
     * @see SpreadsheetNonNumberParsePatternParserMilliseconds
     */
    static SpreadsheetNonNumberParsePatternParserMilliseconds milliseconds(final String pattern) {
        return SpreadsheetNonNumberParsePatternParserMilliseconds.with(pattern);
    }

    /**
     * @see SpreadsheetNonNumberParsePatternParserString
     */
    static SpreadsheetNonNumberParsePatternParserString stringChoices(final Function<SpreadsheetParserContext, List<String>> values,
                                                                      final BiFunction<Integer, String, SpreadsheetFormulaParserToken> tokenFactory,
                                                                      final String pattern) {
        return SpreadsheetNonNumberParsePatternParserString.with(
            values,
            tokenFactory,
            pattern
        );
    }

    SpreadsheetNonNumberParsePatternParser() {
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

        final SpreadsheetFormulaParserToken token = this.parseNotEmpty0(cursor, context, save);
        if (null == token) {
            save.restore();
        }

        return Optional.ofNullable(token);
    }

    /**
     * This method is only called when the {@link TextCursor} is not empty, with at least one character.
     */
    abstract SpreadsheetFormulaParserToken parseNotEmpty0(final TextCursor cursor,
                                                          final SpreadsheetParserContext context,
                                                          final TextCursorSavePoint start);

    @Override
    public abstract String toString();
}
