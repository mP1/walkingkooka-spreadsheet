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

package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParser} that wraps a non {@link Parser} that is not a {@link SpreadsheetParser}.
 * It adds no additional functionality and delegates all methods to the wrapped parser.
 */
final class ParserSpreadsheetParser implements SpreadsheetParser {

    public static SpreadsheetParser with(final Parser<SpreadsheetParserContext> parser) {
        return parser instanceof SpreadsheetParser ?
                (SpreadsheetParser) parser :
                new ParserSpreadsheetParser(
                        Objects.requireNonNull(parser, "parser")
                );
    }

    private ParserSpreadsheetParser(final Parser<SpreadsheetParserContext> parser) {
        this.parser = parser;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor text,
                                       final SpreadsheetParserContext context) {
        return this.parser.parse(
                text,
                context
        );
    }

    private final Parser<SpreadsheetParserContext> parser;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.parser.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof ParserSpreadsheetParser &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final ParserSpreadsheetParser other) {
        return this.parser.equals(other.parser);
    }

    @Override
    public String toString() {
        return this.parser.toString();
    }
}
