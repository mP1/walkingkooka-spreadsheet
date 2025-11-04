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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wrapos another {@link SpreadsheetParser} and uses the provided {@link #toString()}.
 */
final class ToStringSpreadsheetParser implements SpreadsheetParser {

    static SpreadsheetParser with(final SpreadsheetParser parser,
                                  final String toString) {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(toString, "toString");

        SpreadsheetParser parserWithToString;

        SpreadsheetParser temp = parser;

        if (temp.toString().equals(toString)) {
            parserWithToString = parser; // no need to wrap
        } else {
            if (temp instanceof ToStringSpreadsheetParser) {
                temp = ((ToStringSpreadsheetParser) temp).parser;
            }
            if (temp.toString().equals(toString)) {
                parserWithToString = parser; // no need to wrap
            } else {
                parserWithToString = new ToStringSpreadsheetParser(
                    parser,
                    toString
                );
            }
        }

        return parserWithToString;
    }

    private ToStringSpreadsheetParser(final SpreadsheetParser parser,
                                      final String toString) {
        super();

        this.parser = parser;
        this.toString = toString;
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
        return this.parser.tokens(context);
    }

    @Override
    public Optional<ValueTypeName> valueType() {
        return this.parser.valueType();
    }

    private final SpreadsheetParser parser;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.parser,
            this.toString
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof ToStringSpreadsheetParser &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final ToStringSpreadsheetParser other) {
        return this.parser.equals(other.parser) &&
            this.toString.equals(other.toString);
    }

    @Override
    public String toString() {
        return this.toString;
    }

    private final String toString;
}
