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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.format.parser.AmPmSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DaySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DecimalPointSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitZeroSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EscapeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.HourSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MinuteSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MonthSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.QuotedTextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SecondSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.StarSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextLiteralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextPlaceholderSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.UnderscoreSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.WhitespaceSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.YearSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.formula.parser.AmPmSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.HourSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinuteSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SecondsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.WhitespaceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.YearSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that creates a parser, by mapping {@link SpreadsheetFormatParserToken}
 * into a parser made up of multiple tokens.
 */
final class SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    /**
     * Creates a {@link SpreadsheetParser} for each of the individual date/datetime/time individual patterns.
     */
    static SpreadsheetParser toParser(final ParserToken token,
                                      final ValidationValueTypeName valueType) {
        final SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);

        return SpreadsheetNonNumberParsePatternSpreadsheetParser.with(
            Parsers.alternatives(visitor.parsers)
                .andEmptyTextCursor()
                .setToString(
                    CharSequences.quoteAndEscape(
                        token.toString()
                    ).toString()
                ),
            token,
            Optional.of(valueType)
        );
    }

    SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor() {
        super();
    }


    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return this.startParser();
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return this.startParser();
    }

    @Override
    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        return this.startParser();
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return this.startParser();
    }

    private Visiting startParser() {
        this.parser = null;
        this.milliseconds = 0;
        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormatParserToken token) {
        this.endParser(
            token,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::transformDate
        );
    }

    private static DateSpreadsheetFormulaParserToken transformDate(final ParserToken token,
                                                                   final SpreadsheetParserContext context) {
        return transform(
            token,
            SpreadsheetFormulaParserToken::date
        );
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormatParserToken token) {
        this.endParser(
            token,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::transformDateTime
        );
    }

    private static DateTimeSpreadsheetFormulaParserToken transformDateTime(final ParserToken token,
                                                                           final SpreadsheetParserContext context) {
        return transform(
            token,
            SpreadsheetFormulaParserToken::dateTime
        );
    }

    @Override
    protected void endVisit(final GeneralSpreadsheetFormatParserToken token) {
        this.endParser(
            token,
            null
        );
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormatParserToken token) {
        this.endParser(
            token,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::transformTime
        );
    }

    private static TimeSpreadsheetFormulaParserToken transformTime(final ParserToken token,
                                                                   final SpreadsheetParserContext context) {
        return transform(
            token,
            SpreadsheetFormulaParserToken::time
        );
    }

    private static <T extends SpreadsheetFormulaParserToken> T transform(final ParserToken token,
                                                                         final BiFunction<List<ParserToken>, String, T> factory) {
        return factory.apply(
            token instanceof SequenceParserToken ?
                token.cast(SequenceParserToken.class)
                    .flat()
                    .value() :
                Lists.of(token),
            token.text()
        );
    }

    private void endParser(final SpreadsheetFormatParserToken token,
                           final BiFunction<ParserToken, SpreadsheetParserContext, ParserToken> transformer) {
        this.appendDecimalSeparatorMillisecondsIfNecessary();

        Parser<SpreadsheetParserContext> parser = this.parser;
        if (null != transformer) {
            parser = parser.transform(transformer);
        }

        this.parsers.add(
            parser.andEmptyTextCursor()
                .setToString(token.text())
        );

        this.parser = null;
    }

    private final List<Parser<SpreadsheetParserContext>> parsers = Lists.array();

    // symbols within a date/datetime/time..............................................................................

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.text(
            SpreadsheetNonNumberParsePatternParser.stringChoices(
                SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::ampm,
                SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::spreadsheetAmPmParserToken,
                token.text()
            )
        );
    }

    private static List<String> ampm(final SpreadsheetParserContext context) {
        return context.ampms();
    }

    private static AmPmSpreadsheetFormulaParserToken spreadsheetAmPmParserToken(final int choice,
                                                                                final String text) {
        return SpreadsheetFormulaParserToken.amPm(choice * 12, text);
    }

    @Override
    protected void visit(final DaySpreadsheetFormatParserToken token) {
        this.value(
            1,
            2,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::day
        );
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link DayNumberSpreadsheetFormulaParserToken}.
     */
    private static DayNumberSpreadsheetFormulaParserToken day(final ParserToken token,
                                                              final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::dayNumber
        );
    }

    /**
     * Records that a decimal point needs to be parsed. The actual parser will be added with or before the milliseconds parser.
     */
    @Override
    protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
        this.milliseconds = 1;
    }

    // milliseconds...
    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.milliseconds++;
    }

    @Override
    protected void visit(final EscapeSpreadsheetFormatParserToken token) {
        this.literal(token.value());
    }

    @Override
    protected void visit(final HourSpreadsheetFormatParserToken token) {
        this.value(
            1,
            2,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::hour
        );
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link HourSpreadsheetFormulaParserToken}.
     */
    private static HourSpreadsheetFormulaParserToken hour(final ParserToken token,
                                                          final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::hour
        );
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormatParserToken token) {
        this.value(
            1,
            2,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::minute
        );
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link MinuteSpreadsheetFormulaParserToken}.
     */
    private static MinuteSpreadsheetFormulaParserToken minute(final ParserToken token,
                                                              final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::minute
        );
    }

    @Override
    protected void visit(final MonthSpreadsheetFormatParserToken token) {
        final int length = token.value().length();

        switch (length) {
            case 1:
            case 2:
                this.value(
                    1,
                    2,
                    SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::month
                );
                break;
            case 3:
                this.addParser(
                    SpreadsheetNonNumberParsePatternParser.stringChoices(
                        SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::monthNamesAbbreviations,
                        SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::monthNameAbbreviationParserToken,
                        token.text()
                    )
                );
                break;
            default:
                this.addParser(
                    SpreadsheetNonNumberParsePatternParser.stringChoices(
                        SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::monthNames,
                        SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::monthNameParserToken,
                        token.text()
                    )
                );
                break;
        }
    }

    private static List<String> monthNames(final SpreadsheetParserContext context) {
        return context.monthNames();
    }

    private static MonthNameSpreadsheetFormulaParserToken monthNameParserToken(final int value,
                                                                               final String text) {
        return SpreadsheetFormulaParserToken.monthName(
            value + 1,
            text
        ); // JAN=1 but SpreadsheetNonNumberParsePatternParser.stringChoices 1st = 0.
    }

    private static List<String> monthNamesAbbreviations(final SpreadsheetParserContext context) {
        return context.monthNameAbbreviations();
    }

    private static MonthNameAbbreviationSpreadsheetFormulaParserToken monthNameAbbreviationParserToken(final int value,
                                                                                                       final String text) {
        return SpreadsheetFormulaParserToken.monthNameAbbreviation(
            value + 1,
            text
        ); // JAN=1 but SpreadsheetNonNumberParsePatternParser.stringChoices 1st = 0.
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link MonthNumberSpreadsheetFormulaParserToken}.
     */
    private static MonthNumberSpreadsheetFormulaParserToken month(final ParserToken token,
                                                                  final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::monthNumber
        );
    }

    @Override
    protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.value(
            1,
            2,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::seconds
        );
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link SecondsSpreadsheetFormulaParserToken}.
     */
    private static SecondsSpreadsheetFormulaParserToken seconds(final ParserToken token,
                                                                final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::seconds
        );
    }

    @Override
    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        // consume but ignore separator.
    }

    @Override
    protected void visit(final StarSpreadsheetFormatParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        this.literal(token.value());
    }

    @Override
    protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final UnderscoreSpreadsheetFormatParserToken token) {
        this.literal(
            CharSequences.repeating(
                ' ',
                token.text().length()
            ).toString()
        );
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormatParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final YearSpreadsheetFormatParserToken token) {
        switch (token.value().length()) {
            case 1:
                this.year(1, 2);
                break;
            case 2:
                this.year(2, 2);
                break;
            default:
                this.year(4, 4);
                break;
        }
    }

    private void year(final int min,
                      final int max) {
        this.value(
            min,
            max,
            SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::year
        );
    }

    /**
     * Transforms the {@link SpreadsheetFormulaParserToken} into a {@link YearSpreadsheetFormulaParserToken}.
     */
    private static YearSpreadsheetFormulaParserToken year(final ParserToken token,
                                                          final SpreadsheetParserContext context) {
        return token(
            token,
            SpreadsheetFormulaParserToken::year
        );
    }

    // helpers.........................................................................................................

    /**
     * Creates a {@link SpreadsheetFormulaParserToken} that has an Integer, String factory.
     */
    private static <T> T token(final ParserToken token,
                               final BiFunction<Integer, String, T> factory) {
        final StringParserToken stringParserToken = token.cast(StringParserToken.class);

        return factory.apply(
            Integer.parseInt(stringParserToken.value()),
            stringParserToken.text()
        );
    }

    private void text(final SpreadsheetNonNumberParsePatternParser parser) {
        this.appendDecimalSeparatorMillisecondsIfNecessary();
        this.addParser(parser);
    }

    private void value(final int minWidth,
                       final int maxWidth,
                       final BiFunction<ParserToken, SpreadsheetParserContext, ParserToken> transformer) {
        this.appendDecimalSeparatorMillisecondsIfNecessary();

        this.value0(
            minWidth,
            maxWidth,
            transformer
        );
    }

    private void value0(final int minWidth,
                        final int maxWidth,
                        final BiFunction<ParserToken, SpreadsheetParserContext, ParserToken> transformer) {
        this.addParser(
            Parsers.<SpreadsheetParserContext>charPredicateString(
                CharPredicates.digit(),
                minWidth,
                maxWidth
            ).transform(transformer)
        );
    }

    private void literal(final Value<String> value) {
        this.literal(value.value()); // text may have quotes etc.
    }

    private void literal(final char c) {
        this.appendDecimalSeparatorMillisecondsIfNecessary();
        this.literal0(c);
    }

    private void literal0(final char c) {
        this.literal(String.valueOf(c));
    }

    private void literal(final String text) {
        this.appendDecimalSeparatorMillisecondsIfNecessary();
        this.addParser(
            Parsers.string(
                    text,
                    CaseSensitivity.SENSITIVE
                ).transform(
                    SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor::textLiteralOrWhitespace
                )
                .cast()
        );
    }

    /**
     * Transforms a {@link StringParserToken} into a {@link TextLiteralSpreadsheetFormulaParserToken} or
     * {@link WhitespaceSpreadsheetFormulaParserToken}
     */
    private static SpreadsheetFormulaParserToken textLiteralOrWhitespace(final ParserToken token,
                                                                         final ParserContext context) {
        final StringParserToken stringParserToken = token.cast(StringParserToken.class);
        final String text = stringParserToken.text();
        final String value = stringParserToken.value();

        return text.equals(" ") ?
            SpreadsheetFormulaParserToken.whitespace(
                text,
                value
            ) :
            SpreadsheetFormulaParserToken.textLiteral(
                value,
                text
            );
    }

    /**
     * Milliseconds & digit zero increment a counter. Before adding other parsers this count will be used to create optional parsers
     * for the decimal and the millisecond digits.
     */
    private void appendDecimalSeparatorMillisecondsIfNecessary() {
        final int millis = this.milliseconds;
        if (millis > 0) {

            this.addParser(
                SpreadsheetNonNumberParsePatternParser.decimalSeparator()
                    .and(
                        SpreadsheetNonNumberParsePatternParser.milliseconds(
                            CharSequences.repeating(
                                '0',
                                millis - 1
                            ).toString()
                        ).optional()
                    ).optional()
            );

            this.milliseconds = 0;
        }
    }

    /**
     * Initial set to zero, 1 means decimal separator, 2+ are the number of millisecond digits -1.
     */
    private int milliseconds;

    /**
     * Adds a required parser to the sequence. This sequence wil eventually be transformed into either a
     * {@link SpreadsheetFormulaParserToken} subclass like {@link DateSpreadsheetFormulaParserToken}.
     */
    private void addParser(final Parser<SpreadsheetParserContext> parser) {
        if (null == this.parser) {
            this.parser = parser;
        } else {
            this.parser = this.parser.and(parser);
        }
    }

    /**
     * Multiple parsers for each of the tokens in the pattern.
     */
    private Parser<SpreadsheetParserContext> parser;

    private void failInvalid(final SpreadsheetFormatParserToken token) {
        throw new IllegalStateException("Invalid token " + token);
    }

    @Override
    public String toString() {
        return this.parsers.toString();
    }
}
