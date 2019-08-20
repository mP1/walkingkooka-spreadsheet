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
import walkingkooka.Value;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.HasParser;
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
 * Holds a tokens that may be used to parse or format values along with helpers.
 */
abstract public class SpreadsheetPattern<V> implements HashCodeEqualsDefined,
        HasConverter,
        HasJsonNode,
        HasParser<ParserContext>,
        Value<V> {

    /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from the given tokens.
     */
    public static SpreadsheetDateParsePatterns withDateParse(final List<SpreadsheetFormatDateParserToken> token) {
        return SpreadsheetDateParsePatterns.withTokens(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePatterns} from the given tokens.
     */
    public static SpreadsheetDateTimeParsePatterns withDateTimeParse(final List<SpreadsheetFormatDateTimeParserToken> token) {
        return SpreadsheetDateTimeParsePatterns.withTokens(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from the given tokens.
     */
    public static SpreadsheetNumberParsePatterns withNumberParse(final List<SpreadsheetFormatNumberParserToken> token) {
        return SpreadsheetNumberParsePatterns.withTokens(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    public static SpreadsheetTimeParsePatterns withTimeParse(final List<SpreadsheetFormatTimeParserToken> token) {
        return SpreadsheetTimeParsePatterns.withTokens(token);
    }

    // parseDate........................................................................................................

    /**
     * Creates a new {@link SpreadsheetDateParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetDateParsePatterns parseDate(final String text) {
        return parsePattern(text,
                DATE_PARSER,
                SpreadsheetDateParsePatterns::withToken);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSER = patternParserOrFail(SpreadsheetFormatParsers.date().cast());

    // parseDateTime....................................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetDateTimeParsePatterns parseDateTime(final String text) {
        return parsePattern(text,
                DATETIME_PARSER,
                SpreadsheetDateTimeParsePatterns::withToken);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSER = patternParserOrFail(SpreadsheetFormatParsers.dateTime().cast());

    // parseNumber.......................................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetNumberParsePatterns parseNumber(final String text) {
        return parsePattern(text,
                NUMBER_PARSER,
                SpreadsheetNumberParsePatterns::withToken);
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSER = patternParserOrFail(SpreadsheetFormatParsers.number().cast());

    // parseTime....................................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetTimeParsePatterns parseTime(final String text) {
        return parsePattern(text,
                TIME_PARSER,
                SpreadsheetTimeParsePatterns::withToken);
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSER = patternParserOrFail(SpreadsheetFormatParsers.time().cast());

    // helper...........................................................................................................

    /**
     * Parsers input that requires a single {@link SpreadsheetFormatParserToken token} followed by an optional separator and more tokens.
     */
    static Parser<SpreadsheetFormatParserContext> patternParser(final Parser<ParserContext> parser) {
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
                .cast();
    }

    /**
     * Parsers input that requires a single {@link SpreadsheetFormatParserToken token} followed by an optional separator and more tokens.
     */
    static Parser<SpreadsheetFormatParserContext> patternParserOrFail(final Parser<ParserContext> parser) {
        return patternParser(parser).orFailIfCursorNotEmpty(ParserReporters.basic());
    }

    // helpers..........................................................................................................

    static void check(final List<? extends SpreadsheetFormatParserToken> tokens) {
        Objects.requireNonNull(tokens, "tokens");
    }

    /**
     * Parses text using the given parser and transformer.
     */
    static <P extends SpreadsheetPattern> P parsePattern(final String text,
                                                         final Parser<SpreadsheetFormatParserContext> parser,
                                                         final Function<ParserToken, P> transformer) {
        Objects.requireNonNull(text, "text");

        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                    .map(transformer)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pattern " + CharSequences.quoteAndEscape(text)));
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetPattern(final V value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public final V value() {
        return this.value;
    }

    final V value;

    // isXXX............................................................................................................

    /**
     * Returns true if holding date pattern(s)
     */
    public abstract boolean isDate();

    /**
     * Returns true if holding date/time pattern(s)
     */
    public abstract boolean isDateTime();

    /**
     * Returns true if holding number pattern(s)
     */
    public abstract boolean isNumber();

    /**
     * Returns true if holding time pattern(s)
     */
    public abstract boolean isTime();

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

    private boolean equals0(final SpreadsheetPattern other) {
        return this.value.equals(other.value);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDateParsePatterns fromJsonNodeDateParsePatterns(final JsonNode node) {
        checkNode(node);

        return parseDate(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDateTimeParsePatterns fromJsonNodeDateTimeParsePatterns(final JsonNode node) {
        checkNode(node);

        return parseDateTime(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetNumberParsePatterns fromJsonNodeNumberParsePatterns(final JsonNode node) {
        checkNode(node);

        return parseNumber(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetTimeParsePatterns fromJsonNodeTimeParsePatterns(final JsonNode node) {
        checkNode(node);

        return parseTime(node.stringValueOrFail());
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
        HasJsonNode.register("spreadsheet-date-parse-pattern",
                SpreadsheetPattern::fromJsonNodeDateParsePatterns,
                SpreadsheetDateParsePatterns.class);

        HasJsonNode.register("spreadsheet-datetime-parse-pattern",
                SpreadsheetPattern::fromJsonNodeDateTimeParsePatterns,
                SpreadsheetDateTimeParsePatterns.class);

        HasJsonNode.register("spreadsheet-number-parse-pattern",
                SpreadsheetPattern::fromJsonNodeNumberParsePatterns,
                SpreadsheetNumberParsePatterns.class);

        HasJsonNode.register("spreadsheet-time-parse-pattern",
                SpreadsheetPattern::fromJsonNodeTimeParsePatterns,
                SpreadsheetTimeParsePatterns.class);
    }
}
