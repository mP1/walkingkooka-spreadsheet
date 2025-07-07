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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Parser} that wraps another returning {@link Optional#empty()} and resetting the {@link TextCursor} if only a {@link ColorSpreadsheetFormatParserToken} is matched.
 */
final class SpreadsheetFormatParsersFormatColorParser implements Parser<SpreadsheetFormatParserContext> {

    static SpreadsheetFormatParsersFormatColorParser with(final Parser<SpreadsheetFormatParserContext> parser) {
        return new SpreadsheetFormatParsersFormatColorParser(parser);
    }

    private SpreadsheetFormatParsersFormatColorParser(final Parser<SpreadsheetFormatParserContext> parser) {
        super();
        this.parser = parser;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetFormatParserContext context) {
        final TextCursorSavePoint save = cursor.save();
        Optional<ParserToken> maybeToken = parser.parse(
            cursor,
            context
        );
        if (maybeToken.isPresent()) {
            final List<ParserToken> tokens = maybeToken.get()
                .children();
            if (tokens.size() == 1) {
                if (tokens.get(0) instanceof ColorSpreadsheetFormatParserToken) {
                    maybeToken = Optional.empty();
                    save.restore();
                }
            }
        }

        return maybeToken;
    }

    @Override
    public int minCount() {
        return this.parser.minCount();
    }

    @Override
    public int maxCount() {
        return this.parser.maxCount();
    }

    private final Parser<SpreadsheetFormatParserContext> parser;

    @Override
    public String toString() {
        return this.parser.toString();
    }
}
