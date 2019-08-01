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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
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
 * Holds a a {@link List} of {@link SpreadsheetFormatDateTimeParserToken date/time} or {@link SpreadsheetFormatNumberParserToken} number tokens and some common functionality.
 */
public abstract class SpreadsheetPatterns<T extends SpreadsheetFormatParserToken> implements HashCodeEqualsDefined,
        HasJsonNode,
        Value<List<T>> {

    // with.............................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDatePatterns} from the given tokens.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatDateParserToken> withDate(final List<SpreadsheetFormatDateParserToken> value) {
        return SpreadsheetDatePatterns.withDate0(value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimePatterns} from the given tokens.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatDateTimeParserToken> withDateTime(final List<SpreadsheetFormatDateTimeParserToken> value) {
        return SpreadsheetDateTimePatterns.withDateTime0(value);
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberPatterns} from the given tokens.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatNumberParserToken> withNumber(final List<SpreadsheetFormatNumberParserToken> value) {
        return SpreadsheetNumberPatterns.withNumber0(value);
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

    // parseDate....................................................................................................

    /**
     * Creates a new {@link SpreadsheetDatePatterns} after checking the value is valid.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatDateParserToken> parseDate(final String text) {
        return parseDate0(text);
    }

    static SpreadsheetDatePatterns parseDate0(final String text) {
        return parse0(text,
                DATE_PARSER,
                SpreadsheetPatterns::transformDate);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSER = parser(SpreadsheetFormatParsers.date().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetDatePatterns}
     */
    private static SpreadsheetDatePatterns transformDate(final ParserToken token) {
        return SpreadsheetDatePatterns.withDate0(SpreadsheetDatePatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    // parseDateTime....................................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimePatterns} after checking the value is valid.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatDateTimeParserToken> parseDateTime(final String text) {
        return parseDateTime0(text);
    }

    static SpreadsheetDateTimePatterns parseDateTime0(final String text) {
        return parse0(text,
                DATETIME_PARSER,
                SpreadsheetPatterns::transformDateTime);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSER = parser(SpreadsheetFormatParsers.dateTime().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetDateTimePatterns}
     */
    private static SpreadsheetDateTimePatterns transformDateTime(final ParserToken token) {
        return SpreadsheetDateTimePatterns.withDateTime0(SpreadsheetDateTimePatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    // parseNumber.......................................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberPatterns} after checking the value is valid.
     */
    public static SpreadsheetPatterns<SpreadsheetFormatNumberParserToken> parseNumber(final String text) {
        return parseNumber0(text);
    }

    static SpreadsheetNumberPatterns parseNumber0(final String text) {
        return parse0(text,
                NUMBER_PARSER,
                SpreadsheetPatterns::transformNumber);
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSER = parser(SpreadsheetFormatParsers.number().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetNumberPatterns}
     */
    private static SpreadsheetNumberPatterns transformNumber(final ParserToken token) {
        return SpreadsheetNumberPatterns.withNumber0(SpreadsheetNumberPatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    // helper...........................................................................................................

    /**
     * Parses text using the given parser and transformer.
     */
    static <P extends SpreadsheetPatterns> P parse0(final String text,
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
     * Parsers input that requires a single {@link SpreadsheetFormatParserToken token} followed by an optional separator and more tokens.
     */
    static Parser<SpreadsheetFormatParserContext> parser(final Parser<ParserContext> parser) {
        final Parser<ParserContext> optional = Parsers.sequenceParserBuilder()
                .required(SpreadsheetFormatParsers.expressionSeparator().cast())
                .required(parser)
                .build()
                .repeating()
                .cast();

        return Parsers.sequenceParserBuilder()
                .required(parser)
                .optional(optional.repeating())
                .build()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .cast();
    }

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetPatterns(final List<T> value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public final List<T> value() {
        return this.value;
    }

    private final List<T> value;

    // isXXX............................................................................................................

    public abstract boolean isDate();

    public abstract boolean isDateTime();

    public abstract boolean isNumber();

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

    private boolean equals0(final SpreadsheetPatterns other) {
        return this.value.equals(other.value);
    }

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return ParserToken.text(this.value);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDatePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDatePatterns fromJsonNodeDate(final JsonNode node) {
        checkNode(node);

        return parseDate0(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDateTimePatterns fromJsonNodeDateTime(final JsonNode node) {
        checkNode(node);

        return parseDateTime0(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberPatterns} from a {@link JsonNode}.
     */
    static SpreadsheetNumberPatterns fromJsonNodeNumber(final JsonNode node) {
        checkNode(node);

        return parseNumber0(node.stringValueOrFail());
    }

    private static void checkNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");
    }

    /**
     * Creates a {@link walkingkooka.tree.json.JsonStringNode}.
     */
    @Override
    public final JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
    }

    static {
        HasJsonNode.register("spreadsheet-text-formatter-date-pattern",
                SpreadsheetPatterns::fromJsonNodeDate,
                SpreadsheetDatePatterns.class);

        HasJsonNode.register("spreadsheet-text-formatter-datetime-pattern",
                SpreadsheetPatterns::fromJsonNodeDateTime,
                SpreadsheetDateTimePatterns.class);

        HasJsonNode.register("spreadsheet-text-formatter-number-pattern",
                SpreadsheetPatterns::fromJsonNodeNumber,
                SpreadsheetNumberPatterns.class);
    }
}
