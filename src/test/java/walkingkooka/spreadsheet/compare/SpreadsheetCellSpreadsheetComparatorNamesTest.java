
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
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellSpreadsheetComparatorNamesTest implements ClassTesting<SpreadsheetCellSpreadsheetComparatorNames>,
        ComparatorTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellSpreadsheetComparatorNames>,
        ToStringTesting<SpreadsheetCellSpreadsheetComparatorNames>,
        ParseStringTesting<List<SpreadsheetCellSpreadsheetComparatorNames>> {

    private final static SpreadsheetColumnOrRowReference COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("A");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("string-123");

    private final static SpreadsheetComparatorDirection DIRECTION = SpreadsheetComparatorDirection.DOWN;

    private final static List<SpreadsheetComparatorNameAndDirection> NAME_AND_DIRECTIONS = Lists.of(
            NAME.setDirection(DIRECTION)
    );

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellSpreadsheetComparatorNames.with(
                        null,
                        Lists.of(
                                NAME.setDirection(DIRECTION)
                        )
                )
        );
    }

    @Test
    public void testWithNullComparatorsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellSpreadsheetComparatorNames.with(
                        COLUMN_OR_ROW,
                        null
                )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetCellSpreadsheetComparatorNames.with(
                        COLUMN_OR_ROW,
                        Lists.empty()
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnOrRowReference column = SpreadsheetSelection.parseColumn("D");
        final List<SpreadsheetComparatorNameAndDirection> namesAndDirections = Lists.of(
                NAME.setDirection(DIRECTION)
        );

        final SpreadsheetCellSpreadsheetComparatorNames columnOrRowComparators = SpreadsheetCellSpreadsheetComparatorNames.with(
                column,
                namesAndDirections
        );
        this.columnOrRowAndCheck(
                columnOrRowComparators,
                column
        );
        this.comparatorNameAndDirectionAndCheck(
                columnOrRowComparators,
                namesAndDirections
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentColumnOrRow() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumnOrRow("C"),
                        NAME_AND_DIRECTIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumnOrRow("$B"),
                        NAME_AND_DIRECTIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentNameAndDirection() {
        this.checkNotEquals(
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        COLUMN_OR_ROW,
                        Lists.of(
                                NAME.setDirection(DIRECTION.flip())
                        )
                )
        );
    }

    @Override
    public SpreadsheetCellSpreadsheetComparatorNames createObject() {
        return SpreadsheetCellSpreadsheetComparatorNames.with(
                COLUMN_OR_ROW,
                NAME_AND_DIRECTIONS
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
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetName() {
        this.parseStringAndCheck(
                "2=day-of-month",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
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
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "A=day-of-month DOWN",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "2=day-of-month UP",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "2=day-of-month DOWN",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
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
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameUpSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "2=day-of-month,month-of-year",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUpSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "2=day-of-month UP,month-of-year DOWN",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT),
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
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
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("B"),
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetNameUpColumnSpreadsheetNameDown() {
        this.parseStringAndCheck(
                "A=day-of-month UP;B=month-of-year DOWN",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("B"),
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetNameUpRowSpreadsheetNameDownSpreadsheetNameUp() {
        this.parseStringAndCheck(
                "1=day-of-month UP;2=month-of-year DOWN,year",
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("1"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                SpreadsheetCellSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN),
                                SpreadsheetComparators.year()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    private void parseStringAndCheck(final String text,
                                     final SpreadsheetCellSpreadsheetComparatorNames... comparators) {
        this.parseStringAndCheck(
                text,
                Lists.of(
                        comparators
                )
        );
    }

    @Override
    public List<SpreadsheetCellSpreadsheetComparatorNames> parseString(final String text) {
        return SpreadsheetCellSpreadsheetComparatorNames.parseList(text);
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
                "A string-123 DOWN"
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetCellSpreadsheetComparatorNames columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReference columnOrRow) {
        this.checkEquals(
                columnOrRow,
                columnOrRowComparators.columnOrRow(),
                "columnOrRow"
        );
    }

    private void comparatorNameAndDirectionAndCheck(final SpreadsheetCellSpreadsheetComparatorNames columnOrRowComparators,
                                                    final List<SpreadsheetComparatorNameAndDirection> namesAndDirections) {
        this.checkEquals(
                namesAndDirections,
                columnOrRowComparators.comparatorNameAndDirections(),
                "namesAndDirections"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellSpreadsheetComparatorNames> type() {
        return SpreadsheetCellSpreadsheetComparatorNames.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
