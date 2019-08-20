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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Objects;

/**
 * Holds a a {@link List} of {@link SpreadsheetFormatDateTimeParserToken date/time} or {@link SpreadsheetFormatNumberParserToken} number tokens and some common functionality.
 */
public abstract class SpreadsheetParsePatterns<T extends SpreadsheetFormatParserToken> extends SpreadsheetPatterns<List<T>>
    implements HasConverter,
        HasParser<ParserContext> {

    // with.............................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from the given tokens.
     */
    public static SpreadsheetDateParsePatterns withDate(final List<SpreadsheetFormatDateParserToken> value) {
        return SpreadsheetDateParsePatterns.withTokens(value);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePatterns} from the given tokens.
     */
    public static SpreadsheetDateTimeParsePatterns withDateTime(final List<SpreadsheetFormatDateTimeParserToken> value) {
        return SpreadsheetDateTimeParsePatterns.withTokens(value);
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from the given tokens.
     */
    public static SpreadsheetNumberParsePatterns withNumber(final List<SpreadsheetFormatNumberParserToken> value) {
        return SpreadsheetNumberParsePatterns.withTokens(value);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    public static SpreadsheetTimeParsePatterns withTime(final List<SpreadsheetFormatTimeParserToken> value) {
        return SpreadsheetTimeParsePatterns.withTokens(value);
    }

    // parseDate........................................................................................................

    /**
     * Creates a new {@link SpreadsheetDateParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetDateParsePatterns parseDate(final String text) {
        return parsePattern(text,
                DATE_PARSER,
                SpreadsheetParsePatterns::transformDate);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSER = patternParserOrFail(SpreadsheetFormatParsers.date().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetDateParsePatterns}
     */
    private static SpreadsheetDateParsePatterns transformDate(final ParserToken token) {
        return SpreadsheetDateParsePatterns.withToken(token);
    }

    // parseDateTime....................................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetDateTimeParsePatterns parseDateTime(final String text) {
        return parsePattern(text,
                DATETIME_PARSER,
                SpreadsheetParsePatterns::transformDateTime);
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSER = patternParserOrFail(SpreadsheetFormatParsers.dateTime().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetDateTimeParsePatterns}
     */
    private static SpreadsheetDateTimeParsePatterns transformDateTime(final ParserToken token) {
        return SpreadsheetDateTimeParsePatterns.withToken(token);
    }

    // parseNumber.......................................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetNumberParsePatterns parseNumber(final String text) {
        return parsePattern(text,
                NUMBER_PARSER,
                SpreadsheetParsePatterns::transformNumber);
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSER = patternParserOrFail(SpreadsheetFormatParsers.number().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetNumberParsePatterns}
     */
    private static SpreadsheetNumberParsePatterns transformNumber(final ParserToken token) {
        return SpreadsheetNumberParsePatterns.withToken(token);
    }

    // parseTime....................................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeParsePatterns} after checking the value is valid.
     */
    public static SpreadsheetTimeParsePatterns parseTime(final String text) {
        return parsePattern(text,
                TIME_PARSER,
                SpreadsheetParsePatterns::transformTime);
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSER = patternParserOrFail(SpreadsheetFormatParsers.time().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetTimeParsePatterns}
     */
    private static SpreadsheetTimeParsePatterns transformTime(final ParserToken token) {
        return SpreadsheetTimeParsePatterns.withToken(token);
    }
    
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

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePatterns(final List<T> tokens) {
        super(Lists.immutable(tokens));
    }

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return ParserToken.text(this.value);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDateParsePatterns fromJsonNodeDate(final JsonNode node) {
        checkNode(node);

        return parseDate(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetDateTimeParsePatterns fromJsonNodeDateTime(final JsonNode node) {
        checkNode(node);

        return parseDateTime(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetNumberParsePatterns fromJsonNodeNumber(final JsonNode node) {
        checkNode(node);

        return parseNumber(node.stringValueOrFail());
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetTimeParsePatterns fromJsonNodeTime(final JsonNode node) {
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
        HasJsonNode.register("spreadsheet-text-formatter-date-pattern",
                SpreadsheetParsePatterns::fromJsonNodeDate,
                SpreadsheetDateParsePatterns.class);

        HasJsonNode.register("spreadsheet-text-formatter-datetime-pattern",
                SpreadsheetParsePatterns::fromJsonNodeDateTime,
                SpreadsheetDateTimeParsePatterns.class);

        HasJsonNode.register("spreadsheet-text-formatter-number-pattern",
                SpreadsheetParsePatterns::fromJsonNodeNumber,
                SpreadsheetNumberParsePatterns.class);

        HasJsonNode.register("spreadsheet-text-formatter-time-pattern",
                SpreadsheetParsePatterns::fromJsonNodeTime,
                SpreadsheetTimeParsePatterns.class);
    }

    // HasConverter........................................................................................................

    /**
     * Returns a {@link Converter} which will try all the patterns.
     */
    public final Converter converter() {
        if (null == this.converter) {
            this.converter = this.createConverter();
        }
        return this.converter;
    }

    private Converter converter;

    /**
     * Factory that lazily creates a {@link Converter}
     */
    abstract Converter createConverter();
    
    // HasParser........................................................................................................

    /**
     * Returns a {@link Parser} which will try all the patterns.
     */
    public final Parser<ParserContext> parser() {
        if (null == this.parser) {
            this.parser = this.createParser();
        }
        return this.parser;
    }

    private Parser<ParserContext> parser;

    /**
     * Factory that lazily creates a {@link Parser}
     */
    abstract Parser<ParserContext> createParser();
}
