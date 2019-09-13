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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;

import java.util.List;
import java.util.Optional;

/**
 * The {@link Parser} returned by {@link SpreadsheetNumberParsePatterns#converter()}, that tries each pattern until a
 * {@link BigDecimalParserToken} is created.
 */
final class SpreadsheetNumberParsePatternsParser implements Parser<ParserContext> {

    static SpreadsheetNumberParsePatternsParser with(final SpreadsheetNumberParsePatterns pattern) {
        return new SpreadsheetNumberParsePatternsParser(pattern);
    }

    private SpreadsheetNumberParsePatternsParser(final SpreadsheetNumberParsePatterns pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final ParserContext context) {
        BigDecimalParserToken token = null;

        final TextCursorSavePoint save = cursor.save();

        for (List<SpreadsheetNumberParsePatternsComponent> pattern : this.pattern.patterns) {
            final SpreadsheetNumberParsePatternsContext patternsContext = SpreadsheetNumberParsePatternsContext.with(pattern.iterator(), context);
            patternsContext.nextComponent(cursor);
            if (false == patternsContext.isRequired()) {
                final CharSequence text = save.textBetween();
                if (text.length() > 0) {
                    token = ParserTokens.bigDecimal(patternsContext.computeValue(), text.toString());
                    break;
                }
            }
            save.restore();
        }

        return Optional.ofNullable(token);
    }

    @Override
    public String toString() {
        return this.pattern.toString();
    }

    /**
     * The enclosing {@link SpreadsheetNumberParsePatterns}.
     */
    private final SpreadsheetNumberParsePatterns pattern;
}
