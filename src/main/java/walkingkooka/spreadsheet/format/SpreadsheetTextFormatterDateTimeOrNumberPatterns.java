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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
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
import java.util.function.Function;

/**
 * Holds a a {@link List} of {@link walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken date/time} or {@link walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken} number tokens and some common functionality.
 */
abstract class SpreadsheetTextFormatterDateTimeOrNumberPatterns<T extends SpreadsheetFormatParserToken> implements HashCodeEqualsDefined,
        HasJsonNode,
        Value<List<T>> {

    /**
     * Parses text using the given parser and transformer.
     */
    static <P extends SpreadsheetTextFormatterDateTimeOrNumberPatterns> P parse0(final String text,
                                                                                 final Parser<SpreadsheetFormatParserContext> parser,
                                                                                 final Function<ParserToken, P> transformer) {
        Objects.requireNonNull(text, "value");

        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                    .map(transformer)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pattern " + CharSequences.quoteAndEscape(text)));
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    /**
     * Parsers input that requires a single {@link SpreadsheetFormatParserToken datetime or number} followed by an optional separator and more tokens.
     */
    static Parser<SpreadsheetFormatParserContext> parser(final Parser<ParserContext> dateTimeOrNumber) {
        final Parser<ParserContext> optional = Parsers.sequenceParserBuilder()
                .required(SpreadsheetFormatParsers.expressionSeparator().cast())
                .required(dateTimeOrNumber)
                .build()
                .repeating()
                .cast();

        return Parsers.sequenceParserBuilder()
                .required(dateTimeOrNumber)
                .optional(optional.repeating())
                .build()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .cast();
    }

    /**
     * Should be called from the factory to check the given tokens.
     */
    static <T extends SpreadsheetFormatParserToken> List<T> copyAndNotEmptyCheck(final List<T> value) {
        Objects.requireNonNull(value, "value");

        final List<T> copy = Lists.immutable(value);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Tokens is empty");
        }
        return copy;
    }

    /**
     * Package private ctor use factory
     */
    SpreadsheetTextFormatterDateTimeOrNumberPatterns(final List<T> value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public final List<T> value() {
        return this.value;
    }

    private final List<T> value;

    // HashCodeEqualsDefined............................................................................................

    @Override
    public final int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetTextFormatterDateTimeOrNumberPatterns other) {
        return this.value.equals(other.value);
    }

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return ParserToken.text(this.value);
    }

    // HasJsonNode......................................................................................................

    /**
     * Creates a {@link walkingkooka.tree.json.JsonStringNode}.
     */
    @Override
    public final JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
    }
}
