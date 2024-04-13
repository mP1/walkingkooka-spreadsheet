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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellComparatorTest implements ComparatorTesting2<SpreadsheetCellComparator, SpreadsheetCell> {

    @Test
    public void testWithNullComparatorsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellComparator.with(
                        null,
                        true,
                        SpreadsheetComparatorContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellComparator.with(
                        Lists.of(
                                SpreadsheetComparators.fake()
                        ),
                        true,
                        null
                )
        );
    }

    @Test
    public void testCompareStringString() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell text2 = this.cell("A2", "2b");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        true // missingBefore
                ),
                text1,
                text2,
                text1,
                text2
        );
    }

    @Test
    public void testCompareStringString2() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell text2 = this.cell("A2", "2b");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        true // missingBefore
                ),
                text2,
                text1,
                text1,
                text2
        );
    }

    @Test
    public void testCompareStringUnConvertibleAfter() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell date2 = this.cell("A2", LocalDate.now());

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        false // missingBefore
                ),
                text1,
                date2,
                text1,
                date2
        );
    }

    @Test
    public void testCompareStringUnConvertibleAfter2() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell date2 = this.cell("A2", LocalDate.now());

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        false // missingBefore
                ),
                date2,
                text1,
                text1,
                date2
        );
    }

    @Test
    public void testCompareStringUnConvertibleBefore() {
        final SpreadsheetCell text2 = this.cell("A2", "1a");
        final SpreadsheetCell date1 = this.cell("A1", LocalDate.now());

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        true // missingBefore
                ),
                text2,
                date1,
                date1,
                text2
        );
    }

    @Test
    public void testCompareStringUnConvertibleBefore2() {
        final SpreadsheetCell text2 = this.cell("A2", "1a");
        final SpreadsheetCell date1 = this.cell("A1", LocalDate.now());

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "string",
                        true // missingBefore
                ),
                date1,
                text2,
                date1,
                text2
        );
    }

    @Test
    public void testCompareDateDate() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-12-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-01");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "date",
                        true // missingBefore
                ),
                date1,
                date2,
                date1,
                date2
        );
    }

    @Test
    public void testCompareDateDateDateDayOfMonthMonthOfYearYear() {
        final SpreadsheetCell date1 = this.cell("A1", "2000-01-01");
        final SpreadsheetCell date2 = this.cell("A2", "2022-02-02");
        final SpreadsheetCell date3 = this.cell("A3", "1999-12-31");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month,month-of-year,year",
                        true // missingBefore
                ),
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareDateDateDateDayOfMonthMonthOfYearYear2() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-12-31");
        final SpreadsheetCell date2 = this.cell("A2", "2022-02-02");
        final SpreadsheetCell date3 = this.cell("A3", "2000-01-01");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month DOWN,month-of-year DOWN,year DOWN",
                        true // missingBefore
                ),
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareDateDateDateDayOfMonthMonthOfYearYear3() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month UP,month-of-year UP,year UP",
                        true // missingBefore
                ),
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month UP,month-of-year UP,year UP",
                        true // missingBefore
                ),
                date3,
                date1,
                date2,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareDateDateDateStringDayOfMonthMonthOfYearYearBefore() {
        final SpreadsheetCell text1 = this.cell("A1", "first");
        final SpreadsheetCell date2 = this.cell("A2", "1999-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2000-01-31");
        final SpreadsheetCell date4 = this.cell("A4", "2022-01-31");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month UP,month-of-year UP,year UP",
                        true // missingBefore
                ),
                date3,
                date2,
                text1,
                date4,
                text1,
                date2,
                date3,
                date4
        );
    }

    @Test
    public void testCompareDateDateDateStringDayOfMonthMonthOfYearYearAfter() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");
        final SpreadsheetCell text4 = this.cell("A4", "fourth");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month UP,month-of-year UP,year UP",
                        false // missingBefore
                ),
                date3,
                date2,
                text4,
                date1,
                date1,
                date2,
                date3,
                text4
        );
    }

    @Test
    public void testCompareDateDateDateStringStringDayOfMonthMonthOfYearYearAfter() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");
        final SpreadsheetCell text4 = this.cell("A4", "fourth");
        final SpreadsheetCell text5 = this.cell("A5", "fifth");

        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "day-of-month UP,month-of-year UP,year UP",
                        false // missingBefore
                ),
                date3,
                date2,
                text4,
                date1,
                text5,
                date1,
                date2,
                date3,
                text4,
                text5
        );
    }

    private SpreadsheetCell cell(final String reference,
                                 final Object value) {
        return SpreadsheetSelection.parseCell(reference)
                .setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                                Optional.ofNullable(value)
                        )
                );
    }


    @Override
    public SpreadsheetCellComparator createComparator() {
        return this.createComparator(
                "day-of-month UP,month-of-year UP,year",
                true // missingBefore=true
        );
    }

    private SpreadsheetCellComparator createComparator(final String comparators,
                                                       final boolean missingBefore) {
        return SpreadsheetCellComparator.with(
                SpreadsheetComparators.parse(
                        comparators,
                        SpreadsheetComparators.nameToSpreadsheetComparator()
                ),
                missingBefore,
                new FakeSpreadsheetComparatorContext() {
                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        if (value instanceof String && LocalDate.class == target) {
                            try {
                                return this.successfulConversion(
                                        LocalDate.parse((String) value),
                                        target
                                );
                            } catch (final Exception ignore) {
                                // eventually becomes a failConversion
                            }
                        }
                        if (value instanceof LocalDate && LocalDate.class == target) {
                            return this.successfulConversion(
                                    (LocalDate) value,
                                    target
                            );
                        }
                        if (value instanceof String && String.class == target) {
                            return this.successfulConversion(
                                    (String) value,
                                    target
                            );
                        }
                        return this.failConversion(
                                value,
                                target
                        );
                    }
                }
        );
    }

    @Override
    public Class<SpreadsheetCellComparator> type() {
        return SpreadsheetCellComparator.class;
    }
}
