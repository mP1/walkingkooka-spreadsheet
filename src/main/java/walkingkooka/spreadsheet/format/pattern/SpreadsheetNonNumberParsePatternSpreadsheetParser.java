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

import walkingkooka.Cast;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link Parser} keeping the original {@link ParserToken}. The former provides the parsing and the token is used to provide the {@link SpreadsheetParserSelectorToken}.
 */
final class SpreadsheetNonNumberParsePatternSpreadsheetParser implements SpreadsheetParser {

    static SpreadsheetNonNumberParsePatternSpreadsheetParser with(final Parser<SpreadsheetParserContext> parser,
                                                                  final ParserToken token,
                                                                  final Optional<ValueTypeName> valueType) {
        return new SpreadsheetNonNumberParsePatternSpreadsheetParser(
            parser,
            token,
            valueType
        );
    }

    private SpreadsheetNonNumberParsePatternSpreadsheetParser(final Parser<SpreadsheetParserContext> parser,
                                                              final ParserToken token,
                                                              final Optional<ValueTypeName> valueType) {
        this.parser = parser;
        this.token = token;
        this.valueType = valueType;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetParserContext context) {
        return this.parser.parse(
            cursor,
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

    private final Parser<SpreadsheetParserContext> parser;

    @Override
    public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetParserSelectorToken.tokens(this.token);
    }

    private final ParserToken token;

    @Override
    public Optional<ValueTypeName> valueType() {
        return this.valueType;
    }

    private final Optional<ValueTypeName> valueType;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.parser,
            this.token
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetNonNumberParsePatternSpreadsheetParser &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetNonNumberParsePatternSpreadsheetParser other) {
        return this.parser.equals(other.parser) &&
            this.token.equals(other.token);
    }

    @Override
    public String toString() {
        return this.parser.toString();
    }
}
