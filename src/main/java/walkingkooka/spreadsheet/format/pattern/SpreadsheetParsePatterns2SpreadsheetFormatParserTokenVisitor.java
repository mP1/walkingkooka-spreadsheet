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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthOrMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatStarParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextPlaceholderParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatUnderscoreParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatWhitespaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetAmPmParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetHourParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinuteParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameAbbreviationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSecondsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextLiteralParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetYearParserToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserBuilder;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;

import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that calls methods on a {@link DateTimeFormatterBuilder} and fails when invalid formats are present.
 */
final class SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    /**
     * Creates a {@link Parser} which will return a {@link walkingkooka.text.cursor.parser.SequenceParserToken}, which
     * will need to be transformed into either a {@link walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken},
     * {@link walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken} or {@link walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken}.
     */
    static Parser<SpreadsheetParserContext> toParser(final SpreadsheetFormatParserToken token) {
        final SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor(token);
        visitor.accept(token);
        visitor.appendDecimalSeparatorMillisecondsIfNecessary();

        final Parser<SpreadsheetParserContext> parser = visitor.sequenceParserBuilder.build();
        return parser.transform(SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::flat)
                .setToString(parser.toString());
    }

    private static ParserToken flat(final ParserToken token, final SpreadsheetParserContext context) {
        return token.cast(SequenceParserToken.class).flat();
    }

    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor(final SpreadsheetFormatParserToken token) {
        super();
        this.token = token;
        this.milliseconds = 0;
    }

    // symbols within a date/datetime/time..............................................................................

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.text(
                SpreadsheetParsePatterns2Parser.stringChoices(
                        SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::ampm,
                        SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::spreadsheetAmPmParserToken,
                        token.text()
                )
        );
        this.month = false;
    }

    private static List<String> ampm(final SpreadsheetParserContext context) {
        return context.ampms();
    }

    private static SpreadsheetAmPmParserToken spreadsheetAmPmParserToken(final int choice,
                                                                         final String text) {
        return SpreadsheetParserToken.amPm(choice * 12, text);
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.value(
                1,
                2,
                SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::day
        );

        this.month = true;
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetDayNumberParserToken}.
     */
    private static SpreadsheetDayNumberParserToken day(final ParserToken token,
                                                       final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::dayNumber
        );
    }

    /**
     * Records that a decimal point needs to be parsed. The actual parser will be added with or before the milliseconds parser.
     */
    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.milliseconds = 1;
    }

    // milliseconds...
    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.milliseconds++;
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.literal(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.value(
                1,
                2,
                SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::hour
        );

        this.month = false;
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetHourParserToken}.
     */
    private static SpreadsheetHourParserToken hour(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::hour
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        final int length = token.value().length();

        if (this.month) {
            switch (length) {
                case 1:
                case 2:
                    this.value(
                            1,
                            2,
                            SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::month
                    );
                    break;
                case 3:
                    this.addParser(
                            SpreadsheetParsePatterns2Parser.stringChoices(
                                    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::monthNamesAbbreviations,
                                    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::spreadsheetMonthNameAbbreviationParserToken,
                                    token.text()
                            )
                    );
                    break;
                default:
                    this.addParser(
                            SpreadsheetParsePatterns2Parser.stringChoices(
                                    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::monthNames,
                                    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::spreadsheetMonthNameParserToken,
                                    token.text()
                            )
                    );
                    break;
            }
        } else {
            this.value(
                    1,
                    2,
                    SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::minute
            );
        }
    }

    private boolean month = true;

    private static List<String> monthNames(final SpreadsheetParserContext context) {
        return context.monthNames();
    }

    private static SpreadsheetMonthNameParserToken spreadsheetMonthNameParserToken(final int value, final String text) {
        return SpreadsheetParserToken.monthName(value + 1, text); // JAN=1 but SpreadsheetParsePatterns2Parser.stringChoices 1st = 0.
    }

    private static List<String> monthNamesAbbreviations(final SpreadsheetParserContext context) {
        return context.monthNameAbbreviations();
    }

    private static SpreadsheetMonthNameAbbreviationParserToken spreadsheetMonthNameAbbreviationParserToken(final int value, final String text) {
        return SpreadsheetParserToken.monthNameAbbreviation(value + 1, text); // JAN=1 but SpreadsheetParsePatterns2Parser.stringChoices 1st = 0.
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetMinuteParserToken}.
     */
    private static SpreadsheetMinuteParserToken minute(final ParserToken token,
                                                       final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::minute
        );
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetMonthNumberParserToken}.
     */
    private static SpreadsheetMonthNumberParserToken month(final ParserToken token,
                                                           final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::monthNumber
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.value(
                1,
                2,
                SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::seconds
        );

        this.month = false;
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetSecondsParserToken}.
     */
    private static SpreadsheetSecondsParserToken seconds(final ParserToken token,
                                                         final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::seconds
        );
    }

//    final BiFunction<ParserToken, ParserContext, ParserToken> second(final )

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatStarParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.literal(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        this.literal(
                CharSequences.repeating(
                        ' ',
                        token.text().length()
                ).toString()
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
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
        this.month = true;
    }

    private void year(final int min,
                      final int max) {
        this.value(
                min,
                max,
                SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::year
        );
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link SpreadsheetYearParserToken}.
     */
    private static SpreadsheetYearParserToken year(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return token(
                token,
                SpreadsheetParserToken::year
        );
    }


    // helpers.........................................................................................................

    /**
     * Creates a {@link SpreadsheetParserToken} that has an Integer, String factory.
     */
    private static <T> T token(final ParserToken token,
                               final BiFunction<Integer, String, T> factory) {
        final StringParserToken stringParserToken = token.cast(StringParserToken.class);

        return factory.apply(
                Integer.parseInt(stringParserToken.value()),
                stringParserToken.text()
        );
    }

    private void text(final SpreadsheetParsePatterns2Parser parser) {
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
                Parsers.<SpreadsheetParserContext>stringCharPredicate(
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
                ).transform(SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor::textLiteral)
                        .cast()
        );
    }

    /**
     * Transforms a {@link StringParserToken} into a {@link SpreadsheetTextLiteralParserToken}.
     */
    private static SpreadsheetTextLiteralParserToken textLiteral(final ParserToken token,
                                                                 final ParserContext context) {
        final StringParserToken stringParserToken = token.cast(StringParserToken.class);

        return SpreadsheetParserToken.textLiteral(
                stringParserToken.value(),
                stringParserToken.text()
        );
    }

    /**
     * Milliseconds & digit zero increment a counter. Before adding other parsers this count will be used to create optional parsers
     * for the decimal and the millisecond digits.
     */
    private void appendDecimalSeparatorMillisecondsIfNecessary() {
        final int millis = this.milliseconds;
        if (millis > 0) {

            this.sequenceParserBuilder.optional(
                    Parsers.<SpreadsheetParserContext>sequenceParserBuilder()
                        .required(SpreadsheetParsePatterns2Parser.decimalSeparator())
                        .optional(SpreadsheetParsePatterns2Parser.milliseconds(CharSequences.repeating('0', millis -1).toString()))
                    .build()
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
     * {@link SpreadsheetParserToken} sub class like {@link walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken}.
     */
    private void addParser(final Parser<SpreadsheetParserContext> parser) {
        this.sequenceParserBuilder.required(parser);
    }

    /**
     * Multiple parsers for each of the components in the pattern.
     */
    private final SequenceParserBuilder<SpreadsheetParserContext> sequenceParserBuilder = Parsers.sequenceParserBuilder();

    private void failInvalid(final SpreadsheetFormatParserToken token) {
        throw new IllegalStateException("Invalid token " + token);
    }

    @Override
    public String toString() {
        return this.token.toString();
    }

    private final SpreadsheetFormatParserToken token;
}
