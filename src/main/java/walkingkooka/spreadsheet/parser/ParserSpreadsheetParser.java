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
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParser} that wraps a non {@link Parser} that is not a {@link SpreadsheetParser}.
 * It adds no additional functionality and delegates all methods to the wrapped parser.
 */
final class ParserSpreadsheetParser implements SpreadsheetParser {

    public static SpreadsheetParser with(final Parser<SpreadsheetParserContext> parser,
                                         final Optional<ValidationValueTypeName> valueType) {
        return parser instanceof SpreadsheetParser ?
            (SpreadsheetParser) parser :
            new ParserSpreadsheetParser(
                Objects.requireNonNull(parser, "parser"),
                Objects.requireNonNull(valueType, "valueType")
            );
    }

    private ParserSpreadsheetParser(final Parser<SpreadsheetParserContext> parser,
                                    final Optional<ValidationValueTypeName> valueType) {
        this.parser = parser;
        this.valueType = valueType;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor text,
                                       final SpreadsheetParserContext context) {
        return this.parser.parse(
            text,
            context
        );
    }

    @Override
    public int minCount() {
        return this.parser.minCount();
    }

    @Override
    public int maxCount() {
        return this.parser.maxCount();
    }

    @Override
    public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
        Objects.requireNonNull(context, "context");

        throw new UnsupportedOperationException();
    }

    private final Parser<SpreadsheetParserContext> parser;

    @Override
    public Optional<ValidationValueTypeName> valueType() {
        return this.valueType;
    }

    private final Optional<ValidationValueTypeName> valueType;

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
