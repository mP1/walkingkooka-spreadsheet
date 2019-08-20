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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class {@link Parser} that tries each and every try until success.
 */
abstract class SpreadsheetParsePatternsParser<T extends SpreadsheetFormatParserToken> implements Parser<ParserContext> {

    /**
     * Package private to limit sub class.
     */
    SpreadsheetParsePatternsParser(final SpreadsheetParsePatterns<T> patterns) {
        super();
        this.patterns = patterns;
    }

    @Override
    public final Optional<ParserToken> parse(final TextCursor cursor,
                                             final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return this.parse0(cursor, context);
    }

    abstract Optional<ParserToken> parse0(final TextCursor cursor,
                                          final ParserContext context);

    @Override
    public final String toString() {
        return this.patterns.toString();
    }

    final SpreadsheetParsePatterns<T> patterns;
}
