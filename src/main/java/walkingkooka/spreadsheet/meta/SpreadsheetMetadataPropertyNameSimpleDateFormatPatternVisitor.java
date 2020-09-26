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

package walkingkooka.spreadsheet.meta;

import walkingkooka.datetime.SimpleDateFormatPatternComponentKind;
import walkingkooka.datetime.SimpleDateFormatPatternVisitor;
import walkingkooka.text.CharSequences;

/**
 * Translates {@link java.text.DateFormat} to spreadsheet format patterns. Note its not perfect, there is some liberal
 * matching such as 23 or 24 hour time becomes 24, timezone patterns are ignored, year and yearOfEra are equivalent to year
 * and a few others.
 */
final class SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor extends SimpleDateFormatPatternVisitor {

    /**
     * Accepts a {@link java.text.SimpleDateFormat} pattern and returns its equivalent spreadsheet format pattern.
     */
    static String pattern(final String pattern) {
        final SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor visitor = new SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor();
        visitor.accept(pattern);

        // normalize spaces
        String result = visitor.pattern
                .toString()
                .trim();
        for(;;) {
            final String less = result.replace("  ", " ");
            if(less.equals(result)) {
                break;
            }
            result = less;
        }
        return result;
    }

    // @VisibleForTesting.
    SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor() {
        super();
    }

    @Override
    protected void visitEra(final int width) {
        // ignore
    }

    @Override
    protected void visitYear(final int width) {
        this.add('y', width == 2 ? 2 : 4);
    }

    @Override
    protected void visitWeekYear(int width) {
        this.add('Y', width);
    }

    @Override
    protected void visitMonthInYearContextSensitive(final int width,
                                                    final SimpleDateFormatPatternComponentKind kind) {
        this.add('m', width);
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
        this.add('d', width);
    }

    @Override
    protected void visitDayOfWeekInMonth(final int width) {
        this.add('d', width);
    }

    @Override
    protected void visitDayNameInWeek(final int width,
                                      final SimpleDateFormatPatternComponentKind kind) {
        this.add('d', width);
    }

    @Override
    protected void visitDayNumberOfWeek(final int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visitAmPmMarker(final int width) {
        this.add("AM/PM");
    }

    @Override
    protected void visitHourInDay23(final int width) {
        this.add('H', width);
    }

    @Override
    protected void visitHourInDay24(final int width) {
        this.add('H', width);
    }

    @Override
    protected void visitHourInAmPm11(final int width) {
        this.add('H', width);
    }

    @Override
    protected void visitHourInAmPm12(final int width) {
        this.add('H', width);
    }

    @Override
    protected void visitMinuteInHour(final int width) {
        this.add('m', width);
    }

    @Override
    protected void visitSecondInMinute(final int width) {
        this.add('s', width);
    }

    @Override
    protected void visitMillisecond(final int width) {
        this.add('.');
        this.add('0', width);
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
        for(final char c : text.toCharArray()) {
            switch(c) {
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
            }
        }
    }

    @Override
    protected void visitIllegal(final String component) {
        throw new UnsupportedOperationException(component);
    }

    private void add(final char c) {
        this.add(c, 1);
    }

    private void add(final char c,
                     final int repeat) {
        this.add(CharSequences.repeating(c, repeat));
    }

    private void add(final CharSequence chars) {
        this.pattern.append(chars);
    }

    private final StringBuilder pattern = new StringBuilder();

    @Override
    public String toString() {
        return this.pattern.toString();
    }
}
