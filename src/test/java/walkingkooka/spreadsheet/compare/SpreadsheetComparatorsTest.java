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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetComparatorsTest implements PublicStaticHelperTesting<SpreadsheetComparators>,
        ComparatorTesting {

    @Test
    public void testDayOfMonth() {
        this.compareAndCheckLess(
                SpreadsheetComparators.dayOfMonth(),
                LocalDate.class,
                LocalDate.of(2001, 12, 1),
                LocalDate.of(1999, 1, 31)
        );
    }

    @Test
    public void testExpressionNumber() {
        this.compareAndCheckLess(
                SpreadsheetComparators.expressionNumber(),
                ExpressionNumber.class,
                ExpressionNumberKind.BIG_DECIMAL.zero(),
                ExpressionNumberKind.BIG_DECIMAL.one()
        );
    }

    @Test
    public void testExpressionNumberDifferentExpressionNumberKind() {
        this.compareAndCheckLess(
                SpreadsheetComparators.expressionNumber(),
                ExpressionNumber.class,
                ExpressionNumberKind.BIG_DECIMAL.zero(),
                ExpressionNumberKind.DOUBLE.one()
        );
    }

    @Test
    public void testHourOfAmpm() {
        this.compareAndCheckLess(
                SpreadsheetComparators.hourOfAmPm(),
                LocalTime.class,
                LocalTime.of(13, 1, 11),
                LocalTime.of(2, 2, 22)
        );
    }

    @Test
    public void testHourOfDay() {
        this.compareAndCheckLess(
                SpreadsheetComparators.hourOfAmPm(),
                LocalTime.class,
                LocalTime.of(1, 11, 11),
                LocalTime.of(2, 2, 2)
        );
    }

    @Test
    public void testMinuteOfHour() {
        this.compareAndCheckLess(
                SpreadsheetComparators.minuteOfHour(),
                LocalTime.class,
                LocalTime.of(13, 1, 11),
                LocalTime.of(2, 2, 22)
        );
    }

    @Test
    public void testMonthOfYear() {
        this.compareAndCheckLess(
                SpreadsheetComparators.monthOfYear(),
                LocalDate.class,
                LocalDate.of(2001, 1, 31),
                LocalDate.of(1999, 12, 2)
        );
    }

    @Test
    public void testNanoOfSecond() {
        this.compareAndCheckLess(
                SpreadsheetComparators.nanoOfSecond(),
                LocalTime.class,
                LocalTime.of(1, 1, 11, 100),
                LocalTime.of(2, 2, 22, 222)
        );
    }

    @Test
    public void testNullAfter() {
        this.compareAndCheckLess(
                SpreadsheetComparators.nullAfter(
                        String.class,
                        String.CASE_INSENSITIVE_ORDER
                ),
                "abc",
                null
        );
    }

    @Test
    public void testNullBefore() {
        this.compareAndCheckMore(
                SpreadsheetComparators.nullBefore(
                        String.class,
                        String.CASE_INSENSITIVE_ORDER
                ),
                "abc",
                null
        );
    }

    @Test
    public void testSecondOfMinute() {
        this.compareAndCheckLess(
                SpreadsheetComparators.secondsOfMinute(),
                LocalTime.class,
                LocalTime.of(11, 11, 1, 100),
                LocalTime.of(2, 2, 22, 222)
        );
    }

    @Test
    public void testString() {
        this.compareAndCheckLess(
                SpreadsheetComparators.string(),
                String.class,
                "abc",
                "xyz"
        );
    }

    @Test
    public void testString2() {
        this.compareAndCheckLess(
                SpreadsheetComparators.string(),
                String.class,
                "BCD",
                "abc"
        );
    }

    @Test
    public void testStringCaseInsensitive() {
        this.compareAndCheckLess(
                SpreadsheetComparators.stringCaseInsensitive(),
                String.class,
                "abc",
                "XYZ"
        );
    }

    @Test
    public void testStringCaseInsensitive2() {
        this.compareAndCheckLess(
                SpreadsheetComparators.stringCaseInsensitive(),
                String.class,
                "abc",
                "BCD"
        );
    }

    @Test
    public void testYear() {
        this.compareAndCheckLess(
                SpreadsheetComparators.monthOfYear(),
                LocalDate.class,
                LocalDate.of(1999, 1, 1),
                LocalDate.of(2000, 12, 31)
        );
    }

    private <T> void compareAndCheckLess(final SpreadsheetComparator<T> comparator,
                                         final Class<T> type,
                                         final T left,
                                         final T right) {
        this.checkEquals(
                type,
                comparator.type(),
                () -> comparator + " type"
        );
        this.compareAndCheckLess(
                comparator,
                left,
                right
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseNullComparatorsStringFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetComparators.parse(
                        null,
                        (n) -> {
                            throw new UnsupportedOperationException();
                        }
                )
        );
    }

    @Test
    public void testParseEmptyComparatorsStringFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetComparators.parse(
                        "",
                        (n) -> {
                            throw new UnsupportedOperationException();
                        }
                )
        );
    }

    @Test
    public void testParseNullFunctionFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetComparators.parse(
                        "hello",
                        null
                )
        );
    }

    @Test
    public void testParseInvalidUpOrDownFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetComparators.parse(
                        "string XYZ",
                        SpreadsheetComparators.nameToSpreadsheetComparator()
                )
        );
        this.checkEquals(
                "Expected \"UP\" or \"DOWN\" and got \"XYZ\" in \"string XYZ\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseInvalidUpOrDownFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetComparators.parse(
                        "day-of-month,month-of-year XYZ,year",
                        SpreadsheetComparators.nameToSpreadsheetComparator()
                )
        );
        this.checkEquals(
                "Expected \"UP\" or \"DOWN\" and got \"XYZ\" in \"month-of-year XYZ\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseString() {
        this.parseAndCheck(
                "string",
                SpreadsheetComparators.string()
        );
    }

    @Test
    public void testParseStringUp() {
        this.parseAndCheck(
                "string UP",
                SpreadsheetComparators.string()
        );
    }

    @Test
    public void testParseStringDown() {
        this.parseAndCheck(
                "string DOWN",
                SpreadsheetComparators.reverse(
                        SpreadsheetComparators.string()
                )
        );
    }

    @Test
    public void testParseDayOfMonth_MonthOfYear_Year() {
        this.parseAndCheck(
                "day-of-month,month-of-year,year",
                SpreadsheetComparators.dayOfMonth(),
                SpreadsheetComparators.monthOfYear(),
                SpreadsheetComparators.year()
        );
    }

    @Test
    public void testParseDayOfMonth_Up_MonthOfYear_Down_Year_Up() {
        this.parseAndCheck(
                "day-of-month UP,month-of-year DOWN,year UP",
                SpreadsheetComparators.dayOfMonth(),
                SpreadsheetComparators.reverse(
                        SpreadsheetComparators.monthOfYear()
                ),
                SpreadsheetComparators.year()
        );
    }

    private void parseAndCheck(final String comparators,
                               final SpreadsheetComparator<?>... expected) {
        this.parseAndCheck(
                comparators,
                SpreadsheetComparators.nameToSpreadsheetComparator(),
                expected
        );
    }

    private void parseAndCheck(final String comparators,
                               final Function<String, SpreadsheetComparator<?>> nameToSpreadsheetComparator,
                               final SpreadsheetComparator<?>... expected) {
        this.parseAndCheck(
                comparators,
                nameToSpreadsheetComparator,
                Lists.of(
                        expected
                )
        );
    }

    private void parseAndCheck(final String comparators,
                               final Function<String, SpreadsheetComparator<?>> nameToSpreadsheetComparator,
                               final List<SpreadsheetComparator<?>> expected) {
        this.checkEquals(
                expected,
                SpreadsheetComparators.parse(
                        comparators,
                        nameToSpreadsheetComparator
                )
        );
    }

    // PublicStaticHelper...............................................................................................

    @Override
    public Class<SpreadsheetComparators> type() {
        return SpreadsheetComparators.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}