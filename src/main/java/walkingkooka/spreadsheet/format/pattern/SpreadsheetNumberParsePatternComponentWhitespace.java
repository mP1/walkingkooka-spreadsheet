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

import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Optional;

/**
 * A {@link SpreadsheetNumberParsePatternComponent} that matches a given number of whitespace characters.
 */
final class SpreadsheetNumberParsePatternComponentWhitespace extends SpreadsheetNumberParsePatternComponentNonDigit {

    /**
     * Singleton
     */
    static SpreadsheetNumberParsePatternComponentWhitespace with(final int length) {
        return new SpreadsheetNumberParsePatternComponentWhitespace(length);
    }

    private SpreadsheetNumberParsePatternComponentWhitespace(final int length) {
        super();
        this.length = length;

        this.parser = Parsers.charPredicateString(
            CharPredicates.whitespace(),
            length,
            length
        );
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        boolean completed = false;
        final Optional<ParserToken> maybeWhitespace = this.parser.parse(
            cursor,
            PARSER_CONTEXT
        );
        if (maybeWhitespace.isPresent()) {
            final String whitespace = maybeWhitespace.get()
                .text();
            request.add(
                SpreadsheetFormulaParserToken.whitespace(
                    whitespace,
                    whitespace
                )
            );
            completed = request.nextComponent(cursor);
        }

        return completed;
    }


    private final Parser<ParserContext> parser;

    @Override
    public String toString() {
        return CharSequences.repeating(
            ' ',
            this.length
        ).toString();
    }

    private final int length;
}
