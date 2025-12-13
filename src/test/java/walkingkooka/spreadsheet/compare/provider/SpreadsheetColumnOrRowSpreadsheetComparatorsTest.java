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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.compare.Comparators;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.FakeSpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorDirection;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.test.ParseStringTesting;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorsTest implements ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparators>,
    ComparatorTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetColumnOrRowSpreadsheetComparators>,
    ToStringTesting<SpreadsheetColumnOrRowSpreadsheetComparators>,
    ParseStringTesting<List<SpreadsheetColumnOrRowSpreadsheetComparators>> {

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumnOrRow("A"),
                null
            )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumnOrRow("A"),
                Lists.empty()
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("D");
        final List<SpreadsheetComparator<?>> comparators = Lists.of(
            SpreadsheetComparators.dayOfMonth()
        );

        final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumnOrRow("C"),
                COMPARATORS
            )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumnOrRow("$B"),
                COMPARATORS
            )
        );
    }

    @Test
    public void testEqualsDifferentComparators() {
        this.checkNotEquals(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                COLUMN_OR_ROW,
                Lists.of(
                    SpreadsheetComparators.monthOfYear()
                )
            )
        );
    }

    private final static SpreadsheetColumnOrRowReferenceOrRange COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("B");

    private final static List<SpreadsheetComparator<?>> COMPARATORS = Lists.of(
        SpreadsheetComparators.dayOfMonth()
    );

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparators createObject() {
        return SpreadsheetColumnOrRowSpreadsheetComparators.with(
            COLUMN_OR_ROW,
            COMPARATORS
        );
    }

    // toSpreadsheetColumnOrRowSpreadsheetComparatorNames......................................................................

    @Test
    public void testToSpreadsheetColumnOrRowSpreadsheetComparatorNamesWhenColumn() {
        SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        this.toSpreadsheetCellSpreadsheetComparatorNameAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                column,
                Lists.of(
                    SpreadsheetComparators.dayOfMonth(),
                    SpreadsheetComparators.monthOfYear(),
                    SpreadsheetComparators.reverse(
                        SpreadsheetComparators.year()
                    )
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                column,
                Lists.of(
                    SpreadsheetComparatorName.with("day-of-month")
                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                    SpreadsheetComparatorName.with("month-of-year")
                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                    SpreadsheetComparatorName.with("year")
                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                )
            )
        );
    }

    @Test
    public void testToSpreadsheetColumnOrRowSpreadsheetComparatorNamesWhenRow() {
        SpreadsheetRowReference row = SpreadsheetSelection.parseRow("23");

        this.toSpreadsheetCellSpreadsheetComparatorNameAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                row,
                Lists.of(
                    SpreadsheetComparators.dayOfMonth(),
                    SpreadsheetComparators.monthOfYear(),
                    SpreadsheetComparators.reverse(
                        SpreadsheetComparators.year()
                    )
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                row,
                Lists.of(
                    SpreadsheetComparatorName.with("day-of-month")
                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                    SpreadsheetComparatorName.with("month-of-year")
                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                    SpreadsheetComparatorName.with("year")
                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                )
            )
        );
    }

    private void toSpreadsheetCellSpreadsheetComparatorNameAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators comparators,
                                                                    final SpreadsheetColumnOrRowSpreadsheetComparatorNames expected) {
        this.checkEquals(
            expected,
            comparators.toSpreadsheetColumnOrRowSpreadsheetComparatorNames(),
            comparators::toString
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            new IllegalArgumentException("Got Row 1 expected Column")
        );
    }

    @Test
    public void testParseRowSpreadsheetNameColumnFails() {
        final String text = "1=day-of-month;A=month-of-year";

        this.parseStringFails(
            text,
            new IllegalArgumentException("Got Column A expected Row")
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseRow("1"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
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

    @Test
    public void testParseToString() {
        final SpreadsheetColumnOrRowSpreadsheetComparators comparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
            SpreadsheetSelection.parseRow("123"),
            Lists.of(
                SpreadsheetComparators.dayOfMonth()
            )
        );
        this.parseStringAndCheck(
            comparators.toString(),
            comparators
        );
    }

    @Test
    public void testParseToStringReversed() {
        final SpreadsheetColumnOrRowSpreadsheetComparators comparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
            SpreadsheetSelection.parseRow("123"),
            Lists.of(
                SpreadsheetComparators.dayOfMonth()
                    .reversed()
            )
        );
        this.parseStringAndCheck(
            comparators.toString(),
            comparators
        );
    }

    @Test
    public void testParseToStringReversed2() {
        final SpreadsheetColumnOrRowSpreadsheetComparators comparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
            SpreadsheetSelection.parseRow("456"),
            Lists.of(
                SpreadsheetComparators.dayOfMonth()
                    .reversed(),
                SpreadsheetComparators.monthOfYear()
                    .reversed()
            )
        );
        this.parseStringAndCheck(
            comparators.toString(),
            comparators
        );
    }

    @Test
    public void testParseToString2() {
        final SpreadsheetColumnOrRowSpreadsheetComparators comparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
            SpreadsheetSelection.parseColumn("AB"),
            Lists.of(
                SpreadsheetComparators.dayOfMonth(),
                SpreadsheetComparators.reverse(
                    SpreadsheetComparators.monthOfYear()
                ),
                SpreadsheetComparators.year()
            )
        );
        this.parseStringAndCheck(
            comparators.toString(),
            comparators
        );
    }

    @Test
    public void testParseToString3() {
        final SpreadsheetColumnOrRowSpreadsheetComparators comparators = SpreadsheetColumnOrRowSpreadsheetComparators.with(
            SpreadsheetSelection.parseRow("34"),
            Lists.of(
                SpreadsheetComparators.dayOfMonth(),
                SpreadsheetComparators.reverse(
                    SpreadsheetComparators.monthOfYear()
                ),
                SpreadsheetComparators.year()
            )
        );
        this.parseStringAndCheck(
            comparators.toString(),
            comparators
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetColumnOrRowSpreadsheetComparators... comparators) {
        this.parseStringAndCheck(
            text,
            Lists.of(
                comparators
            )
        );
    }

    @Override
    public List<SpreadsheetColumnOrRowSpreadsheetComparators> parseString(final String text) {
        return SpreadsheetColumnOrRowSpreadsheetComparators.parse(
            text,
            SpreadsheetComparatorProviders.spreadsheetComparators(),
            ProviderContexts.fake()
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
            "A=text",
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
            "A=text",
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
            "A=text",
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
            "A=text",
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
            "A=text",
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
            "A=text",
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
            "A=text",
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

        final List<SpreadsheetColumnOrRowSpreadsheetComparators> columnOrRowSpreadsheetComparators = SpreadsheetColumnOrRowSpreadsheetComparators.parse(
            comparators,
            SpreadsheetComparatorProviders.spreadsheetComparators(),
            ProviderContexts.fake()
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
                        value,
                        target
                    );
                }
                if (value instanceof String && String.class == target) {
                    return this.successfulConversion(
                        value,
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

                for (final SpreadsheetColumnOrRowSpreadsheetComparators c : columnOrRowSpreadsheetComparators) {
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
    public void testToStringColumn() {
        this.toStringAndCheck(
            this.createObject(),
            "B=day-of-month"
        );
    }

    @Test
    public void testToStringAbsoluteColumn() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumn("$C"),
                Lists.of(
                    SpreadsheetComparators.monthOfYear()
                )
            ),
            "$C=month-of-year"
        );
    }

    @Test
    public void testToStringRow() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseRow("12"),
                Lists.of(
                    SpreadsheetComparators.monthOfYear()
                )
            ),
            "12=month-of-year"
        );
    }

    @Test
    public void testToStringAbsoluteRow() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseRow("$34"),
                Lists.of(
                    SpreadsheetComparators.year()
                )
            ),
            "$34=year"
        );
    }

    @Test
    public void testToStringColumnReversedComparator() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.monthOfYear()
                        .reversed()
                )
            ),
            "A=month-of-year DOWN"
        );
    }

    @Test
    public void testToStringRowReversedComparator() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseRow("1"),
                Lists.of(
                    SpreadsheetComparators.year()
                        .reversed()
                )
            ),
            "1=year DOWN"
        );
    }

    @Test
    public void testToStringSeveralComparators() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparators.with(
                SpreadsheetSelection.parseRow("3"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth(),
                    SpreadsheetComparators.monthOfYear(),
                    SpreadsheetComparators.year()
                        .reversed()
                )
            ),
            "3=day-of-month,month-of-year,year DOWN"
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReferenceOrRange columnOrRow) {
        this.checkEquals(
            columnOrRow,
            columnOrRowComparators.columnOrRow(),
            "columnOrRow"
        );
    }

    private void comparatorsAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators,
                                     final List<SpreadsheetComparator<?>> comparators) {
        this.checkEquals(
            comparators,
            columnOrRowComparators.comparators(),
            "comparators"
        );
    }

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparators> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparators.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
