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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorsTest implements ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparators>,
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
        final SpreadsheetColumnOrRowReference column = SpreadsheetSelection.parseColumn("D");
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

    private final static SpreadsheetColumnOrRowReference COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("B");

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

    // Object...........................................................................................................
    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "B DayOfMonth"
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReference columnOrRow) {
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
