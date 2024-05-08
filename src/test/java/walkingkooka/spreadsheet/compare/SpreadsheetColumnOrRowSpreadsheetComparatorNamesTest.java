
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
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesTest implements ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        ComparatorTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        ToStringTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        ParseStringTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        JsonNodeMarshallingTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
        TreePrintableTesting {

    private final static SpreadsheetColumnOrRowReference COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("A");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("text-123");

    private final static SpreadsheetComparatorDirection DIRECTION = SpreadsheetComparatorDirection.DOWN;

    private final static List<SpreadsheetComparatorNameAndDirection> NAME_AND_DIRECTIONS = Lists.of(
            NAME.setDirection(DIRECTION)
    );

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        COLUMN_OR_ROW,
                        null
                )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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

        final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators = SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumnOrRow("C"),
                        NAME_AND_DIRECTIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumnOrRow("$B"),
                        NAME_AND_DIRECTIONS
                )
        );
    }

    @Test
    public void testEqualsDifferentNameAndDirection() {
        this.checkNotEquals(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        COLUMN_OR_ROW,
                        Lists.of(
                                NAME.setDirection(DIRECTION.flip())
                        )
                )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames createObject() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                COLUMN_OR_ROW,
                NAME_AND_DIRECTIONS
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseMissingColumnOrRowFails() {
        final String text = "! text";

        this.parseStringFails(
                text,
                new IllegalArgumentException("Expected column/row")
        );
    }

    @Test
    public void testParseMissingColumnOrRowSeparatorFails() {
        final String text = "C";

        this.parseStringFails(
                text,
                new IllegalArgumentException("Expected column/row")
        );
    }

    @Test
    public void testParseMissingSpreadsheetComparatorNameFails() {
        final String text = "C=";

        this.parseStringFails(
                text,
                new IllegalArgumentException("Missing comparator name")
        );
    }

    @Test
    public void testParseInvalidSeparatorComparatorNameFails() {
        final String text = "D=text!";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('!')
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetComparatorName() {
        this.parseStringAndCheck(
                "A=text123",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparatorName.with("text123")
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetComparatorName() {
        this.parseStringAndCheck(
                "23=text456",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("23"),
                        Lists.of(
                                SpreadsheetComparatorName.with("text456")
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetComparatorNameUp() {
        this.parseStringAndCheck(
                "A=text UP",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                )
        );
    }

    @Test
    public void testParseRowSpreadsheetComparatorNameDown() {
        this.parseStringAndCheck(
                "23=text DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("23"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                        )
                )
        );
    }

    @Test
    public void testParseColumnSpreadsheetComparatorNameUpSpreadsheetComparatorNameDown() {
        this.parseStringAndCheck(
                "A=text UP,text-case-insensitive DOWN,xyz",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP),
                                SpreadsheetComparators.textCaseInsensitive()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN),
                                SpreadsheetComparatorName.with("xyz")
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames parseString(final String text) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // parseList........................................................................................................

    @Test
    public void testParseListNullFails() {
        this.parseStringListFails(
                null,
                NullPointerException.class
        );
    }

    @Test
    public void testParseListEmptyStringFails() {
        this.parseStringListFails(
                "",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListMissingColumnOrRowFails() {
        final String text = "=day-of-month";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        0
                )
        );
    }

    @Test
    public void testParseListMissingInvalidColumnOrRowFails() {
        final String text = "A1=day-of-month";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        1
                )
        );
    }

    @Test
    public void testParseListInvalidSpreadsheetComparatorNameCharacterFails() {
        this.parseStringListFails(
                "A1=!day-of-month",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListInvalidSpreadsheetComparatorNameCharacterFails2() {
        this.parseStringListFails(
                "A1=day-of-month!",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListMissingEmptySpreadsheetComparatorNameFails() {
        this.parseStringListFails(
                "A1=",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListUnknownSpreadsheetComparatorNameFails() {
        this.parseStringListFails(
                "A1=unknown",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorName() {
        this.parseStringListAndCheck(
                "A=day-of-month",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorName() {
        this.parseStringListAndCheck(
                "2=day-of-month",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListSpreadsheetComparatorNameSpaceFails() {
        this.parseStringListFails(
                "A=day-of-month ",
                new IllegalArgumentException("Missing UP/DOWN")
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceEmptyUpOrDownFails() {
        final String text = "A=day-of-month ,";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf(',')
                )
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceInvalidUpOrDownCharacterFails() {
        final String text = "A=day-of-month !,";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('!')
                )
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceInvalidUpOrDownCharacterFails2() {
        this.parseStringListFails(
                "A=day-of-month U!,",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceInvalidUpOrDownFails() {
        this.parseStringListFails(
                "A=day-of-month INVALID",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceInvalidUpOrDownFails2() {
        this.parseStringListFails(
                "A=day-of-month INVALID,",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameCommaFails() {
        this.parseStringListFails(
                "2=day-of-month,",
                new IllegalArgumentException(
                        "Missing comparator name"
                )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "A=day-of-month UP",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListAbsoluteColumnSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "$A=day-of-month UP",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("$A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameDown() {
        this.parseStringListAndCheck(
                "A=day-of-month DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "2=day-of-month UP",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListAbsoluteRowSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "$2=day-of-month UP",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("$2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                )
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDown() {
        this.parseStringListAndCheck(
                "2=day-of-month DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorNameUpCommaFails() {
        this.parseStringListFails(
                "2=day-of-month UP,",
                new IllegalArgumentException("Missing comparator name")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDownCommaFails() {
        this.parseStringListFails(
                "2=day-of-month DOWN,",
                new IllegalArgumentException("Missing comparator name")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameSpreadsheetComparatorName() {
        this.parseStringListAndCheck(
                "2=day-of-month,month-of-year",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListColumnSpreadsheetComparatorNameUpSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "2=day-of-month,month-of-year",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorNameDefaultSpreadsheetComparatorNameDown() {
        this.parseStringListAndCheck(
                "2=day-of-month,month-of-year DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorNameUpSpreadsheetComparatorNameDown() {
        this.parseStringListAndCheck(
                "2=day-of-month UP,month-of-year DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("2"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP),
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                        )
                )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameRowFails() {
        final String text = "A=day-of-month;1=month-of-year";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('1')
                )
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameColumnFails() {
        final String text = "1=day-of-month;A=month-of-year";

        this.parseStringListFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf('A')
                )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameDuplicateColumnFails() {
        this.parseStringListFails(
                "A=day-of-month;A=month-of-year",
                new IllegalArgumentException("Duplicate column A")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameDuplicateColumnFails2() {
        this.parseStringListFails(
                "A=day-of-month;$A=month-of-year",
                new IllegalArgumentException("Duplicate column $A")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDuplicateRowFails() {
        this.parseStringListFails(
                "1=day-of-month;1=month-of-year",
                new IllegalArgumentException("Duplicate row 1")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDuplicateRowFails2() {
        this.parseStringListFails(
                "1=day-of-month;$1=month-of-year",
                new IllegalArgumentException("Duplicate row $1")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameColumnSpreadsheetComparatorName() {
        this.parseStringListAndCheck(
                "A=day-of-month;B=month-of-year",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListColumnSpreadsheetComparatorNameUpColumnSpreadsheetComparatorNameDown() {
        this.parseStringListAndCheck(
                "A=day-of-month UP;B=month-of-year DOWN",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("A"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
    public void testParseListRowSpreadsheetComparatorNameUpRowSpreadsheetComparatorNameDownSpreadsheetComparatorNameUp() {
        this.parseStringListAndCheck(
                "1=day-of-month UP;2=month-of-year DOWN,year",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("1"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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

    private void parseStringListAndCheck(final String text,
                                         final SpreadsheetColumnOrRowSpreadsheetComparatorNames... comparators) {
        this.parseStringListAndCheck(
                text,
                Lists.of(
                        comparators
                )
        );
    }

    private void parseStringListAndCheck(final String text,
                                         final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators) {
        this.checkEquals(
                comparators,
                parseList(text),
                () -> "parseList " + CharSequences.quoteAndEscape(text)
        );
    }

    private List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> parseList(final String text) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList(text);
    }

    /**
     * Parsers the given text and verifies the expected {@link RuntimeException} was thrown
     */
    private void parseStringListFails(final String text,
                                      final Class<? extends RuntimeException> expected) {
        assertThrows(
                expected,
                () -> this.parseList(text)
        );
    }

    private void parseStringListFails(final String text,
                                      final RuntimeException expected) {
        final RuntimeException thrown = assertThrows(
                expected.getClass(),
                () -> this.parseList(text)
        );

        this.checkEquals(
                expected.getMessage(),
                thrown.getMessage(),
                () -> "Incorrect failure message for " + CharSequences.quoteAndEscape(text)
        );
    }

    // SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.............................................................

    @Test
    public void testList() {
        final String text = "A=text123 DOWN";

        this.checkEquals(
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse(text),
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse(text).list()
        );
    }

    // Object...........................................................................................................

    @Test
    public void testToStringColumn() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("AB"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                "AB=day-of-month"
        );
    }

    @Test
    public void testToStringAbsoluteColumn() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("$AB"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                "$AB=day-of-month"
        );
    }

    @Test
    public void testToStringRow() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("12"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                "12=text"
        );
    }

    @Test
    public void testToStringAbsoluteRow() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseRow("$12"),
                        Lists.of(
                                SpreadsheetComparators.text()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DEFAULT)
                        )
                ),
                "$12=text"
        );
    }

    @Test
    public void testToStringUp() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("BC"),
                        Lists.of(
                                SpreadsheetComparators.time()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                "BC=time UP"
        );
    }

    @Test
    public void testToStringDown() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("ABC"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN)
                        )
                ),
                "ABC=day-of-month DOWN"
        );
    }

    @Test
    public void testToStringDown2() {
        this.toStringAndCheck(
                this.createObject(),
                "A=text-123 DOWN"
        );
    }

    @Test
    public void testToStringSeveralComparators() {
        this.toStringAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("AB"),
                        Lists.of(
                                SpreadsheetComparators.dayOfMonth()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP),
                                SpreadsheetComparators.monthOfYear()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.DOWN),
                                SpreadsheetComparators.year()
                                        .name()
                                        .setDirection(SpreadsheetComparatorDirection.UP)
                        )
                ),
                "AB=day-of-month UP,month-of-year DOWN,year UP"
        );
    }

    @Test
    public void testToStringParse() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("AB"),
                Lists.of(
                        SpreadsheetComparatorName.with("abc123")
                                .setDirection(SpreadsheetComparatorDirection.UP),
                        SpreadsheetComparatorName.with("xyz456")
                                .setDirection(SpreadsheetComparatorDirection.DOWN)
                )
        );

        this.checkEquals(
                names,
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse(names.toString()),
                () -> names.toString()
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReference columnOrRow) {
        this.checkEquals(
                columnOrRow,
                columnOrRowComparators.columnOrRow(),
                "columnOrRow"
        );
    }

    private void comparatorNameAndDirectionAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators,
                                                    final List<SpreadsheetComparatorNameAndDirection> namesAndDirections) {
        this.checkEquals(
                namesAndDirections,
                columnOrRowComparators.comparatorNameAndDirections(),
                "namesAndDirections"
        );
    }

    // Json...........................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("AB"),
                        Lists.of(
                                SpreadsheetComparatorNameAndDirection.parse("text123 DOWN")
                        )
                ),
                "\"AB=text123 DOWN\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "\"AB=text123 DOWN,abc456 UP\"",
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                        SpreadsheetSelection.parseColumn("AB"),
                        Lists.of(
                                SpreadsheetComparatorNameAndDirection.parse("text123 DOWN"),
                                SpreadsheetComparatorNameAndDirection.parse("abc456 UP")
                        )
                )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames unmarshall(final JsonNode json,
                                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowSpreadsheetComparatorNames> type() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
