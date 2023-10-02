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

import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNameParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatWhitespaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that is used exclusively by {@link SpreadsheetFormatter#format(Object, SpreadsheetFormatterContext)} to
 * assemble a {@link SpreadsheetFormatter} that handles date times.
 */
final class DateTimeSpreadsheetFormatterFormatSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Visits all the individual tokens in the given token which was compiled parse the given pattern.
     */
    static SpreadsheetText format(final SpreadsheetFormatParserToken token,
                                  final LocalDateTime value,
                                  final SpreadsheetFormatterContext context,
                                  final boolean twelveHourTime,
                                  final int millisecondDecimals) {
        final DateTimeSpreadsheetFormatterFormatSpreadsheetFormatParserTokenVisitor visitor = new DateTimeSpreadsheetFormatterFormatSpreadsheetFormatParserTokenVisitor(value,
                context,
                twelveHourTime,
                millisecondDecimals);
        visitor.accept(token);

        return SpreadsheetText.with(
                visitor.text.toString()
        ).setColor(visitor.color);
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    DateTimeSpreadsheetFormatterFormatSpreadsheetFormatParserTokenVisitor(final LocalDateTime value,
                                                                          final SpreadsheetFormatterContext context,
                                                                          final boolean twelveHourTime,
                                                                          final int millisecondDecimals) {
        super();
        this.context = context;
        this.value = value;

        this.millisecondDecimals = millisecondDecimals;

        float secondRounding;
        switch (millisecondDecimals) {
            case 0:
                secondRounding = 5E-1f;
                break;
            case 1:
                secondRounding = 5E-2f;
                break;
            case 2:
                secondRounding = 5E-3f;
                break;
            case 3:
                secondRounding = 5E-4f;
                break;
            case 4:
                secondRounding = 5E-5f;
                break;
            case 5:
                secondRounding = 5E-6f;
                break;
            case 6:
                secondRounding = 5E-7f;
                break;
            case 7:
                secondRounding = 5E-8f;
                break;
            case 8:
                secondRounding = 5E-9f;
                break;
            case 9:
                secondRounding = 5E-10f;
                break;
            default:
                secondRounding = 0;
                break;
        }
        this.secondRounding = secondRounding;

        this.twelveHourTime = twelveHourTime;
    }

    private final SpreadsheetFormatterContext context;

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        final String ampm = this.context.ampm(this.value.getHour());

        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();

        final String text;
        switch (kind) {
            case AMPM_INITIAL_LOWER: // a/p
                text = ampm.substring(0, 1)
                        .toLowerCase();
                break;
            case AMPM_INITIAL_UPPER: // A/P
                text = ampm.substring(0, 1)
                        .toUpperCase();
                break;
            case AMPM_FULL_LOWER:
                text = ampm.toLowerCase();
                break;
            case AMPM_FULL_UPPER:
                text = ampm.toUpperCase();
                break;
            default:
                throw new UnsupportedOperationException("Expected ampm kind got " + kind);
        }

        this.text.append(text);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.color = this.context.colorName(
                token.colorName()
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.color = this.context.colorNumber(
                token.value()
        );
    }

    private Optional<Color> color = SpreadsheetText.WITHOUT_COLOR;

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        final LocalDateTime value = this.value;

        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();

        switch (kind) {
            case DAY_WITHOUT_LEADING_ZERO:
                this.append(
                        value.getDayOfMonth()
                );
                break;
            case DAY_WITH_LEADING_ZERO:
                this.appendWithLeadingZero(
                        value.getDayOfMonth()
                );
                break;
            case DAY_NAME_ABBREVIATION:
                this.append(
                        this.context.weekDayNameAbbreviation(
                                dayOfWeekIndex(value)
                        )
                );
                break;
            case DAY_NAME_FULL:
                this.append(
                        this.context.weekDayName(
                                dayOfWeekIndex(value)
                        )
                );
                break;
            default:
                throw new UnsupportedOperationException("Expected day kind got " + kind);
        }
    }

    // DayOfWeek 1=Monday 2=Tuesday.
    private static int dayOfWeekIndex(final LocalDateTime dateTime) {
        final int value = dateTime.getDayOfWeek().getValue();
        return 7 == value ?
                0 :
                value;
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        int hour = this.value.getHour();
        if (this.twelveHourTime) {
            hour = hour % 12;
            if (0 == hour) {
                hour = 12;
            }
        }

        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();

        switch (kind) {
            case HOUR_WITHOUT_LEADING_ZERO:
                this.append(hour);
                break;
            case HOUR_WITH_LEADING_ZERO:
                this.appendWithLeadingZero(hour);
                break;
            default:
                throw new UnsupportedOperationException("Expected hour kind got " + kind);
        }
    }

    private final boolean twelveHourTime;

    @Override
    protected void visit(final SpreadsheetFormatMinuteParserToken token) {
        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();
        final LocalDateTime value = this.value;

        switch (kind) {
            case MINUTES_WITHOUT_LEADING_ZERO:
                this.append(
                        value.getMinute()
                );
                break;
            case MINUTES_WITH_LEADING_ZERO:
                this.appendWithLeadingZero(
                        value.getMinute()
                );
                break;
            default:
                throw new UnsupportedOperationException("Expected minute kind got " + kind);
        }
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthParserToken token) {
        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();
        final LocalDateTime value = this.value;

        switch (kind) {
            case MONTH_WITHOUT_LEADING_ZERO:
                this.append(
                        value.getMonthValue()
                );
                break;
            case MONTH_WITH_LEADING_ZERO:
                this.appendWithLeadingZero(
                        value.getMonthValue()
                );
                break;
            case MONTH_NAME_ABBREVIATION:
                this.append(
                        this.context.monthNameAbbreviation(
                                value.getMonthValue() -
                                        LOCALE_DATE_TIME_MONTH_BIAS
                        )
                );
                break;
            case MONTH_NAME_FULL:
                this.append(
                        this.context.monthName(
                                value.getMonthValue() -
                                        LOCALE_DATE_TIME_MONTH_BIAS
                        )
                );
                break;
            case MONTH_NAME_INITIAL:
                this.append(
                        this.context.monthName(
                                value.getMonthValue() -
                                        LOCALE_DATE_TIME_MONTH_BIAS
                        ).substring(0, 1)
                );
                break;
            default:
                throw new UnsupportedOperationException("Expected month kind got " + kind);
        }
    }

    private final static int LOCALE_DATE_TIME_MONTH_BIAS = 1;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        final LocalDateTime value = this.value;
        final double secondsAndMills = value.getSecond() + 1.0 * value.getNano() / NANOS_IN_SECOND + this.secondRounding;
        final int seconds = (int) secondsAndMills;

        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();
        switch (kind) {
            case SECONDS_WITHOUT_LEADING_ZERO:
                this.append(seconds);
                break;
            case SECONDS_WITH_LEADING_ZERO:
                this.appendWithLeadingZero(seconds);
                break;
            default:
                throw new UnsupportedOperationException("Expected seconds kind got " + kind);
        }

        // only add decimal point followed by millis as a decimal if decimal places were present.
        final int millisecondDecimals = this.millisecondDecimals;
        if (millisecondDecimals > 0) {
            this.append(this.context.decimalSeparator());

            double millis = secondsAndMills - seconds;

            for (int i = 0; i < millisecondDecimals; i++) {
                millis = millis * 10;
                this.append(Character.forDigit(((int) millis) % 10, 10));
            }
        }
    }

    private final static int NANOS_IN_SECOND = 1_000_000_000;

    /**
     * Added to any nano value to handle rounding to the given number of places.
     */
    private final float secondRounding;

    /**
     * When non zero the millisecond component should be rounded to the given number of decimal places.
     */
    private final int millisecondDecimals;

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        final int year = this.value.getYear();

        final SpreadsheetFormatParserTokenKind kind = token.kind()
                .get();

        switch (kind) {
            case YEAR_TWO_DIGIT:
                this.appendWithLeadingZero(year % 100);
                break;
            case YEAR_FULL:
                this.append(year);
                break;
            default:
                throw new UnsupportedOperationException("Expected year kind got " + kind);
        }
    }

    private void append(final int text) {
        this.text.append(text);
    }

    private void appendWithLeadingZero(final int value) {
        if (value < 10) {
            this.append('0');
        }
        this.append(value);
    }

    private void append(final char c) {
        this.text.append(c);
    }

    private void append(final String text) {
        this.text.append(text);
    }

    /**
     * Aggregates the formatted output text.
     */
    private final StringBuilder text = new StringBuilder();

    /**
     * The date/time.
     */
    private final LocalDateTime value;

    @Override
    public String toString() {
        return this.text.toString();
    }
}
