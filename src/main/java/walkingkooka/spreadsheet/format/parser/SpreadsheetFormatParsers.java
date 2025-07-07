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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.BigIntegerParserToken;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Parsers that know how to parse formatting patterns<br>.
 * <a href="https://support.office.com/en-us/article/number-format-codes-5026bbd6-04bc-48cd-bf33-80f18b4eae68>Formatting</a>
 * <a href="https://support.google.com/docs/answer/56470?p=drive_custom_numbers&visit_id=637014719918429541-764379239&rd=1"</a>
 * <a href="https://support.google.com/drive/?p=drive_custom_numbers">Custom number</a>
 */
public final class SpreadsheetFormatParsers implements PublicStaticHelper {

    // shared

    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER = EbnfIdentifierName.with("WHITESPACE");
    private final static Parser<SpreadsheetFormatParserContext> WHITESPACE = Parsers.charPredicateString(CharPredicates.whitespace(), 1, Integer.MAX_VALUE)
        .transform(SpreadsheetFormatParsers::transformWhitespace)
        .setToString(WHITESPACE_IDENTIFIER.toString())
        .cast();

    private static ParserToken transformWhitespace(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.whitespace(
            token.cast(StringParserToken.class).value(),
            token.text()
        );
    }

    // color..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a color format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> color() {
        return COLOR_PARSER;
    }

    static final EbnfIdentifierName COLOR_IDENTIFIER = EbnfIdentifierName.with("COLOR");
    private final static Parser<SpreadsheetFormatParserContext> COLOR_PARSER;

    private static void color(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(COLOR_NUMBER_IDENTIFIER, COLOR_NUMBER);
        predefined.put(COLOR_NAME_IDENTIFIER, COLOR_NAME);
    }

    private static final EbnfIdentifierName COLOR_NUMBER_IDENTIFIER = EbnfIdentifierName.with("COLOR_NUMBER");

    private static final EbnfIdentifierName COLOR_NAME_IDENTIFIER = EbnfIdentifierName.with("COLOR_NAME");

    private static final Parser<SpreadsheetFormatParserContext> COLOR_NAME = Parsers.charPredicateString(CharPredicates.letter(), 1, Integer.MAX_VALUE)
        .transform(SpreadsheetFormatParsers::colorName)
        .setToString(COLOR_NAME_IDENTIFIER.toString())
        .cast();

    private static ColorNameSpreadsheetFormatParserToken colorName(final ParserToken string, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorName(
            string.cast(StringParserToken.class).value(),
            string.text()
        );
    }

    private static final Parser<SpreadsheetFormatParserContext> COLOR_DIGITS = Parsers.bigInteger(10)
        .transform(SpreadsheetFormatParsers::transformColorDigits)
        .setToString("COLOR_DIGITS")
        .cast();

    private static ParserToken transformColorDigits(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorNumber(
            token.cast(BigIntegerParserToken.class).value().intValueExact(),
            token.text()
        );
    }

    private static final Parser<SpreadsheetFormatParserContext> COLOR_NUMBER = Parsers.string(
            "COLOR",
            SpreadsheetStrings.CASE_SENSITIVITY
        ).transform(SpreadsheetFormatParsers::transformColorLiteral)
        .and(WHITESPACE.optional().cast())
        .and(COLOR_DIGITS.cast())
        .setToString(COLOR_NUMBER_IDENTIFIER.toString())
        .cast();

    private static ColorLiteralSymbolSpreadsheetFormatParserToken transformColorLiteral(final ParserToken string, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorLiteralSymbol(
            string.cast(StringParserToken.class).value(),
            string.text()
        );
    }

    // conditional..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a condition format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> condition() {
        return CONDITION_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> CONDITION_PARSER;

    private static void condition(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(CONDITION_NUMBER_LITERAL_IDENTIFIER, Parsers.bigDecimal()
            .transform(SpreadsheetFormatParsers::transformConditionNumber)
            .setToString(CONDITION_NUMBER_LITERAL_IDENTIFIER.toString())
            .cast());

        predefined.put(EQUALS_SYMBOL_IDENTIFIER, EQUALS_SYMBOL);
        predefined.put(NOT_EQUALS_SYMBOL_IDENTIFIER, NOT_EQUALS_SYMBOL);
        predefined.put(GREATER_THAN_SYMBOL_IDENTIFIER, GREATER_THAN_SYMBOL);
        predefined.put(GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER, GREATER_THAN_EQUALS_SYMBOL);
        predefined.put(LESS_THAN_SYMBOL_IDENTIFIER, LESS_THAN_SYMBOL);
        predefined.put(LESS_THAN_EQUALS_SYMBOL_IDENTIFIER, LESS_THAN_EQUALS_SYMBOL);
    }

    private static final EbnfIdentifierName CONDITION_NUMBER_LITERAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_NUMBER");

    private static ConditionNumberSpreadsheetFormatParserToken transformConditionNumber(final ParserToken token,
                                                                                        final ParserContext context) {
        return ConditionNumberSpreadsheetFormatParserToken.with(
            token.cast(BigDecimalParserToken.class).value(),
            token.text()
        );
    }

    private static final Parser<SpreadsheetFormatParserContext> EQUALS_SYMBOL = symbol(
        '=',
        SpreadsheetFormatParserToken::equalsSymbol
    );
    private static final EbnfIdentifierName EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("EQUALS");

    private static final Parser<SpreadsheetFormatParserContext> NOT_EQUALS_SYMBOL = symbol(
        "<>",
        SpreadsheetFormatParserToken::notEqualsSymbol
    );
    private static final EbnfIdentifierName NOT_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NOT_EQUALS");

    private static final Parser<SpreadsheetFormatParserContext> GREATER_THAN_SYMBOL = symbol(
        '>',
        SpreadsheetFormatParserToken::greaterThanSymbol
    );
    private static final EbnfIdentifierName GREATER_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN");

    private static final Parser<SpreadsheetFormatParserContext> GREATER_THAN_EQUALS_SYMBOL = symbol(
        ">=",
        SpreadsheetFormatParserToken::greaterThanEqualsSymbol
    );
    private static final EbnfIdentifierName GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN_EQUALS");

    private static final Parser<SpreadsheetFormatParserContext> LESS_THAN_SYMBOL = symbol(
        '<',
        SpreadsheetFormatParserToken::lessThanSymbol
    );
    private static final EbnfIdentifierName LESS_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN");

    private static final Parser<SpreadsheetFormatParserContext> LESS_THAN_EQUALS_SYMBOL = symbol(
        "<=",
        SpreadsheetFormatParserToken::lessThanEqualsSymbol
    );
    private static final EbnfIdentifierName LESS_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN_EQUALS");

    // date..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a date format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> dateFormat() {
        return DATE_FORMAT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_FORMAT_PARSER;

    /**
     * Returns a {@link Parser} that returns a date format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> dateParse() {
        return DATE_PARSE_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSE_PARSER;

    private static void date(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> parsers) {
        parsers.put(DAY_IDENTIFIER, DAY);
        parsers.put(YEAR_IDENTIFIER, YEAR);
    }

    private static final EbnfIdentifierName DAY_IDENTIFIER = EbnfIdentifierName.with("DAY");
    private static final Parser<SpreadsheetFormatParserContext> DAY = repeatingSymbol(
        'D',
        SpreadsheetFormatParserToken::day
    );

    private static final EbnfIdentifierName YEAR_IDENTIFIER = EbnfIdentifierName.with("YEAR");
    private static final Parser<SpreadsheetFormatParserContext> YEAR = repeatingSymbol(
        'Y',
        SpreadsheetFormatParserToken::year
    );

    // dateTime..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a datetime format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> dateTimeFormat() {
        return DATETIME_FORMAT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_FORMAT_PARSER;

    /**
     * Returns a {@link Parser} that returns a datetime format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> dateTimeParse() {
        return DATETIME_PARSE_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSE_PARSER;

    private static void dateAndTime(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(MINUTE_IDENTIFIER, MINUTE);
        predefined.put(MONTH_IDENTIFIER, MONTH);
    }

    private static final EbnfIdentifierName MINUTE_IDENTIFIER = EbnfIdentifierName.with("MINUTE");

    private static final Parser<SpreadsheetFormatParserContext> MINUTE = repeatingSymbol(
        'M',
        SpreadsheetFormatParserToken::minute
    );

    private static final EbnfIdentifierName MONTH_IDENTIFIER = EbnfIdentifierName.with("MONTH");

    private static final Parser<SpreadsheetFormatParserContext> MONTH = repeatingSymbol(
        'M',
        SpreadsheetFormatParserToken::month
    );

    // general ..........................................................................................................

    /**
     * Returns a {@link Parser} that matches a general token.
     */
    public static Parser<SpreadsheetFormatParserContext> general() {
        return GENERAL_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> GENERAL_PARSER;
    static final EbnfIdentifierName GENERAL_IDENTIFIER = EbnfIdentifierName.with("GENERAL");

    private static void general(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(GENERAL_SYMBOL_IDENTIFIER, GENERAL_SYMBOL);
    }

    private static final EbnfIdentifierName GENERAL_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GENERAL_SYMBOL");

    private static final Parser<SpreadsheetFormatParserContext> GENERAL_SYMBOL = Parsers.string(
            "GENERAL",
            SpreadsheetStrings.CASE_SENSITIVITY
        ).transform(SpreadsheetFormatParsers::transformGeneralSymbol)
        .setToString("GENERAL")
        .cast();

    private static ParserToken transformGeneralSymbol(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.generalSymbol(
            token.cast(StringParserToken.class).value(),
            token.text()
        );
    }

    // fraction..........................................................................................................

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> fraction() {
        return FRACTION_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> FRACTION_PARSER;

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> numberFormat() {
        return NUMBER_FORMAT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_FORMAT_PARSER;

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> numberParse() {
        return NUMBER_PARSE_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSE_PARSER;

    private static void number(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(CURRENCY_IDENTIFIER, CURRENCY);
        predefined.put(DECIMAL_POINT_IDENTIFIER, DECIMAL_POINT_PARSER);
        predefined.put(DIGIT_IDENTIFIER, DIGIT);
        predefined.put(DIGIT_SPACE_IDENTIFIER, DIGIT_SPACE);
        predefined.put(DIGIT_ZERO_IDENTIFIER, DIGIT_ZERO);
        predefined.put(FRACTION_SYMBOL_IDENTIFIER, FRACTION_SYMBOL);
        predefined.put(GROUP_SEPARATOR_IDENTIFIER, GROUP_SEPARATOR);
        predefined.put(PERCENTAGE_IDENTIFIER, PERCENTAGE);
    }

    private static final EbnfIdentifierName CURRENCY_IDENTIFIER = EbnfIdentifierName.with("CURRENCY");
    private static final Parser<SpreadsheetFormatParserContext> CURRENCY = symbol(
        '$',
        SpreadsheetFormatParserToken::currency
    );

    private static final EbnfIdentifierName DECIMAL_POINT_IDENTIFIER = EbnfIdentifierName.with("DECIMAL_POINT");
    private static final Parser<SpreadsheetFormatParserContext> DECIMAL_POINT_PARSER = symbol(
        '.',
        SpreadsheetFormatParserToken::decimalPoint
    );

    private static final EbnfIdentifierName DIGIT_IDENTIFIER = EbnfIdentifierName.with("DIGIT");
    private static final Parser<SpreadsheetFormatParserContext> DIGIT = symbol(
        '#',
        SpreadsheetFormatParserToken::digit
    );

    private static final EbnfIdentifierName DIGIT_SPACE_IDENTIFIER = EbnfIdentifierName.with("DIGIT_SPACE");
    private static final Parser<SpreadsheetFormatParserContext> DIGIT_SPACE = symbol(
        '?',
        SpreadsheetFormatParserToken::digitSpace
    );

    private static final EbnfIdentifierName DIGIT_ZERO_IDENTIFIER = EbnfIdentifierName.with("DIGIT_ZERO");
    private static final Parser<SpreadsheetFormatParserContext> DIGIT_ZERO = symbol(
        '0',
        SpreadsheetFormatParserToken::digitZero
    );

    private static final EbnfIdentifierName FRACTION_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("FRACTION_SYMBOL");
    private static final Parser<SpreadsheetFormatParserContext> FRACTION_SYMBOL = symbol(
        '/',
        SpreadsheetFormatParserToken::fractionSymbol
    );

    private static final Parser<SpreadsheetFormatParserContext> GROUP_SEPARATOR = symbol(
        ',',
        SpreadsheetFormatParserToken::groupSeparator
    );
    private static final EbnfIdentifierName GROUP_SEPARATOR_IDENTIFIER = EbnfIdentifierName.with("GROUP_SEPARATOR");

    private static final EbnfIdentifierName PERCENTAGE_IDENTIFIER = EbnfIdentifierName.with("PERCENTAGE");
    private static final Parser<SpreadsheetFormatParserContext> PERCENTAGE = symbol(
        '%',
        SpreadsheetFormatParserToken::percent
    );

    // pattern..........................................................................................................

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> patternSeparator() {
        return PATTERN_SEPARATOR_SYMBOL_PARSER.cast();
    }

    private static final Parser<SpreadsheetFormatParserContext> PATTERN_SEPARATOR_SYMBOL_PARSER = symbol(
        ';', // cant call SpreadsheetPattern.SEPARATOR.character() because will NPE,
        SpreadsheetFormatParserToken::separatorSymbol
    );

    private static final EbnfIdentifierName PATTERN_SEPARATOR_IDENTIFIER = EbnfIdentifierName.with("PATTERN_SEPARATOR");

    private static void patternSeparator(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(PATTERN_SEPARATOR_IDENTIFIER, PATTERN_SEPARATOR_SYMBOL_PARSER);
    }

    // text..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a text format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> textFormat() {
        return TEXT_FORMAT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> TEXT_FORMAT_PARSER;
    static final EbnfIdentifierName TEXT_FORMAT = EbnfIdentifierName.with("TEXT_FORMAT");

    private static void text(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(QUOTED_IDENTIFIER, QUOTED);
        predefined.put(STAR_IDENTIFIER, STAR);
        predefined.put(TEXT_PLACEHOLDER_IDENTIFIER, TEXT_PLACEHOLDER);
        predefined.put(UNDERSCORE_IDENTIFIER, UNDERSCORE);
        predefined.put(WHITESPACE_IDENTIFIER, WHITESPACE);
    }

    private static final EbnfIdentifierName QUOTED_IDENTIFIER = EbnfIdentifierName.with("QUOTED");
    private static final Parser<SpreadsheetFormatParserContext> QUOTED = Parsers.doubleQuoted()
        .transform(SpreadsheetFormatParsers::transformQuoted)
        .setToString("QUOTED")
        .cast();

    private static ParserToken transformQuoted(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.quotedText(
            token.cast(DoubleQuotedParserToken.class).value(),
            token.text()
        );
    }

    private static final EbnfIdentifierName STAR_IDENTIFIER = EbnfIdentifierName.with("STAR");
    private static final Parser<SpreadsheetFormatParserContext> STAR = escapeStarOrUnderline(
        '*',
        SpreadsheetFormatParserToken::star,
        "STAR"
    );

    private static final EbnfIdentifierName TEXT_PLACEHOLDER_IDENTIFIER = EbnfIdentifierName.with("TEXT_PLACEHOLDER");
    private static final Parser<SpreadsheetFormatParserContext> TEXT_PLACEHOLDER = symbol(
        '@',
        SpreadsheetFormatParserToken::textPlaceholder
    );

    private static final EbnfIdentifierName UNDERSCORE_IDENTIFIER = EbnfIdentifierName.with("UNDERSCORE");
    private static final Parser<SpreadsheetFormatParserContext> UNDERSCORE = escapeStarOrUnderline(
        '_',
        SpreadsheetFormatParserToken::underscore,
        "UNDERSCORE"
    );

    // time..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a time format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> timeFormat() {
        return TIME_FORMAT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_FORMAT_PARSER;

    /**
     * Returns a {@link Parser} that returns a time format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> timeParse() {
        return TIME_PARSE_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSE_PARSER;


    private static void time(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(A_SLASH_P_IDENTIFIER, A_SLASH_P);
        predefined.put(AM_SLASH_PM_IDENTIFIER, AM_SLASH_PM);
        predefined.put(HOUR_IDENTIFIER, HOUR);
        predefined.put(SECOND_IDENTIFIER, SECOND);
    }

    private static final EbnfIdentifierName A_SLASH_P_IDENTIFIER = EbnfIdentifierName.with("A_SLASH_P");
    private static final Parser<SpreadsheetFormatParserContext> A_SLASH_P = symbol(
        "A/P",
        SpreadsheetFormatParserToken::amPm
    );

    private static final EbnfIdentifierName AM_SLASH_PM_IDENTIFIER = EbnfIdentifierName.with("AM_SLASH_PM");
    private static final Parser<SpreadsheetFormatParserContext> AM_SLASH_PM = symbol(
        "AM/PM",
        SpreadsheetFormatParserToken::amPm
    );

    private static final EbnfIdentifierName HOUR_IDENTIFIER = EbnfIdentifierName.with("HOUR");
    private static final Parser<SpreadsheetFormatParserContext> HOUR = repeatingSymbol(
        'H',
        SpreadsheetFormatParserToken::hour
    );

    private static final EbnfIdentifierName SECOND_IDENTIFIER = EbnfIdentifierName.with("SECOND");
    private static final Parser<SpreadsheetFormatParserContext> SECOND = repeatingSymbol(
        'S',
        SpreadsheetFormatParserToken::second
    );

    // misc..............................................................................................................

    private static void misc(final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined) {
        predefined.put(BRACKET_OPEN_IDENTIFIER, BRACKET_OPEN);
        predefined.put(BRACKET_CLOSE_IDENTIFIER, BRACKET_CLOSE);

        predefined.put(ESCAPE_IDENTIFIER, ESCAPE);
        predefined.put(NUMBER_LITERAL_IDENTIFIER, NUMBER_LITERAL);
        predefined.put(FRACTION_LITERAL_IDENTIFIER, FRACTION_LITERAL);
        predefined.put(DATETIME_TEXT_LITERAL_IDENTIFIER, DATETIME_TEXT_LITERAL);
    }

    private static final EbnfIdentifierName BRACKET_OPEN_IDENTIFIER = EbnfIdentifierName.with("BRACKET_OPEN");
    private static final Parser<SpreadsheetFormatParserContext> BRACKET_OPEN = symbol(
        '[',
        SpreadsheetFormatParserToken::bracketOpenSymbol
    );

    private static final EbnfIdentifierName BRACKET_CLOSE_IDENTIFIER = EbnfIdentifierName.with("BRACKET_CLOSE");
    private static final Parser<SpreadsheetFormatParserContext> BRACKET_CLOSE = symbol(
        ']',
        SpreadsheetFormatParserToken::bracketCloseSymbol
    );

    private static final EbnfIdentifierName ESCAPE_IDENTIFIER = EbnfIdentifierName.with("ESCAPE");
    private static final Parser<SpreadsheetFormatParserContext> ESCAPE = escapeStarOrUnderline(
        '\\',
        SpreadsheetFormatParserToken::escape,
        "ESCAPE"
    );

    private static final EbnfIdentifierName NUMBER_LITERAL_IDENTIFIER = EbnfIdentifierName.with("NUMBER_LITERAL");
    private static final Parser<SpreadsheetFormatParserContext> NUMBER_LITERAL = literal(
        "(): +-=<>",
        NUMBER_LITERAL_IDENTIFIER
    );

    /**
     * Note fractions literals are slightly different to number literals, eg percentage-symbol
     */
    private static final EbnfIdentifierName FRACTION_LITERAL_IDENTIFIER = EbnfIdentifierName.with("FRACTION_LITERAL");
    private static final Parser<SpreadsheetFormatParserContext> FRACTION_LITERAL = literal(
        "(): +-=<>%,",
        FRACTION_LITERAL_IDENTIFIER
    );

    private static final EbnfIdentifierName DATETIME_TEXT_LITERAL_IDENTIFIER = EbnfIdentifierName.with("DATETIME_TEXT_LITERAL");
    private static final Parser<SpreadsheetFormatParserContext> DATETIME_TEXT_LITERAL = literal(
        "$-+(): /+-,",
        DATETIME_TEXT_LITERAL_IDENTIFIER);

    // helpers..............................................................................................................

    static final EbnfIdentifierName DATE_FORMAT = EbnfIdentifierName.with("DATE_FORMAT");
    static final EbnfIdentifierName DATE_PARSE = EbnfIdentifierName.with("DATE_PARSE");

    static final EbnfIdentifierName DATETIME_FORMAT = EbnfIdentifierName.with("DATETIME_FORMAT");
    static final EbnfIdentifierName DATETIME_PARSE = EbnfIdentifierName.with("DATETIME_PARSE");

    static final EbnfIdentifierName NUMBER_FORMAT = EbnfIdentifierName.with("NUMBER_FORMAT");
    static final EbnfIdentifierName NUMBER_PARSE = EbnfIdentifierName.with("NUMBER_PARSE");

    static final EbnfIdentifierName TIME_FORMAT = EbnfIdentifierName.with("TIME_FORMAT");
    static final EbnfIdentifierName TIME_PARSE = EbnfIdentifierName.with("TIME_PARSE");

    /*
     * Parsers the grammar and returns the selected parser.
     */
    static {
        final String filename = SpreadsheetFormatParsers.class.getSimpleName() + "Grammar.txt";

        final Map<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> predefined = Maps.sorted();

        patternSeparator(predefined);
        color(predefined);
        condition(predefined);
        date(predefined);
        dateAndTime(predefined);
        general(predefined);
        number(predefined);
        text(predefined);
        time(predefined);

        misc(predefined);

        final Function<EbnfIdentifierName, Parser<SpreadsheetFormatParserContext>> parsers = EbnfParserToken.parseFile(
            new SpreadsheetFormatParsersGrammarProvider()
                .text(),
            filename
        ).combinatorForFile(
            (nn) -> Optional.ofNullable(
                predefined.get(nn)
            ),
            SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer.create(),
            filename
        );

        COLOR_PARSER = parsers.apply(COLOR_IDENTIFIER);
        CONDITION_PARSER = parsers.apply(EbnfIdentifierName.with("CONDITION"));

        DATE_FORMAT_PARSER = parsers.apply(DATE_FORMAT)
            .orFailIfCursorNotEmpty(ParserReporters.basic());
        DATE_PARSE_PARSER = parsers.apply(DATE_PARSE)
            .orFailIfCursorNotEmpty(ParserReporters.basic());

        DATETIME_FORMAT_PARSER = parsers.apply(DATETIME_FORMAT)
            .orFailIfCursorNotEmpty(ParserReporters.basic());
        DATETIME_PARSE_PARSER = parsers.apply(DATETIME_PARSE)
            .orFailIfCursorNotEmpty(ParserReporters.basic());

        FRACTION_PARSER = parsers.apply(EbnfIdentifierName.with("FRACTION"));
        GENERAL_PARSER = parsers.apply(GENERAL_IDENTIFIER);

        NUMBER_FORMAT_PARSER = parsers.apply(NUMBER_FORMAT)
            .orFailIfCursorNotEmpty(ParserReporters.basic());
        NUMBER_PARSE_PARSER = parsers.apply(NUMBER_PARSE)
            .orFailIfCursorNotEmpty(ParserReporters.basic());

        TEXT_FORMAT_PARSER = parsers.apply(TEXT_FORMAT)
            .orFailIfCursorNotEmpty(ParserReporters.basic());

        TIME_FORMAT_PARSER = parsers.apply(TIME_FORMAT)
            .orFailIfCursorNotEmpty(ParserReporters.basic());
        TIME_PARSE_PARSER = parsers.apply(TIME_PARSE)
            .orFailIfCursorNotEmpty(ParserReporters.basic());
    }

    private static Parser<SpreadsheetFormatParserContext> literal(final String any,
                                                                  final EbnfIdentifierName name) {
        return Parsers.charPredicateString(
                CharPredicates.any(any),
                1, // min
                65535 // max
            ).transform(SpreadsheetFormatParsers::literalTransform)
            .setToString(name.toString())
            .cast();
    }

    /**
     * Includes logic making a special case of space characters transforming them into a {@link SpreadsheetFormatParserToken#whitespace(String, String)}.
     */
    private static SpreadsheetFormatParserToken literalTransform(final ParserToken token,
                                                                 final ParserContext context) {
        final StringParserToken stringParserToken = token.cast(StringParserToken.class);
        final String value = stringParserToken.value();
        final String text = stringParserToken.text();

        return SpreadsheetFormatParserToken.textLiteral(
            value,
            text
        );
    }

    /**
     * Matches a token filled with the given c ignoring case.
     */
    private static Parser<SpreadsheetFormatParserContext> repeatingSymbol(final char c,
                                                                          final BiFunction<String, String, ParserToken> factory) {
        return Parsers.charPredicateString(
                SpreadsheetStrings.CASE_SENSITIVITY.charPredicate(c),
                1,
                Integer.MAX_VALUE
            ).transform(
                (stringParserToken, context) ->
                    factory.apply(stringParserToken.cast(StringParserToken.class)
                            .value(),
                        stringParserToken.text()
                    )
            ).setToString("{" + CharSequences.quoteAndEscape(String.valueOf(c)) + "}")
            .cast();
    }

    /**
     * Matches a token holding a single character.
     */
    private static Parser<SpreadsheetFormatParserContext> symbol(final char c,
                                                                 final BiFunction<String, String, ParserToken> factory) {
        return Parsers.character(CaseSensitivity.SENSITIVE.charPredicate(c))
            .transform(
                (characterParserToken, context) ->
                    factory.apply(
                        characterParserToken.cast(CharacterParserToken.class)
                            .value()
                            .toString(),
                        characterParserToken.text()
                    )
            ).setToString(
                CharSequences.quoteAndEscape(
                    String.valueOf(c)
                ).toString()
            ).cast();
    }

    private static Parser<SpreadsheetFormatParserContext> symbol(final String text,
                                                                 final BiFunction<String, String, ParserToken> factory) {
        return Parsers.string(
            text,
            SpreadsheetStrings.CASE_SENSITIVITY
        ).transform(
            (stringParserToken, context) ->
                factory.apply(
                    stringParserToken.cast(StringParserToken.class)
                        .value(),
                    stringParserToken.text()
                )
        ).setToString(
            CharSequences.quoteAndEscape(text)
                .toString()
        ).cast();
    }

    /**
     * This parser requires two characters, the second becomes the {@link Character} value of the {@link SpreadsheetFormatParserToken}
     */
    private static Parser<SpreadsheetFormatParserContext> escapeStarOrUnderline(final char initial,
                                                                                final BiFunction<Character, String, ParserToken> factory,
                                                                                final String toString) {
        return Parsers.initialAndPartCharPredicateString(
                CharPredicates.is(initial),
                CharPredicates.always(),
                2,
                2
            ).transform(
                (stringParserToken, context) ->
                    factory.apply(stringParserToken.cast(StringParserToken.class)
                            .value()
                            .charAt(1),
                        stringParserToken.text()
                    )
            ).setToString(toString)
            .cast();
    }

    /**
     * Stop construction
     */
    private SpreadsheetFormatParsers() {
        throw new UnsupportedOperationException();
    }
}
