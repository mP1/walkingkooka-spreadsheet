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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Holds a tokens that may be used to parse or format values along with helpers.
 */
abstract public class SpreadsheetPattern implements Value<ParserToken>, TreePrintable {

    /**
     * The separator character between multiple patterns.
     */
    public static final CharacterConstant SEPARATOR = CharacterConstant.with(';');

    /**
     * Factory that creates a {@link ParserToken} from the given token.
     */
    public static SpreadsheetDateFormatPattern dateFormatPattern(final ParserToken token) {
        return SpreadsheetDateFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateParsePattern} from the given tokens.
     */
    public static SpreadsheetDateParsePattern dateParse(final ParserToken token) {
        return SpreadsheetDateParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} from the given token.
     */
    public static SpreadsheetDateTimeFormatPattern dateTimeFormatPattern(final ParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    public static SpreadsheetDateTimeParsePattern dateTimeParsePatterns(final ParserToken token) {
        return SpreadsheetDateTimeParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} from the given token.
     */
    public static SpreadsheetNumberFormatPattern numberFormatPattern(final ParserToken token) {
        return SpreadsheetNumberFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    public static SpreadsheetNumberParsePattern numberParsePatterns(final ParserToken token) {
        return SpreadsheetNumberParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeFormatPattern} from the given token.
     */
    public static SpreadsheetTimeFormatPattern timeFormatPattern(final ParserToken token) {
        return SpreadsheetTimeFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    public static SpreadsheetTimeParsePattern timeParsePatterns(final ParserToken token) {
        return SpreadsheetTimeParsePattern.with(token);
    }

    // Locale public factory............................................................................................

    /**
     * Creates a {@link SpreadsheetDateFormatPattern} using date patterns from the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetDateFormatPattern dateFormatPatternLocale(final Locale locale) {
        return javaTextDateFormat(
                DateFormat.getDateInstance(DateFormat.FULL, locale),
                NOT_DATE,
                NOT_TIME,
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    /**
     * Creates a {@link SpreadsheetDateFormatPattern} using date patterns from the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetDateParsePattern dateParsePatternsLocale(final Locale locale) {
        return javaTextDateFormat(
                Lists.of(
                        DateFormat.getDateInstance(DateFormat.FULL, locale),
                        DateFormat.getDateInstance(DateFormat.LONG, locale),
                        DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
                        DateFormat.getDateInstance(DateFormat.SHORT, locale)
                ),
                DATE,
                NOT_TIME,
                SpreadsheetPattern::parseDateParsePatterns
        );
    }

    /**
     * Creates a {@link SpreadsheetDateFormatPattern} using date/time patterns from the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetDateTimeFormatPattern dateTimeFormatPatternLocale(final Locale locale) {
        checkLocale(locale);

        return javaTextDateFormat(
                DateFormat.getDateTimeInstance(
                        DateFormat.FULL,
                        DateFormat.FULL,
                        locale
                ),
                NOT_DATE,
                NOT_TIME,
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    public static SpreadsheetDateTimeParsePattern dateTimeParsePatternsLocale(final Locale locale) {
        checkLocale(locale);

        final List<DateFormat> patterns = Lists.array();

        for (final int dateStyle : DATE_FORMAT_STYLES) {
            for (final int timeStyle : DATE_FORMAT_STYLES) {
                patterns.add(
                        DateFormat.getDateTimeInstance(
                                dateStyle,
                                timeStyle,
                                locale
                        )
                );
            }
        }

        return javaTextDateFormat(
                patterns,
                DATE,
                TIME,
                SpreadsheetPattern::parseDateTimeParsePatterns
        );
    }


    /**
     * Creates a {@link SpreadsheetTimeFormatPattern} using time patterns from the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetTimeFormatPattern timeFormatPatternLocale(final Locale locale) {
        checkLocale(locale);

        return javaTextDateFormat(
                DateFormat.getTimeInstance(DateFormat.FULL, locale),
                NOT_DATE,
                NOT_TIME,
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    /**
     * Creates a {@link SpreadsheetTimeFormatPattern} using time patterns from the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetTimeParsePattern timeParsePatternsLocale(final Locale locale) {
        checkLocale(locale);

        return javaTextDateFormat(
                Lists.of(
                        DateFormat.getTimeInstance(DateFormat.FULL, locale),
                        DateFormat.getTimeInstance(DateFormat.LONG, locale),
                        DateFormat.getTimeInstance(DateFormat.MEDIUM, locale),
                        DateFormat.getTimeInstance(DateFormat.SHORT, locale)
                ),
                NOT_DATE,
                TIME,
                SpreadsheetPattern::parseTimeParsePatterns
        );
    }

    private static void checkLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
    }

    private final static boolean DATE = true;
    private final static boolean NOT_DATE = !DATE;

    private final static boolean TIME = true;
    private final static boolean NOT_TIME = !TIME;

    /**
     * Factory that fetches a {@link DateFormat}, casts to a {@link SimpleDateFormat} and parses the pattern.
     */
    private static <P extends SpreadsheetPattern, V> P javaTextDateFormat(final DateFormat dateFormat,
                                                                          final boolean date,
                                                                          final boolean time,
                                                                          final Function<String, P> patternParser) {
        return javaTextDateFormat(
                Lists.of(dateFormat),
                date,
                time,
                patternParser
        );
    }

    /**
     * Uses the provided {@link DateFormat} actually {@link SimpleDateFormat} visiting the pattern of each
     * to create {@link SpreadsheetPattern} sub class instance. For sub classes of {@link SpreadsheetParsePattern}
     * simplified forms of each pattern are also created, this means if a locale supports a pattern like <code>hh:mm:ss</code>
     * the form <code>hh:mm</code> will also be added.
     */
    private static <P extends SpreadsheetPattern, V> P javaTextDateFormat(final Iterable<DateFormat> dateFormats,
                                                                          final boolean date,
                                                                          final boolean time,
                                                                          final Function<String, P> patternParser) {
        final Set<String> patterns = Sets.ordered();

        for (final DateFormat dateFormat : dateFormats) {
            final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
            final String simpleDateFormatPattern = simpleDateFormat.toPattern();

            // include all year, seconds, ampm
            visitSimpleDateFormatPattern(
                    simpleDateFormatPattern,
                    SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                    SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                    true,
                    patterns
            );

            if (date) {
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_2_DIGITS,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                        true,
                        patterns
                );


                if (time) {
                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_2_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                            false,
                            patterns
                    );

                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_2_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE,
                            true,
                            patterns
                    );
                }

                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_4_DIGITS,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                        true,
                        patterns
                );

                if (time) {
                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_4_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE_WITH_MILLIS,
                            true,
                            patterns
                    );

                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_4_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE_WITH_MILLIS,
                            false,
                            patterns
                    );

                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_4_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                            false,
                            patterns
                    );

                    visitSimpleDateFormatPattern(
                            simpleDateFormatPattern,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorYear.ALWAYS_4_DIGITS,
                            SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE,
                            true,
                            patterns
                    );
                }
            }


            // if only date, try and make a pattern without year
            if (date && !time) {
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.EXCLUDE,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE, // only dates, seconds and ampm shouldnt appear anyway
                        false,
                        patterns
                );
            }

            // if a parse pattern want to create simplifications like hh:mm:ss -> hh:mm
            if (time) {
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE,
                        false,
                        patterns
                );
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE_WITH_MILLIS,
                        false,
                        patterns
                );
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE,
                        true,
                        patterns
                );
                visitSimpleDateFormatPattern(
                        simpleDateFormatPattern,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                        SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE,
                        false,
                        patterns
                );
            }
        }

        return patternParser.apply(
                String.join(
                        SEPARATOR.string(),
                        patterns
                )
        );
    }

    private final static int[] DATE_FORMAT_STYLES = new int[]{
            DateFormat.FULL,
            DateFormat.LONG,
            DateFormat.MEDIUM,
            DateFormat.SHORT
    };

    /**
     * Accepts a {@link java.text.SimpleDateFormat} pattern and returns its equivalent spreadsheet format pattern.
     */
    private static void visitSimpleDateFormatPattern(final String simpleDateFormatPattern,
                                                     final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year,
                                                     final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds,
                                                     final boolean ampm,
                                                     final Set<String> patterns) {
        final String pattern = SpreadsheetPatternSimpleDateFormatPatternVisitor.pattern(
                simpleDateFormatPattern,
                year,
                seconds,
                ampm
        );
        patterns.add(pattern);
    }

    /**
     * Creates a {@link SpreadsheetNumberFormatPattern} using the {@link Locale}
     */
    public static SpreadsheetNumberFormatPattern numberFormatPatternLocale(final Locale locale) {
        checkLocale(locale);

        return parseNumberFormatPattern(
                decimalFormatPattern(DecimalFormat.getInstance(locale))
        );
    }

    /**
     * Creates a {@link SpreadsheetNumberParsePattern} using the {@link Locale}
     */
    public static SpreadsheetNumberParsePattern numberParsePatternsLocale(final Locale locale) {
        checkLocale(locale);

        final String number = decimalFormatPattern(DecimalFormat.getInstance(locale));
        final String integer = decimalFormatPattern(DecimalFormat.getIntegerInstance(locale));

        return parseNumberParsePatterns(
                number.equals(integer) ?
                        number :
                        number + ";" + integer);
    }


    /**
     * This makes an assumption that a {@link DecimalFormat} pattern will only use characters that are also equal in
     * functionality and meaning within a spreadsheet number format.
     */
    static String decimalFormatPattern(final NumberFormat numberFormat) {
        final DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.toPattern();
    }

    // parseDateFormatPatterns...........................................................................................

    /**
     * Creates a new {@link SpreadsheetDateFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetDateFormatPattern parseDateFormatPattern(final String text) {
        return parsePattern(
                text,
                DATE_FORMAT_PARSER,
                SpreadsheetPattern::transformDate
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_FORMAT_PARSER = SpreadsheetFormatParsers.dateFormat();

    /**
     * Transforms the tokens into a {@link SpreadsheetDateFormatPattern}
     */
    private static SpreadsheetDateFormatPattern transformDate(final ParserToken token) {
        return SpreadsheetDateFormatPattern.with(
                token.cast(SpreadsheetFormatDateParserToken.class)
        );
    }

    // parseDateParsePatterns...........................................................................................

    /**
     * Creates a new {@link SpreadsheetDateParsePattern} after checking the value is valid.
     */
    public static SpreadsheetDateParsePattern parseDateParsePatterns(final String text) {
        return parsePattern(
                text,
                DATE_PARSE_PARSER,
                SpreadsheetDateParsePattern::with
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSE_PARSER = SpreadsheetFormatParsers.dateParse();

    // parseDateTimeFormatPatterns.......................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetDateTimeFormatPattern parseDateTimeFormatPattern(final String text) {
        return parsePattern(
                text,
                DATETIME_FORMAT_PARSER,
                SpreadsheetPattern::transformDateTime
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_FORMAT_PARSER = SpreadsheetFormatParsers.dateTimeFormat();

    /**
     * Transforms the tokens into a {@link SpreadsheetDateTimeFormatPattern}
     */
    private static SpreadsheetDateTimeFormatPattern transformDateTime(final ParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(
                token.cast(SpreadsheetFormatDateTimeParserToken.class)
        );
    }

    // parseDateTimeParsePatterns.......................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeParsePattern} after checking the value is valid.
     */
    public static SpreadsheetDateTimeParsePattern parseDateTimeParsePatterns(final String text) {
        return parsePattern(
                text,
                DATETIME_PARSE_PARSER,
                SpreadsheetDateTimeParsePattern::with
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSE_PARSER = SpreadsheetFormatParsers.dateTimeParse();

    // parseNumberFormatPatterns.........................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetNumberFormatPattern parseNumberFormatPattern(final String text) {
        return parsePattern(text,
                NUMBER_FORMAT_PARSER,
                SpreadsheetPattern::transformNumber);
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_FORMAT_PARSER = SpreadsheetFormatParsers.numberFormat();

    /**
     * Transforms the tokens into a {@link SpreadsheetNumberFormatPattern}
     */
    private static SpreadsheetNumberFormatPattern transformNumber(final ParserToken token) {
        return SpreadsheetNumberFormatPattern.with(token.cast(SpreadsheetFormatNumberParserToken.class));
    }

    // parseNumberParsePatterns.........................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberParsePattern} after checking the value is valid.
     */
    public static SpreadsheetNumberParsePattern parseNumberParsePatterns(final String text) {
        return parsePattern(
                text,
                NUMBER_PARSE_PARSER,
                SpreadsheetNumberParsePattern::with
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSE_PARSER = SpreadsheetFormatParsers.numberParse();

    // parseTextFormatPatterns..........................................................................................

    /**
     * Creates a new {@link SpreadsheetTextFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetTextFormatPattern parseTextFormatPattern(final String text) {
        return parsePattern(
                text,
                TEXT_FORMAT_PARSER,
                SpreadsheetPattern::transformText
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TEXT_FORMAT_PARSER = SpreadsheetFormatParsers.textFormat();

    /**
     * Transforms the tokens into a {@link SpreadsheetTextFormatPattern}
     */
    private static SpreadsheetTextFormatPattern transformText(final ParserToken token) {
        return SpreadsheetTextFormatPattern.with(token.cast(SpreadsheetFormatTextParserToken.class));
    }

    // parseTimeFormatPatterns..........................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetTimeFormatPattern parseTimeFormatPattern(final String text) {
        return parsePattern(
                text,
                TIME_FORMAT_PARSER,
                SpreadsheetPattern::transformTime
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_FORMAT_PARSER = SpreadsheetFormatParsers.timeFormat();

    /**
     * Transforms the tokens into a {@link SpreadsheetTimeFormatPattern}
     */
    private static SpreadsheetTimeFormatPattern transformTime(final ParserToken token) {
        return SpreadsheetTimeFormatPattern.with(
                token.cast(SpreadsheetFormatTimeParserToken.class)
        );
    }

    // parseTimeParsePatterns....................................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeParsePattern} after checking the value is valid.
     */
    public static SpreadsheetTimeParsePattern parseTimeParsePatterns(final String text) {
        return parsePattern(
                text,
                TIME_PARSE_PARSER,
                SpreadsheetTimeParsePattern::with
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSE_PARSER = SpreadsheetFormatParsers.timeParse();

    // helper...........................................................................................................

    /**
     * Parses text using the given parser and transformer.
     */
    private static <P extends SpreadsheetPattern> P parsePattern(final String text,
                                                                 final Parser<SpreadsheetFormatParserContext> parser,
                                                                 final Function<ParserToken, P> transformer) {
        Objects.requireNonNull(text, "text");

        try {
            return parser.parse(
                            TextCursors.charSequence(text),
                            SpreadsheetFormatParserContexts.basic()
                    )
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
    SpreadsheetPattern(final ParserToken value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public final ParserToken value() {
        return this.value;
    }

    final ParserToken value;

    // isXXX............................................................................................................

    /**
     * Returns true if holding date pattern(s)
     */
    public final boolean isDate() {
        return this instanceof SpreadsheetDateFormatPattern || this instanceof SpreadsheetDateParsePattern;
    }

    /**
     * Returns true if holding date/time pattern(s)
     */
    public final boolean isDateTime() {
        return this instanceof SpreadsheetDateTimeFormatPattern || this instanceof SpreadsheetDateTimeParsePattern;
    }

    /**
     * Returns true if holding number pattern(s)
     */
    public final boolean isNumber() {
        return this instanceof SpreadsheetNumberFormatPattern || this instanceof SpreadsheetNumberParsePattern;
    }

    /**
     * Returns true if holding text pattern(s)
     */
    public final boolean isText() {
        return this instanceof SpreadsheetTextFormatPattern;
    }

    /**
     * Returns true if holding time pattern(s)
     */
    public final boolean isTime() {
        return this instanceof SpreadsheetTimeFormatPattern || this instanceof SpreadsheetTimeParsePattern;
    }

    // TreePrintable....................................................................................................

    @Override
    public final void printTree(final IndentingPrinter printer) {
        printer.println(this.printTreeTypeName());

        printer.indent();
        {
            SpreadsheetPatternPrintTreeSpreadsheetFormatParserTokenVisitor.treePrint(
                    this.value,
                    printer
            );
        }

        printer.outdent();
    }

    abstract String printTreeTypeName();

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.value.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetPattern other) {
        return this.value.equals(other.value);
    }

    /**
     * Attempts to reconstruct an equivalent but not exact pattern representation of the given tokens. The actual
     * optional whitespace and separator tokens are not present only the individual patterns.
     */
    @Override
    public final String toString() {
        return CharSequences.quoteAndEscape(
                this.value.text()
        ).toString();
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDateFormatPattern} from a {@link JsonNode}.
     */
    static SpreadsheetDateFormatPattern unmarshallDateFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseDateFormatPattern(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateParsePattern} from a {@link JsonNode}.
     */
    static SpreadsheetDateParsePattern unmarshallDateParsePatterns(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return parseDateParsePatterns(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeFormatPattern} from a {@link JsonNode}.
     */
    static SpreadsheetDateTimeFormatPattern unmarshallDateTimeFormatPattern(final JsonNode node,
                                                                            final JsonNodeUnmarshallContext context) {
        return parseDateTimeFormatPattern(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePattern} from a {@link JsonNode}.
     */
    static SpreadsheetDateTimeParsePattern unmarshallDateTimeParsePatterns(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return parseDateTimeParsePatterns(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberFormatPattern} from a {@link JsonNode}.
     */
    static SpreadsheetNumberFormatPattern unmarshallNumberFormatPattern(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return parseNumberFormatPattern(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePattern} from a {@link JsonNode}.
     */
    static SpreadsheetNumberParsePattern unmarshallNumberParsePatterns(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return parseNumberParsePatterns(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTextFormatPattern} from a {@link JsonNode}.
     */
    static SpreadsheetTextFormatPattern unmarshallTextFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseTextFormatPattern(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeFormatPattern} from a {@link JsonNode}.
     */
    static SpreadsheetTimeFormatPattern unmarshallTimeFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseTimeFormatPattern(
                checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePattern} from a {@link JsonNode}.
     */
    static SpreadsheetTimeParsePattern unmarshallTimeParsePatterns(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return parseTimeParsePatterns(
                checkString(node)
        );
    }

    private static String checkString(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        return node.stringOrFail();
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.value.text()
        );
    }

    static {

        register(
                SpreadsheetDateFormatPattern.class,
                SpreadsheetPattern::unmarshallDateFormatPattern
        );

        register(
                SpreadsheetDateParsePattern.class,
                SpreadsheetPattern::unmarshallDateParsePatterns
        );

        register(
                SpreadsheetDateTimeFormatPattern.class,
                SpreadsheetPattern::unmarshallDateTimeFormatPattern
        );

        register(
                SpreadsheetDateTimeParsePattern.class,
                SpreadsheetPattern::unmarshallDateTimeParsePatterns
        );

        register(
                SpreadsheetNumberFormatPattern.class,
                SpreadsheetPattern::unmarshallNumberFormatPattern
        );

        register(
                SpreadsheetNumberParsePattern.class,
                SpreadsheetPattern::unmarshallNumberParsePatterns
        );

        register(
                SpreadsheetTextFormatPattern.class,
                SpreadsheetPattern::unmarshallTextFormatPattern
        );

        register(
                SpreadsheetTimeFormatPattern.class,
                SpreadsheetPattern::unmarshallTimeFormatPattern
        );

        register(
                SpreadsheetTimeParsePattern.class,
                SpreadsheetPattern::unmarshallTimeParsePatterns
        );
    }

    private static <P extends SpreadsheetPattern> void register(final Class<P> type,
                                                                final BiFunction<JsonNode, JsonNodeUnmarshallContext, P> unmarshaller) {
        JsonNodeContext.register(JsonNodeContext.computeTypeName(type),
                unmarshaller,
                SpreadsheetPattern::marshall,
                type);
    }
}
