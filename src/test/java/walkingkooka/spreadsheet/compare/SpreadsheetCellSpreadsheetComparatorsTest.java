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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.compare.Comparators;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellSpreadsheetComparatorsTest implements ClassTesting<SpreadsheetCellSpreadsheetComparators>,
        ComparatorTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellSpreadsheetComparators>,
        ToStringTesting<SpreadsheetCellSpreadsheetComparators>,
        ParseStringTesting<List<SpreadsheetCellSpreadsheetComparators>> {

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellSpreadsheetComparators.with(
                        null,
                        Lists.of(
                                SpreadsheetComparators.fake()
                        )
                )
        );
    }

    @Test
    public void testWithNullComparatorsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("A"),
                        null
                )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("A"),
                        Lists.empty()
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnOrRowReference column = SpreadsheetSelection.parseColumn("D");
        final List<SpreadsheetComparator<?>> comparators = Lists.of(
                SpreadsheetComparators.dayOfMonth()
        );

        final SpreadsheetCellSpreadsheetComparators columnOrRowComparators = SpreadsheetCellSpreadsheetComparators.with(
                column,
                comparators
        );
        this.columnOrRowAndCheck(
                columnOrRowComparators,
                column
        );
        this.comparatorsAndCheck(
                columnOrRowComparators,
                comparators
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentColumnOrRow() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("C"),
                        COMPARATORS
                )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumnOrRow("$B"),
                        COMPARATORS
                )
        );
    }

    @Test
    public void testEqualsDifferentComparators() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparators.with(
                        COLUMN_OR_ROW,
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                        )
                )
        );
    }

    private final static SpreadsheetColumnOrRowReference COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("B");

    private final static List<SpreadsheetComparator<?>> COMPARATORS = Lists.of(
            SpreadsheetComparators.dayOfMonth()
    );

    @Override
    public SpreadsheetCellSpreadsheetComparators createObject() {
        return SpreadsheetCellSpreadsheetComparators.with(
                COLUMN_OR_ROW,
                COMPARATORS
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseMissingColumnOrRowFails() {
        final String text = "=day-of-month";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        0
                )
        );
    }

    @Test
    public void testParseMissingInvalidColumnOrRowFails() {
        final String text = "A1=day-of-month";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        1
                )
        );
    }

    @Test
    public void testParseInvalidSpreadsheetComparatorNameCharacterFails() {
        this.parseStringFails(
                "A1=!day-of-month",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseInvalidSpreadsheetComparatorNameCharacterFails2() {
        this.parseStringFails(
                "A1=day-of-month!",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseMissingEmptySpreadsheetComparatorNameFails() {
        this.parseStringFails(
                "A1=",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseUnknownSpreadsheetComparatorNameFails() {
        this.parseStringFails(
                "A1=unknown",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnSpreadsheetName() {
        this.parseStringAndCheck(
                "A=day-of-month",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetName() {
        this.parseStringAndCheck(
                "2=day-of-month",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                )
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceFails() {
        this.parseStringFails(
                "A=day-of-month ",
                new IllegalArgumentException("Missing UP/DOWN")
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceEmptyUpOrDownFails() {
        final String text = "A=day-of-month ,";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf(',')
                )
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceInvalidUpOrDownCharacterFails() {
        final String text = "A=day-of-month !,";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('!')
                )
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceInvalidUpOrDownCharacterFails2() {
        this.parseStringFails(
                "A=day-of-month U!,",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceInvalidUpOrDownFails() {
        this.parseStringFails(
                "A=day-of-month INVALID",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseSpreadsheetNameSpaceInvalidUpOrDownFails2() {
        this.parseStringFails(
                "A=day-of-month INVALID,",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseRowSpreadsheetNameCommaFails() {
        this.parseStringFails(
                "2=day-of-month,",
                new IllegalArgumentException(
                        "Missing comparator name"
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "A=day-of-month UP",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "A=day-of-month DOWN",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.reverse(
                                        SpreadsheetComparators.dayOfMonth()
                                )
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "2=day-of-month UP",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "2=day-of-month DOWN",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.reverse(
                                        SpreadsheetComparators.dayOfMonth()
                                )
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUpCommaFails() {
        this.parseStringFails(
                "2=day-of-month UP,",
                new IllegalArgumentException("Missing comparator name")
        );
    }

    @Test
    public void testParseRowSpreadsheetNameDownCommaFails() {
        this.parseStringFails(
                "2=day-of-month DOWN,",
                new IllegalArgumentException("Missing comparator name")
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameSpreadsheetName() {
        this.parseStringAndCheck(
                "2=day-of-month,month-of-year",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth(),
                                SpreadsheetComparators.monthOfYear()
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameUpSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "2=day-of-month,month-of-year",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth(),
                                SpreadsheetComparators.monthOfYear()
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUpSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "2=day-of-month UP,month-of-year DOWN",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth(),
                                SpreadsheetComparators.reverse(
                                        SpreadsheetComparators.monthOfYear()
                                )
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameRowFails() {
        final String text = "A=day-of-month;1=month-of-year";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('1')
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameColumnFails() {
        final String text = "1=day-of-month;A=month-of-year";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('A')
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameDuplicateColumnFails() {
        this.parseStringFails(
                "A=day-of-month;A=month-of-year",
                new IllegalArgumentException("Duplicate column A")
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameDuplicateColumnFails2() {
        this.parseStringFails(
                "A=day-of-month;$A=month-of-year",
                new IllegalArgumentException("Duplicate column $A")
        );
    }

    @Test
    public void testParseRowSpreadsheetNameDuplicateRowFails() {
        this.parseStringFails(
                "1=day-of-month;1=month-of-year",
                new IllegalArgumentException("Duplicate row 1")
        );
    }

    @Test
    public void testParseRowSpreadsheetNameDuplicateRowFails2() {
        this.parseStringFails(
                "1=day-of-month;$1=month-of-year",
                new IllegalArgumentException("Duplicate row $1")
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameColumnSpreadsheetName() {
        this.parseStringAndCheck(
                "A=day-of-month;B=month-of-year",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                ),
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("B"),
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameUpColumnSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "A=day-of-month UP;B=month-of-year DOWN",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                ),
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseColumn("B"),
                        Lists.of(
                                SpreadsheetComparators.reverse(
                                        SpreadsheetComparators.monthOfYear()
                                )
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUpRowSpreadsheetNameDownSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "1=day-of-month UP;2=month-of-year DOWN,year",
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("1"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                        )
                ),
                SpreadsheetCellSpreadsheetComparators.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.reverse(
                                        SpreadsheetComparators.monthOfYear()
                                ),
                                SpreadsheetComparators.year()
                        )
                )
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetCellSpreadsheetComparators... comparators) {
        this.parseStringAndCheck(
                text,
                Lists.of(
                        comparators
                )
        );
    }

    @Override
    public List<SpreadsheetCellSpreadsheetComparators> parseString(final String text) {
        return SpreadsheetCellSpreadsheetComparators.parse(
                text,
                SpreadsheetComparators.nameToSpreadsheetComparator()
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // compare..........................................................................................................

    @Test
    public void testCompareWithNulNull() {
        final SpreadsheetCell text1 = null;
        final SpreadsheetCell text2 = null;

        this.comparatorArraySortAndCheck(
                "A=string",
                text1,
                text2,
                text1,
                text2
        );
    }

    @Test
    public void testCompareWithNullString() {
        final SpreadsheetCell text1 = null;
        final SpreadsheetCell text2 = this.cell("A2", "2b");

        this.comparatorArraySortAndCheck(
                "A=string",
                text1,
                text2,
                text2, // expected null is always AFTER
                text1
        );
    }

    @Test
    public void testCompareWithStringNull() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell text2 = null;

        this.comparatorArraySortAndCheck(
                "A=string",
                text1,
                text2,
                text1, // null always AFTER
                text2
        );
    }

    @Test
    public void testCompareWithStringString() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell text2 = this.cell("A2", "2b");

        this.comparatorArraySortAndCheck(
                "A=string",
                text1,
                text2,
                text1,
                text2
        );
    }

    @Test
    public void testCompareWithStringString2() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell text2 = this.cell("A2", "2b");

        this.comparatorArraySortAndCheck(
                "A=string",
                text2,
                text1,
                text1,
                text2
        );
    }

    @Test
    public void testCompareWithStringUnConvertible() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell date2 = this.cell("A2", LocalDate.now());

        this.comparatorArraySortAndCheck(
                "A=string",
                text1,
                date2,
                text1,
                date2
        );
    }

    @Test
    public void testCompareWithStringUnConvertible2() {
        final SpreadsheetCell text1 = this.cell("A1", "1a");
        final SpreadsheetCell date2 = this.cell("A2", LocalDate.now());

        this.comparatorArraySortAndCheck(
                "A=string",
                date2,
                text1,
                text1,
                date2
        );
    }

    @Test
    public void testCompareWithDateDate() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-12-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-01");

        this.comparatorArraySortAndCheck(
                "A=date",
                date1,
                date2,
                date1,
                date2
        );
    }

    @Test
    public void testCompareWithDateDateDateDayOfMonthMonthOfYearYear() {
        final SpreadsheetCell date1 = this.cell("A1", "2000-01-01");
        final SpreadsheetCell date2 = this.cell("A2", "2022-02-02");
        final SpreadsheetCell date3 = this.cell("A3", "1999-12-31");

        this.comparatorArraySortAndCheck(
                "A=day-of-month,month-of-year,year",
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareWithDateDateDateDayOfMonthMonthOfYearYear2() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-12-31");
        final SpreadsheetCell date2 = this.cell("A2", "2022-02-02");
        final SpreadsheetCell date3 = this.cell("A3", "2000-01-01");

        this.comparatorArraySortAndCheck(
                "A=day-of-month DOWN,month-of-year DOWN,year DOWN",
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareWithDateDateDateDayOfMonthMonthOfYearYear3() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");

        this.comparatorArraySortAndCheck(
                "A=day-of-month UP,month-of-year UP,year UP",
                date3,
                date2,
                date1,
                date1,
                date2,
                date3
        );

        this.comparatorArraySortAndCheck(
                "A=day-of-month UP,month-of-year UP,year UP",
                date3,
                date1,
                date2,
                date1,
                date2,
                date3
        );
    }

    @Test
    public void testCompareWithDateDateDateStringDayOfMonthMonthOfYearYear() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");
        final SpreadsheetCell text4 = this.cell("A4", "fourth");

        this.comparatorArraySortAndCheck(
                "A=day-of-month UP,month-of-year UP,year UP",
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
    public void testCompareWithDateDateDateStringStringDayOfMonthMonthOfYearYear() {
        final SpreadsheetCell date1 = this.cell("A1", "1999-01-31");
        final SpreadsheetCell date2 = this.cell("A2", "2000-01-31");
        final SpreadsheetCell date3 = this.cell("A3", "2022-01-31");
        final SpreadsheetCell text4 = this.cell("A4", "fourth");
        final SpreadsheetCell text5 = this.cell("A5", "fifth");

        this.comparatorArraySortAndCheck(
                "A=day-of-month UP,month-of-year UP,year UP",
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

    private void comparatorArraySortAndCheck(
            final String comparators,
            final SpreadsheetCell... values) {

        final List<SpreadsheetCellSpreadsheetComparators> columnOrRowSpreadsheetComparators = SpreadsheetCellSpreadsheetComparators.parse(
                comparators,
                SpreadsheetComparators.nameToSpreadsheetComparator()
        );

        final SpreadsheetComparatorContext context = new FakeSpreadsheetComparatorContext() {

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
        };

        this.comparatorArraySortAndCheck(
                (left, right) -> {
                    int result = Comparators.EQUAL;

                    for (final SpreadsheetCellSpreadsheetComparators c : columnOrRowSpreadsheetComparators) {
                        result = c.compare(
                                left,
                                right,
                                context
                        );
                        if (Comparators.EQUAL != result) {
                            break;
                        }
                    }

                    return result;
                },
                values
        );
    }


    // Object...........................................................................................................
    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "B day-of-month UP"
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetCellSpreadsheetComparators columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReference columnOrRow) {
        this.checkEquals(
                columnOrRow,
                columnOrRowComparators.columnOrRow(),
                "columnOrRow"
        );
    }

    private void comparatorsAndCheck(final SpreadsheetCellSpreadsheetComparators columnOrRowComparators,
                                     final List<SpreadsheetComparator<?>> comparators) {
        this.checkEquals(
                comparators,
                columnOrRowComparators.comparators(),
                "comparators"
        );
    }

    @Override
    public Class<SpreadsheetCellSpreadsheetComparators> type() {
        return SpreadsheetCellSpreadsheetComparators.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
