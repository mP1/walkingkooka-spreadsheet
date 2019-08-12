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

import walkingkooka.Value;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
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
import walkingkooka.text.CharSequences;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that calls methods on a {@link DateTimeFormatterBuilder} and fails when invalid formats are present.
 */
final class SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    private final static int DEFAULT_YEAR = 1900;
    private final static int DEFAULT_MONTH = 1;
    private final static int DEFAULT_DAY = 1;

    private final static int DEFAULT_HOUR = 0;
    private final static int DEFAULT_MINUTES = 0;

    private final static ChronoField YEAR = ChronoField.YEAR;
    private final static ChronoField MONTH = ChronoField.MONTH_OF_YEAR;
    private final static ChronoField DAY = ChronoField.DAY_OF_MONTH;

    private final static ChronoField HOUR_24 = ChronoField.HOUR_OF_DAY;
    private final static ChronoField HOUR_AMPM = ChronoField.HOUR_OF_AMPM;

    private final static ChronoField MINUTES = ChronoField.MINUTE_OF_HOUR;
    private final static ChronoField SECONDS = ChronoField.SECOND_OF_MINUTE;

    private final static ChronoField AMPM = ChronoField.AMPM_OF_DAY;

    /**
     * Creates a visitor to build a {@link DateTimeFormatter}
     */
    static DateTimeFormatter toDateTimeFormatter(final SpreadsheetFormatParserToken token,
                                                 final int twoDigitYear,
                                                 final boolean ampm) {
        try {
            final ChronoField hour = ampm ?
                    HOUR_AMPM :
                    HOUR_24;
            final SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor(twoDigitYear,
                    hour,
                    token);
            visitor.accept(token);
            return visitor.builder
                    .parseDefaulting(YEAR, DEFAULT_YEAR)
                    .parseDefaulting(MONTH, DEFAULT_MONTH)
                    .parseDefaulting(DAY, DEFAULT_DAY)
                    .parseDefaulting(hour, DEFAULT_HOUR)
                    .parseDefaulting(MINUTES, DEFAULT_MINUTES)
                    .toFormatter();
        } catch (final IllegalStateException rethrow) {
            throw rethrow;
        } catch (final RuntimeException cause) {
            throw new IllegalStateException(cause.getMessage(), cause);
        }
    }

    SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunctionSpreadsheetFormatParserTokenVisitor(final int twoDigitYear,
                                                                                                         final ChronoField hour,
                                                                                                         final SpreadsheetFormatParserToken token) {
        super();
        this.twoDigitYear = twoDigitYear;
        this.hour = hour;
        this.token = token;
        this.builder = new DateTimeFormatterBuilder();
    }

    // symbols within a date/datetime/time..............................................................................

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        switch(token.value().length()) {
            case 1:
                this.text(AMPM, TextStyle.SHORT);
                break;
            default:
                this.text(AMPM, TextStyle.FULL);
                break;
        }
        this.month = false;
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        switch(token.value().length()) {
            case 1:
                this.value(DAY, 1, 2);
                break;
            default:
                this.value(DAY, 2, 2); // leading zeros
                break;
        }

        this.month = true;
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        switch(token.value().length()) {
            case 1:
                this.value(this.hour, 1, 2);
                break;
            default:
                this.value(this.hour, 2, 2); // leading zero
                break;
        }
        this.month = false;
    }

    private final ChronoField hour;

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        if(this.month) {
            switch(token.value().length()) {
                case 1:
                    this.value(MONTH, 1, 2);
                    break;
                case 2:
                    this.value(MONTH, 2, 2); // leading zero
                    break;
                case 3:
                    this.text(MONTH, TextStyle.SHORT);
                    break;
                default:
                    this.text(MONTH, TextStyle.FULL);
                    break;
            }
        } else {
            switch(token.value().length()) {
                case 1:
                    this.value(MINUTES, 1, 2);
                    break;
                default:
                    this.value(MINUTES, 2, 2); // leading zero
                    break;
            }
        }
    }

    private boolean month = true;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        switch(token.value().length()) {
            case 1:
                this.value(SECONDS, 1, 2);
                break;
            default:
                this.value(SECONDS, 2, 2); // leading zero
                break;
        }

        this.month = false;
    }

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
        this.literal(CharSequences.repeating(' ', token.text().length()).toString());
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.literal(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        switch(token.value().length()) {
            case 1:
                this.twoDigitYear(1);
                break;
            case 2:
                this.twoDigitYear(2);
                break;
            default:
                this.value(YEAR, 4, 4);
                break;
        }
        this.month = true;
    }

    private void twoDigitYear(final int min) {
        this.builder.appendValueReduced(YEAR, min, 2, this.twoDigitYear);
    }

    private final int twoDigitYear;

    // helpers.........................................................................................................

    private void text(final ChronoField field,
                      final TextStyle textStyle) {
        this.builder.appendText(field, textStyle);
    }

    private void value(final ChronoField field,
                       final int minWidth,
                       final int maxWidth) {
        this.builder.appendValue(field, minWidth, maxWidth, SignStyle.NEVER);
    }

    private void literal(final Value<String> value) {
        this.literal(value.value()); // text may have quotes etc.
    }

    private void literal(final String text) {
        this.builder.appendLiteral(text);
    }

    private final DateTimeFormatterBuilder builder;

    private <T> T failInvalid(final SpreadsheetFormatParserToken token) {
        throw new IllegalStateException("Invalid token " + token);
    }

    @Override
    public String toString() {
        return this.token.toString();
    }

    private final SpreadsheetFormatParserToken token;
}
