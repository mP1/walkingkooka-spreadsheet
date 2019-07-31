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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Objects;

/**
 * Holds a valid {@link SpreadsheetTextFormatterNumberPatterns}.
 */
public final class SpreadsheetTextFormatterNumberPatterns implements HashCodeEqualsDefined,
        HasJsonNode,
        Value<List<SpreadsheetFormatBigDecimalParserToken>> {
    /**
     * Creates a new {@link SpreadsheetTextFormatterNumberPatterns} after checking the value is valid.
     */
    public static SpreadsheetTextFormatterNumberPatterns parse(final String value) {
        Objects.requireNonNull(value, "value");

        try {
            return PARSER.parse(TextCursors.charSequence(value), SpreadsheetFormatParserContexts.basic())
                    .map(SpreadsheetTextFormatterNumberPatterns::transform)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pattern"));
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    /**
     * Transforms the tokens into a {@link SpreadsheetTextFormatterNumberPatterns}
     */
    private static SpreadsheetTextFormatterNumberPatterns transform(final ParserToken token) {
        return with(SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    /**
     * Parsers input that requires a single {@link SpreadsheetFormatBigDecimalParserToken} followed by an optional separator and big decimal tokens.
     */
    private static Parser<SpreadsheetFormatParserContext> parser() {
        final Parser<ParserContext> number = SpreadsheetFormatParsers.bigDecimal().cast();

        final Parser<ParserContext> optional = Parsers.sequenceParserBuilder()
                .required(SpreadsheetFormatParsers.expressionSeparator().cast())
                .required(number)
                .build()
                .repeating()
                .cast();

        return Parsers.sequenceParserBuilder()
                .required(number)
                .optional(optional.repeating())
                .build()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .cast();
    }

    private final static Parser<SpreadsheetFormatParserContext> PARSER = parser();

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterNumberPatterns} from the given tokens.
     */
    public static SpreadsheetTextFormatterNumberPatterns with(final List<SpreadsheetFormatBigDecimalParserToken> value) {
        Objects.requireNonNull(value, "value");

        final List<SpreadsheetFormatBigDecimalParserToken> copy = Lists.immutable(value);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Tokens is empty");
        }
        return new SpreadsheetTextFormatterNumberPatterns(copy);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTextFormatterNumberPatterns(final List<SpreadsheetFormatBigDecimalParserToken> value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public List<SpreadsheetFormatBigDecimalParserToken> value() {
        return this.value;
    }

    private final List<SpreadsheetFormatBigDecimalParserToken> value;

    // HashCodeEqualsDefined............................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetTextFormatterNumberPatterns &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetTextFormatterNumberPatterns other) {
        return this.value.equals(other.value);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ParserToken.text(this.value);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterNumberPatterns} from a {@link JsonNode}.
     */
    static SpreadsheetTextFormatterNumberPatterns fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        return SpreadsheetTextFormatterNumberPatterns.parse(node.stringValueOrFail());
    }

    /**
     * Creates a {@link walkingkooka.tree.json.JsonStringNode}.
     */
    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
    }

    static {
        HasJsonNode.register("spreadsheet-text-formatter-number-pattern",
                SpreadsheetTextFormatterNumberPatterns::fromJsonNode,
                SpreadsheetTextFormatterNumberPatterns.class);
    }
}
