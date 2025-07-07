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
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.ColorNameSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds a tokens that may be used to parse or format values along with helpers.
 */
abstract public class SpreadsheetPattern implements Value<ParserToken>,
    HasText,
    TreePrintable,
    HasUrlFragment {

    /**
     * The separator character between multiple patterns.
     */
    public static final CharacterConstant SEPARATOR = CharacterConstant.with(';');

    /**
     * Factory that creates a {@link ParserToken} parse the given token.
     */
    public static SpreadsheetDateFormatPattern dateFormatPattern(final ParserToken token) {
        return SpreadsheetDateFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateParsePattern} parse the given tokens.
     */
    public static SpreadsheetDateParsePattern dateParsePattern(final ParserToken token) {
        return SpreadsheetDateParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} parse the given token.
     */
    public static SpreadsheetDateTimeFormatPattern dateTimeFormatPattern(final ParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} parse the given tokens.
     */
    public static SpreadsheetDateTimeParsePattern dateTimeParsePattern(final ParserToken token) {
        return SpreadsheetDateTimeParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} parse the given token.
     */
    public static SpreadsheetNumberFormatPattern numberFormatPattern(final ParserToken token) {
        return SpreadsheetNumberFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} parse the given tokens.
     */
    public static SpreadsheetNumberParsePattern numberParsePattern(final ParserToken token) {
        return SpreadsheetNumberParsePattern.with(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetTextFormatPattern} parse the given token.
     */
    public static SpreadsheetTextFormatPattern textFormatPattern(final ParserToken token) {
        return SpreadsheetTextFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeFormatPattern} parse the given token.
     */
    public static SpreadsheetTimeFormatPattern timeFormatPattern(final ParserToken token) {
        return SpreadsheetTimeFormatPattern.with(token);
    }

    /**
     * Factory that creates a {@link ParserToken} parse the given tokens.
     */
    public static SpreadsheetTimeParsePattern timeParsePattern(final ParserToken token) {
        return SpreadsheetTimeParsePattern.with(token);
    }

    // Locale public factory............................................................................................

    /**
     * Creates a {@link SpreadsheetDateFormatPattern} using date patterns parse the {@link DateFormat} and {@link Locale}.
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
     * Creates a {@link SpreadsheetDateFormatPattern} using date patterns parse the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetDateParsePattern dateParsePatternLocale(final Locale locale) {
        return javaTextDateFormat(
            Lists.of(
                DateFormat.getDateInstance(DateFormat.FULL, locale),
                DateFormat.getDateInstance(DateFormat.LONG, locale),
                DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
                DateFormat.getDateInstance(DateFormat.SHORT, locale)
            ),
            DATE,
            NOT_TIME,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    /**
     * Creates a {@link SpreadsheetDateFormatPattern} using date/time patterns parse the {@link DateFormat} and {@link Locale}.
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

    public static SpreadsheetDateTimeParsePattern dateTimeParsePatternLocale(final Locale locale) {
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
            SpreadsheetPattern::parseDateTimeParsePattern
        );
    }


    /**
     * Creates a {@link SpreadsheetTimeFormatPattern} using time patterns parse the {@link DateFormat} and {@link Locale}.
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
     * Creates a {@link SpreadsheetTimeFormatPattern} using time patterns parse the {@link DateFormat} and {@link Locale}.
     */
    public static SpreadsheetTimeParsePattern timeParsePatternLocale(final Locale locale) {
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
            SpreadsheetPattern::parseTimeParsePattern
        );
    }

    private static void checkLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
    }

    /**
     * Returns an equivalent {@link SpreadsheetDateParsePattern} for the given {@link SimpleDateFormat}.
     * <br>
     * Note the {@link SpreadsheetDateParsePattern} will only have a single pattern.
     */
    public static SpreadsheetDateParsePattern dateParsePattern(final SimpleDateFormat simpleDateFormat) {
        return parseDateParsePattern(
            simpleDateFormat(
                simpleDateFormat,
                SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.EXCLUDE
            )
        );
    }

    /**
     * Returns an equivalent {@link SpreadsheetDateTimeParsePattern} for the given {@link SimpleDateFormat}.
     * <br>
     * Note the {@link SpreadsheetDateTimeParsePattern} will only have a single pattern.
     */
    public static SpreadsheetDateTimeParsePattern dateTimeParsePattern(final SimpleDateFormat simpleDateFormat) {
        return parseDateTimeParsePattern(
            simpleDateFormat(
                simpleDateFormat,
                SpreadsheetPatternSimpleDateFormatPatternVisitorYear.INCLUDE,
                SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE
            )
        );
    }

    /**
     * Returns an equivalent {@link SpreadsheetTimeParsePattern} for the given {@link SimpleDateFormat}.
     * <br>
     * Note the {@link SpreadsheetTimeParsePattern} will only have a single pattern.
     */
    public static SpreadsheetTimeParsePattern timeParsePattern(final SimpleDateFormat simpleDateFormat) {
        return parseTimeParsePattern(
            simpleDateFormat(
                simpleDateFormat,
                SpreadsheetPatternSimpleDateFormatPatternVisitorYear.EXCLUDE,
                SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.INCLUDE
            )
        );
    }

    private static String simpleDateFormat(final SimpleDateFormat simpleDateFormat,
                                           final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year,
                                           final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds) {
        Objects.requireNonNull(simpleDateFormat, "simpleDateFormat");

        final Set<String> patterns = Sets.ordered();

        final String simpleDateFormatPattern = simpleDateFormat.toPattern();

        // include all year, seconds, ampm
        visitSimpleDateFormatPattern(
            simpleDateFormatPattern,
            year,
            seconds,
            true,
            patterns
        );

        return String.join(
            SEPARATOR.string(),
            patterns
        );
    }

    private final static boolean DATE = true;
    private final static boolean NOT_DATE = !DATE;

    private final static boolean TIME = true;
    private final static boolean NOT_TIME = !TIME;

    /**
     * Factory that fetches a {@link DateFormat}, casts to a {@link SimpleDateFormat} and parses the pattern.
     */
    private static <P extends SpreadsheetPattern> P javaTextDateFormat(final DateFormat dateFormat,
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
     * Aggregates all the patterns for each and every {@link DateFormat}. The flags date and time may be used
     * to filter or control which pattern tokens in each {@link DateFormat} are actually recorded in the final
     * pattern and then used to create a {@link SpreadsheetFormatPattern} subclass.
     */
    private static <P extends SpreadsheetPattern> P javaTextDateFormat(final Iterable<DateFormat> dateFormats,
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
     * Takes a {@link DecimalFormat} and returns its {@link SpreadsheetNumberParsePattern} equivalent.
     * This method may be used to extract locale aware {@link SpreadsheetNumberParsePattern}. This will be used by
     * <a href="https://github.com/mP1/walkingkooka-spreadsheet-dominokit">to source locale aware number and currency patterns.</a>
     * <br>
     * Note it does not include text literals, doing so will cause failures when the {@link DecimalFormat#toPattern()}
     * is parsed.
     * <br>
     * The {@link SpreadsheetNumberParsePattern} returned will only contain a single pattern.
     */
    public static SpreadsheetNumberParsePattern decimalFormat(final DecimalFormat decimalFormat) {
        Objects.requireNonNull(decimalFormat, "decimalFormat");

        return parseNumberParsePattern(
            decimalFormat.toPattern()
                .replace('¤', '$') // international currency symbol
                .replace('\u00A0', ' ') // convert NBSP to space
                .replace('\u2007', ' ') // convert NBSP to space
                .replace('\u200f', ' ') // convert NBSP to space
                .replace("\u200e", "") // remove Left to right mark
        );
    }

    // parseDateFormatPatterns...........................................................................................

    /**
     * Creates a new {@link SpreadsheetDateFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetDateFormatPattern parseDateFormatPattern(final String text) {
        return SpreadsheetDateFormatPattern.with(
            parsePatternOrFail(
                text,
                DATE_FORMAT_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_FORMAT_PARSER = SpreadsheetFormatParsers.dateFormat();

    // parseDateParsePattern...........................................................................................

    /**
     * Creates a new {@link SpreadsheetDateParsePattern} after checking the value is valid.
     */
    public static SpreadsheetDateParsePattern parseDateParsePattern(final String text) {
        return SpreadsheetDateParsePattern.with(
            parsePatternOrFail(
                text,
                DATE_PARSE_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSE_PARSER = SpreadsheetFormatParsers.dateParse();

    // parseDateTimeFormatPatterns.......................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetDateTimeFormatPattern parseDateTimeFormatPattern(final String text) {
        return SpreadsheetDateTimeFormatPattern.with(
            parsePatternOrFail(
                text,
                DATETIME_FORMAT_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_FORMAT_PARSER = SpreadsheetFormatParsers.dateTimeFormat();

    // parseDateTimeParsePattern.......................................................................................

    /**
     * Creates a new {@link SpreadsheetDateTimeParsePattern} after checking the value is valid.
     */
    public static SpreadsheetDateTimeParsePattern parseDateTimeParsePattern(final String text) {
        return SpreadsheetDateTimeParsePattern.with(
            parsePatternOrFail(
                text,
                DATETIME_PARSE_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSE_PARSER = SpreadsheetFormatParsers.dateTimeParse();

    // parseNumberFormatPatterns.........................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetNumberFormatPattern parseNumberFormatPattern(final String text) {
        return SpreadsheetNumberFormatPattern.with(
            parsePatternOrFail(
                text,
                NUMBER_FORMAT_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_FORMAT_PARSER = SpreadsheetFormatParsers.numberFormat();

    // parseNumberParsePattern.........................................................................................

    /**
     * Creates a new {@link SpreadsheetNumberParsePattern} after checking the value is valid.
     */
    public static SpreadsheetNumberParsePattern parseNumberParsePattern(final String text) {
        return SpreadsheetNumberParsePattern.with(
            parsePatternOrFail(
                text,
                NUMBER_PARSE_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSE_PARSER = SpreadsheetFormatParsers.numberParse();

    // parseTextFormatPatterns..........................................................................................

    /**
     * Creates a new {@link SpreadsheetTextFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetTextFormatPattern parseTextFormatPattern(final String text) {
        return SpreadsheetTextFormatPattern.with(
            parsePatternOrFail(
                text,
                TEXT_FORMAT_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TEXT_FORMAT_PARSER = SpreadsheetFormatParsers.textFormat();

    // parseTimeFormatPatterns..........................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeFormatPattern} after checking the value is valid.
     */
    public static SpreadsheetTimeFormatPattern parseTimeFormatPattern(final String text) {
        return SpreadsheetTimeFormatPattern.with(
            parsePatternOrFail(
                text,
                TIME_FORMAT_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_FORMAT_PARSER = SpreadsheetFormatParsers.timeFormat();

    // parseTimeParsePattern....................................................................................................

    /**
     * Creates a new {@link SpreadsheetTimeParsePattern} after checking the value is valid.
     */
    public static SpreadsheetTimeParsePattern parseTimeParsePattern(final String text) {
        return SpreadsheetTimeParsePattern.with(
            parsePatternOrFail(
                text,
                TIME_PARSE_PARSER
            )
        );
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSE_PARSER = SpreadsheetFormatParsers.timeParse();

    // helper...........................................................................................................

    /**
     * Parses text using the given parser and transformer.
     */
    private static ParserToken parsePatternOrFail(final String text,
                                                  final Parser<SpreadsheetFormatParserContext> parser) {
        return parser.parseText(
            text,
            SpreadsheetFormatParserContexts.basic(
                InvalidCharacterExceptionFactory.POSITION_EXPECTED
            )
        );
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

    // toFormat.........................................................................................................

    /**
     * If necessary returns a {@link SpreadsheetFormatPattern} with the same pattern. For subclasses of {@link SpreadsheetFormatPattern}
     * this is always returned, for subclasses of {@link SpreadsheetParsePattern} an equivalent {@link SpreadsheetFormatPattern}
     * is returned with the same pattern.
     */
    public abstract SpreadsheetFormatPattern toFormat();

    // HasText..........................................................................................................

    /**
     * Returns the pattern in text form.
     */
    @Override
    public final String text() {
        return this.value().text();
    }

    // patterns........................................................................................................

    /**
     * Attempts to break down this {@link SpreadsheetPattern} into individual patterns for each pattern between {@link #SEPARATOR}.
     */
    public abstract List<? extends SpreadsheetPattern> patterns();

    /**
     * A raw {@link List} holding the cached patterns, initially set to null, and lazily populated. A raw List is used
     * to enable subclasses to return the list without casting.
     */
    @SuppressWarnings("rawtypes")
    List patterns;

    // formatter........................................................................................................

    /**
     * Returns a {@link SpreadsheetFormatter} built parse this pattern.
     */
    public final SpreadsheetPatternSpreadsheetFormatter formatter() {
        if (null == this.formatter) {
            this.formatter = this.createFormatter();
        }
        return this.formatter;
    }

    private SpreadsheetPatternSpreadsheetFormatter formatter;

    abstract SpreadsheetPatternSpreadsheetFormatter createFormatter();

    // patternKind......................................................................................................

    /**
     * Returns the matching {@link SpreadsheetPatternKind}
     */
    public final SpreadsheetPatternKind patternKind() {
        return SpreadsheetPatternKind.fromTypeName(
            "spreadsheet-" + this.printTreeTypeName()
        );
    }

    // HasUrlFragment...................................................................................................

    /**
     * Quotes after escaping the pattern.
     */
    @Override
    public final UrlFragment urlFragment() {
        return UrlFragment.with(
            this.value().text()
        );
    }

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

    // SpreadsheetNumberParsePattern -> number-parse-pattern
    private String printTreeTypeName() {
        return CaseKind.CAMEL.change(
            this.getClass()
                .getSimpleName()
                .substring("Spreadsheet".length()),
            CaseKind.KEBAB
        );
    }

    // color............................................................................................................

    /**
     * Returns a {@link SpreadsheetColorName} if one is included in this pattern.
     */
    public final Optional<SpreadsheetColorName> colorName() {
        this.failIfMultiplePatterns("get color name");

        return this.value()
            .findFirst(COLOR_NAME_PREDICATE)
            .map(t -> t.cast(ColorNameSpreadsheetFormatParserToken.class).colorName());
    }

    private final static Predicate<ParserToken> COLOR_NAME_PREDICATE = SpreadsheetFormatParserToken.predicate(SpreadsheetFormatParserToken::isColorName);

    /**
     * Returns a color number if one is included in this pattern.
     */
    public final OptionalInt colorNumber() {
        this.failIfMultiplePatterns("get color name");

        return this.value()
            .findFirst(
                SpreadsheetFormatParserToken.predicate(SpreadsheetFormatParserToken::isColorNumber)
            ).map(t ->
                OptionalInt.of(
                    t.cast(ColorNumberSpreadsheetFormatParserToken.class).value()
                )
            ).orElse(OptionalInt.empty());
    }

    /**
     * Removes any present color name parse this pattern. Only format patterns should actually attempt a remove,
     * parse patterns should just return this.
     */
    public abstract SpreadsheetPattern removeColor();

    final static Predicate<ParserToken> COLOR_PREDICATE = SpreadsheetFormatParserToken.predicate(SpreadsheetFormatParserToken::isColor);

    /**
     * Removes any existing color and then attempts to add the given {@link SpreadsheetColorName} to the pattern.
     * This will always fail for {@link SpreadsheetParsePattern} as colors are not allowed within parse patterns.
     */
    public abstract SpreadsheetPattern setColorName(final SpreadsheetColorName name);

    /**
     * Helper intended to be called by {@link SpreadsheetFormatPattern} subclasses.
     */
    final <T extends SpreadsheetPattern> T setColorName0(final SpreadsheetColorName name,
                                                         final Function<String, T> parser) {
        Objects.requireNonNull(name, "name");
        this.failIfMultiplePatterns("color name");

        return parser.apply(
            "[" + name + "]" + this.removeColor().text()
        );
    }

    /**
     * Removes any existing color and then attempts to add the given color number to the pattern.
     * This will always fail for {@link SpreadsheetParsePattern} as colors are not allowed within parse patterns.
     */
    public abstract SpreadsheetPattern setColorNumber(final int colorNumber);

    /**
     * Helper intended to be called by {@link SpreadsheetFormatPattern} subclasses.
     */
    final <T extends SpreadsheetPattern> T setColorNumber0(final int colorNumber,
                                                           final Function<String, T> parser) {
        SpreadsheetColors.checkNumber(colorNumber);
        this.failIfMultiplePatterns("color number");

        return parser.apply(
            "[color " + colorNumber + "]" + this.removeColor()
                .text()
        );
    }

    /**
     * Throws an {@link IllegalStateException} with a fail message for the given operation if multiple patterns are present
     * in this {@link SpreadsheetPattern} instance.
     */
    private void failIfMultiplePatterns(final String operation) {
        final int count = this.patterns().size();
        if (1 != count) {
            throw new IllegalStateException("Cannot " + operation + " for multiple patterns=" + this);
        }
    }

    // condition........................................................................................................

    /**
     * Removes any present condition parse this pattern if necessary.
     */
    public abstract SpreadsheetPattern removeCondition();

    final static Predicate<ParserToken> CONDITION_PREDICATE = SpreadsheetFormatParserToken.predicate(
        SpreadsheetFormatParserToken::isCondition
    );

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

    /**
     * Generalized helper that attempts to remove any token matched by the given {@link Predicate}. If a remove happened,
     * the factory is called to create a new {@link SpreadsheetPattern} otherwise this is returned.
     */
    final <T extends SpreadsheetFormatPattern> T removeIf0(final Predicate<ParserToken> predicate,
                                                           final Function<ParserToken, T> factory) {
        final ParserToken token = this.value();
        final Optional<?> removed = token.removeIf(predicate);

        return false == removed.isPresent() || token.equals(removed.get()) ?
            (T) this :
            factory.apply(
                (ParserToken) removed.get()
            );
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDateFormatPattern} parse a {@link JsonNode}.
     */
    static SpreadsheetDateFormatPattern unmarshallDateFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseDateFormatPattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateParsePattern} parse a {@link JsonNode}.
     */
    static SpreadsheetDateParsePattern unmarshallDateParsePattern(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return parseDateParsePattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeFormatPattern} parse a {@link JsonNode}.
     */
    static SpreadsheetDateTimeFormatPattern unmarshallDateTimeFormatPattern(final JsonNode node,
                                                                            final JsonNodeUnmarshallContext context) {
        return parseDateTimeFormatPattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimeParsePattern} parse a {@link JsonNode}.
     */
    static SpreadsheetDateTimeParsePattern unmarshallDateTimeParsePattern(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return parseDateTimeParsePattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberFormatPattern} parse a {@link JsonNode}.
     */
    static SpreadsheetNumberFormatPattern unmarshallNumberFormatPattern(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return parseNumberFormatPattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePattern} parse a {@link JsonNode}.
     */
    static SpreadsheetNumberParsePattern unmarshallNumberParsePattern(final JsonNode node,
                                                                      final JsonNodeUnmarshallContext context) {
        return parseNumberParsePattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTextFormatPattern} parse a {@link JsonNode}.
     */
    static SpreadsheetTextFormatPattern unmarshallTextFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseTextFormatPattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeFormatPattern} parse a {@link JsonNode}.
     */
    static SpreadsheetTimeFormatPattern unmarshallTimeFormatPattern(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return parseTimeFormatPattern(
            checkString(node)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePattern} parse a {@link JsonNode}.
     */
    static SpreadsheetTimeParsePattern unmarshallTimeParsePattern(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return parseTimeParsePattern(
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
            SpreadsheetPattern::unmarshallDateParsePattern
        );

        register(
            SpreadsheetDateTimeFormatPattern.class,
            SpreadsheetPattern::unmarshallDateTimeFormatPattern
        );

        register(
            SpreadsheetDateTimeParsePattern.class,
            SpreadsheetPattern::unmarshallDateTimeParsePattern
        );

        register(
            SpreadsheetNumberFormatPattern.class,
            SpreadsheetPattern::unmarshallNumberFormatPattern
        );

        register(
            SpreadsheetNumberParsePattern.class,
            SpreadsheetPattern::unmarshallNumberParsePattern
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
            SpreadsheetPattern::unmarshallTimeParsePattern
        );
    }

    private static <P extends SpreadsheetPattern> void register(final Class<P> type,
                                                                final BiFunction<JsonNode, JsonNodeUnmarshallContext, P> unmarshaller) {
        JsonNodeContext.register(JsonNodeContext.computeTypeName(type),
            unmarshaller,
            SpreadsheetPattern::marshall,
            type);
    }

    // constant here to avoid NPE during static init

    /**
     * The default {@link SpreadsheetTextFormatPattern}.
     */
    public static final SpreadsheetTextFormatPattern DEFAULT_TEXT_FORMAT_PATTERN = parseTextFormatPattern("@");
}
