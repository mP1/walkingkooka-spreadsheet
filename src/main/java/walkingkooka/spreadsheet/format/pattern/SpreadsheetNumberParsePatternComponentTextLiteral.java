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

import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;

import java.util.Optional;

/**
 * Text literals within a parse number pattern are not required and ignored
 */
final class SpreadsheetNumberParsePatternComponentTextLiteral extends SpreadsheetNumberParsePatternComponentNonDigit {

    static SpreadsheetNumberParsePatternComponentTextLiteral with(final String text) {
        return new SpreadsheetNumberParsePatternComponentTextLiteral(text);
    }

    private SpreadsheetNumberParsePatternComponentTextLiteral(final String text) {
        super();
        this.text = text;
        this.parser = Parsers.string(
                text,
                CaseSensitivity.SENSITIVE
        );
    }

    @Override
    boolean isExpressionCompatible() {
        return false;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        boolean completed = false;
        final Optional<ParserToken> maybeTextLiteral = this.parser.parse(
                cursor,
                PARSER_CONTEXT
        );
        if (maybeTextLiteral.isPresent()) {
            final StringParserToken textLiteral = maybeTextLiteral.get()
                    .cast(StringParserToken.class);
            request.add(
                    SpreadsheetParserToken.textLiteral(
                            textLiteral.text(),
                            textLiteral.value()
                    )
            );
            completed = request.nextComponent(cursor);
        }

        return completed;
    }

    private final Parser<ParserContext> parser;

    @Override
    public String toString() {
        return this.text;
    }

    private final String text;
}
