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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.datetime.compare.DateTimeComparators;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class SpreadsheetComparators implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetComparator}
     */
    public static <T> SpreadsheetComparator<T> basic(final Class<T> type,
                                                     final Comparator<? super T> comparator,
                                                     final SpreadsheetComparatorDirection direction,
                                                     final SpreadsheetComparatorName name) {
        return BasicSpreadsheetComparator.with(
                type,
                comparator,
                direction,
                name
        );
    }

    /**
     * {@see SpreadsheetCellsComparator}
     */
    public static Comparator<List<SpreadsheetCell>> cells(final List<SpreadsheetCellSpreadsheetComparators> spreadsheetComparators,
                                                          final SpreadsheetComparatorContext context) {

        return SpreadsheetCellsComparator.with(
                spreadsheetComparators,
                context
        );
    }

    public static SpreadsheetComparator<LocalDate> date() {
        return DATE;
    }

    private final static SpreadsheetComparator<LocalDate> DATE = basic(
            LocalDate.class,
            Comparator.naturalOrder(),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("date")
    );

    public static SpreadsheetComparator<LocalDateTime> dateTime() {
        return DATETIME;
    }

    private final static SpreadsheetComparator<LocalDateTime> DATETIME = basic(
            LocalDateTime.class,
            Comparator.naturalOrder(),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("date-time")
    );


    public static SpreadsheetComparator<LocalDate> dayOfMonth() {
        return DAY_OF_MONTH;
    }

    private final static SpreadsheetComparator<LocalDate> DAY_OF_MONTH = basic(
            LocalDate.class,
            Cast.to(
                    DateTimeComparators.dayOfMonth()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("day-of-month")
    );

    /**
     * {@see FakeSpreadsheetComparatorContext}
     */
    public static <T> SpreadsheetComparator<T> fake() {
        return new FakeSpreadsheetComparator<>();
    }

    public static SpreadsheetComparator<LocalTime> hourOfAmPm() {
        return HOUR_OF_AMPM;
    }

    private final static SpreadsheetComparator<LocalTime> HOUR_OF_AMPM = basic(
            LocalTime.class,
            Cast.to(
                    DateTimeComparators.hourOfAmPm()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("hour-of-am-pm")
    );

    public static SpreadsheetComparator<LocalTime> hourOfDay() {
        return HOUR_OF_DAY;
    }

    private final static SpreadsheetComparator<LocalTime> HOUR_OF_DAY = basic(
            LocalTime.class,
            Cast.to(
                    DateTimeComparators.hourOfDay()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("hour-of-day")
    );

    public static SpreadsheetComparator<LocalTime> minuteOfHour() {
        return MINUTE_OF_HOUR;
    }

    private final static SpreadsheetComparator<LocalTime> MINUTE_OF_HOUR = basic(
            LocalTime.class,
            Cast.to(
                    DateTimeComparators.minuteOfHour()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("minute-of-hour")
    );

    public static SpreadsheetComparator<LocalDate> monthOfYear() {
        return MONTH_OF_YEAR;
    }

    private final static SpreadsheetComparator<LocalDate> MONTH_OF_YEAR = basic(
            LocalDate.class,
            Cast.to(
                    DateTimeComparators.monthOfYear()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("month-of-year")
    );

    public static SpreadsheetComparator<LocalTime> nanoOfSecond() {
        return NANO_OF_SECOND;
    }

    private final static SpreadsheetComparator<LocalTime> NANO_OF_SECOND = basic(
            LocalTime.class,
            Cast.to(
                    DateTimeComparators.nanoOfSecond()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("nano-of-second")
    );

    /**
     * {see NameToSpreadsheetComparatorFunction}
     */
    public static Function<SpreadsheetComparatorName, SpreadsheetComparator<?>> nameToSpreadsheetComparator() {
        return NameToSpreadsheetComparatorFunction.INSTANCE;
    }

    public static <T> SpreadsheetComparator<T> nullAfter(final Class<T> type,
                                                         final Comparator<T> comparator,
                                                         final SpreadsheetComparatorDirection direction,
                                                         final SpreadsheetComparatorName name) {
        return basic(
                type,
                Comparators.nullAfter(comparator),
                direction,
                name
        );
    }

    public static <T> SpreadsheetComparator<T> nullBefore(final Class<T> type,
                                                          final Comparator<T> comparator,
                                                          final SpreadsheetComparatorDirection direction,
                                                          final SpreadsheetComparatorName name) {
        return basic(
                type,
                Comparators.nullBefore(comparator),
                direction,
                name
        );
    }

    public static SpreadsheetComparator<ExpressionNumber> number() {
        return NUMBER;
    }

    private final static SpreadsheetComparator<ExpressionNumber> NUMBER = basic(
            ExpressionNumber.class,
            Comparator.naturalOrder(),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("number")
    );

    /**
     * Accepts a string with compare names and optional UP or DOWN separated by commas.
     * <pre>
     * text DOWN, text-case-insensitive UP
     *
     * day-of-month UP, month-of-year UP, year
     * </pre>
     */
    public static List<SpreadsheetComparator<?>> parse(final String comparators,
                                                       final Function<SpreadsheetComparatorName, SpreadsheetComparator<?>> nameToSpreadsheetComparator) {
        CharSequences.failIfNullOrEmpty(comparators, "comparators");
        Objects.requireNonNull(nameToSpreadsheetComparator, "nameToSpreadsheetComparator");

        final List<SpreadsheetComparator<?>> result = Lists.array();
        int pos = 0;

        for (final String nameAndMaybeUpOrDown : comparators.split(",")) {
            String name = nameAndMaybeUpOrDown;
            final int upOrDownStartIndex = name.lastIndexOf(' ');
            SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.DEFAULT;

            if (-1 != upOrDownStartIndex) {
                String upOrDown = nameAndMaybeUpOrDown.substring(upOrDownStartIndex + 1);
                try {
                    direction = SpreadsheetComparatorDirection.valueOf(upOrDown);
                } catch (final IllegalArgumentException invalid) {
                    throw new IllegalArgumentException(
                            "Expected \"" + SpreadsheetComparatorDirection.DEFAULT + "\" or \"" + SpreadsheetComparatorDirection.DOWN + "\" and got " +
                                    CharSequences.quoteAndEscape(upOrDown) +
                                    " in " +
                                    CharSequences.quoteAndEscape(nameAndMaybeUpOrDown)
                    );
                }

                name = nameAndMaybeUpOrDown.substring(
                        0,
                        upOrDownStartIndex
                );
            }

            final SpreadsheetComparatorName spreadsheetComparatorName;

            try {
                spreadsheetComparatorName = SpreadsheetComparatorName.with(name);
            } catch (final InvalidCharacterException cause) {
                throw cause.setTextAndPosition(
                        comparators,
                        pos + cause.position()
                );
            }

            result.add(
                    direction.apply(
                            nameToSpreadsheetComparator.apply(spreadsheetComparatorName)
                    )
            );

            pos = pos + nameAndMaybeUpOrDown.length();
        }

        return result;
    }

    /**
     * {@see ReverseSpreadsheetComparator}
     */
    public static <T> SpreadsheetComparator<T> reverse(final SpreadsheetComparator<T> comparator) {
        return ReverseSpreadsheetComparator.with(comparator);
    }

    public static SpreadsheetComparator<LocalTime> secondsOfMinute() {
        return SECONDS_OF_MINUTE;
    }

    private final static SpreadsheetComparator<LocalTime> SECONDS_OF_MINUTE = basic(
            LocalTime.class,
            Cast.to(
                    DateTimeComparators.secondOfMinute()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("seconds-of-minute")
    );

    public static SpreadsheetComparator<String> text() {
        return TEXT;
    }

    private final static SpreadsheetComparator<String> TEXT = basic(
            String.class,
            Comparator.naturalOrder(),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("text")
    );

    public static SpreadsheetComparator<String> textCaseInsensitive() {
        return TEXT_CASE_INSENSITIVE;
    }

    private final static SpreadsheetComparator<String> TEXT_CASE_INSENSITIVE = basic(
            String.class,
            String.CASE_INSENSITIVE_ORDER,
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("text-case-insensitive")
    );

    public static SpreadsheetComparator<LocalTime> time() {
        return TIME;
    }

    private final static SpreadsheetComparator<LocalTime> TIME = basic(
            LocalTime.class,
            Comparator.naturalOrder(),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("time")
    );

    public static SpreadsheetComparator<LocalDate> year() {
        return YEAR;
    }

    private final static SpreadsheetComparator<LocalDate> YEAR = basic(
            LocalDate.class,
            Cast.to(
                    DateTimeComparators.year()
            ),
            SpreadsheetComparatorDirection.DEFAULT,
            SpreadsheetComparatorName.with("year")
    );

    /**
     * Stop creation
     */
    private SpreadsheetComparators() {
        throw new UnsupportedOperationException();
    }
}
