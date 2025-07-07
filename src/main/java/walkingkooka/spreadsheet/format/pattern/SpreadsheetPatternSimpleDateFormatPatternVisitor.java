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

import walkingkooka.NeverError;
import walkingkooka.ToStringBuilder;
import walkingkooka.datetime.SimpleDateFormatPatternComponentKind;
import walkingkooka.datetime.SimpleDateFormatPatternVisitor;
import walkingkooka.text.CharSequences;

/**
 * Translates {@link java.text.DateFormat} to spreadsheet format patterns. Note its not perfect, there is some liberal
 * matching such as 23 or 24 hour time becomes 24, timezone patterns are ignored, year and yearOfEra are equivalent to year
 * and a few others.
 */
final class SpreadsheetPatternSimpleDateFormatPatternVisitor extends SimpleDateFormatPatternVisitor {

    private static final char YEAR = 'y';
    private static final char DAY_IN_MONTH = 'd';
    private static final char HOUR = 'h';
    private static final char MONTH_OR_MINUTE = 'm';

    /**
     * Accepts a {@link java.text.SimpleDateFormat} pattern and returns its equivalent spreadsheet format pattern.
     */
    static String pattern(final String pattern,
                          final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year,
                          final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds,
                          final boolean ampm) {
        final SpreadsheetPatternSimpleDateFormatPatternVisitor visitor = new SpreadsheetPatternSimpleDateFormatPatternVisitor(
            year,
            seconds,
            ampm
        );
        visitor.accept(pattern);

        // normalize spaces
        String result = visitor.pattern
            .toString()
            .trim();
        for (; ; ) {
            final String less = result.replace("  ", " ");
            if (less.equals(result)) {
                break;
            }
            result = less;
        }

        return result.trim();
    }

    // @VisibleForTesting.
    SpreadsheetPatternSimpleDateFormatPatternVisitor(final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year,
                                                     final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds,
                                                     final boolean ampm) {
        super();
        this.year = year;
        this.seconds = seconds;
        this.ampm = ampm;
    }

    @Override
    protected void visitEra(final int width) {
        // ignore
    }

    @Override
    protected void visitYear(final int width) {
        // year can only be skipped after month

        final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year = this.year;
        switch (year) {
            case ALWAYS_2_DIGITS:
                this.add(YEAR, 2);
                break;
            case ALWAYS_4_DIGITS:
                this.add(YEAR, 4);
                break;
            case INCLUDE:
                this.add(YEAR, width == 2 ? 2 : 4);
                break;
            case EXCLUDE:
                if (this.date) {
                    // dd/mm/yyyy after day or month so remove the text literal before to become dd/mm
                    this.removeTextLiteral();
                }
                break;
            default:
                NeverError.unhandledCase(year, SpreadsheetPatternSimpleDateFormatPatternVisitorYear.values());
                break;
        }

        this.date = true;
    }

    private final SpreadsheetPatternSimpleDateFormatPatternVisitorYear year;

    @Override
    protected void visitWeekYear(int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitMonthInYearContextSensitive(final int width,
                                                    final SimpleDateFormatPatternComponentKind kind) {
        this.add(MONTH_OR_MINUTE, width);
        this.date = true;
    }

    @Override
    protected void visitMonthInYearStandaloneForm(final int width,
                                                  final SimpleDateFormatPatternComponentKind kind) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDayInYear(final int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitDayInMonth(final int width) {
        this.addDay(width);
    }

    @Override
    protected void visitDayOfWeekInMonth(final int width) {
        this.addDay(width);
    }

    @Override
    protected void visitDayNameInWeek(final int width,
                                      final SimpleDateFormatPatternComponentKind kind) {
        this.addDay(width);
    }

    private void addDay(final int width) {
        this.add(DAY_IN_MONTH, width);
        this.date = true;
    }

    @Override
    protected void visitDayNumberOfWeek(final int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitAmPmMarker(final int width) {
        if (this.ampm) {
            this.add("AM/PM");
        }
    }

    @Override
    protected void visitHourInDay23(final int width) {
        this.addHour(width);
    }

    @Override
    protected void visitHourInDay24(final int width) {
        this.addHour(width);
    }

    @Override
    protected void visitHourInAmPm11(final int width) {
        this.addHour(width);
    }

    @Override
    protected void visitHourInAmPm12(final int width) {
        this.addHour(width);
    }

    private void addHour(final int width) {
        this.add(HOUR, width);
        this.date = false;
    }

    @Override
    protected void visitMinuteInHour(final int width) {
        this.add(MONTH_OR_MINUTE, width);
        this.date = false;
    }

    @Override
    protected void visitSecondInMinute(final int width) {
        this.date = false;

        final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds = this.seconds;
        switch (seconds) {
            case EXCLUDE:
                this.removeTextLiteral();
                break;
            case INCLUDE:
                this.add('s', width);
                break;
            case INCLUDE_WITH_MILLIS:
                this.add('s', width);
                this.add(".0");
                this.millis = this.pattern.length();
                break;
            default:
                NeverError.unhandledCase(seconds, SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.values());
                break;
        }
    }

    @Override
    protected void visitMillisecond(final int width) {
        this.date = false;

        final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds = this.seconds;
        switch (seconds) {
            case EXCLUDE:
                this.removeTextLiteral();
                break;
            case INCLUDE:
            case INCLUDE_WITH_MILLIS:
                this.removeMillis();
                this.add('.');
                this.add('0', width);
                break;
            default:
                NeverError.unhandledCase(seconds, SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds.values());
                break;
        }
    }

    @Override
    protected void visitGeneralTimezone(final int width) {
        // ignore
    }

    @Override
    protected void visitRfc822Timezone(final int width) {
        // ignore
    }

    @Override
    protected void visitIso8601Timezone(final int width) {
        // ignore
    }

    /**
     * Some basic escaping, not complete, only let a few characters be added as literals and escape everything else.
     */
    @Override
    protected void visitLiteral(final String text) {
        int textLiteral = this.textLiteral;
        if (-1 == textLiteral) {
            textLiteral = this.pattern.length();
        }

        for (final char c : text.toCharArray()) {
            switch (c) {
                case ' ':
                case ',':
                case '.':
                case ':':
                case '-':
                case '/':
                    this.add(c);
                    break;
                default:
                    this.add('\\');
                    this.add(c);
                    break;
            }
        }

        // save textLiteral because add will have reset $textLiteral
        this.textLiteral = textLiteral;
    }

    @Override
    protected void visitIllegal(final String component) {
        throw new UnsupportedOperationException(component);
    }

    /**
     * A date part of a pattern. This is used to determine if when years are skipped
     * whether text literals before or after the year are skipped.
     */
    private boolean date = false;

    /**
     * When the seconds component will be included along with any preceding text literals
     */
    private final SpreadsheetPatternSimpleDateFormatPatternVisitorSeconds seconds;

    /**
     * When the am/pm component will be included along with any preceding text literals
     */
    private final boolean ampm;

    private void add(final char c) {
        this.add(c, 1);
    }

    private void add(final char c,
                     final int repeat) {
        this.add(CharSequences.repeating(c, repeat));
    }

    private void add(final CharSequence chars) {
        this.pattern.append(chars);
        this.textLiteral = -1; // real textLiteral need to set this field after the call completes
    }

    private void removeMillis() {
        this.remove(this.millis);
    }

    /**
     * This index is used to reset {@link #pattern} so millis that are included with seconds can be removed if replaced.
     */
    private int millis = -1;

    private void removeTextLiteral() {
        this.remove(this.textLiteral);
    }

    private void remove(final int index) {
        if (-1 != index) {
            this.pattern.setLength(index);
        }
    }

    /**
     * This index is used to reset {@link #pattern} so text added before a seconds or ampm is removed when the seconds or
     * ampm token is encountered and not included.
     */
    private int textLiteral = -1;

    private final StringBuilder pattern = new StringBuilder();

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("year").value(this.year)
            .label("seconds").value(this.seconds)
            .label("ampm").value(this.ampm)
            .build();
    }
}
