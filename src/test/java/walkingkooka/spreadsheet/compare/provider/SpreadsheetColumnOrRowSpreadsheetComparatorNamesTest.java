
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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesTest implements ClassTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    ComparatorTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    HasTextTesting,
    ToStringTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    ParseStringTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    JsonNodeMarshallingTesting<SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    TreePrintableTesting {

    private final static SpreadsheetColumnOrRowReferenceOrRange COLUMN_OR_ROW = SpreadsheetSelection.parseColumnOrRow("A");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("text-123");

    private final static List<SpreadsheetComparatorName> NAMES = Lists.of(NAME);

    @Test
    public void testWithNullColumnOrRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                null,
                Lists.of(NAME)
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
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("D");
        final List<SpreadsheetComparatorName> names = Lists.of(NAME);

        final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators = SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
            column,
            names
        );
        this.columnOrRowAndCheck(
            columnOrRowComparators,
            column
        );
        this.comparatorNamesAndCheck(
            columnOrRowComparators,
            names
        );
    }

    // setColumnOrRow...................................................................................................

    @Test
    public void testSetColumnOrRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setColumnOrRow(null)
        );
    }

    @Test
    public void testSetColumnOrRowWithSame() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = this.createObject();
        assertSame(
            names,
            names.setColumnOrRow(names.columnOrRow())
        );
    }

    @Test
    public void testSetColumnOrRowWithDifferentReferenceKind() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("A=text");

        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("$A");
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames different = names.setColumnOrRow(column);

        assertNotSame(
            names,
            different
        );

        this.columnOrRowAndCheck(
            different,
            column
        );

        this.comparatorNamesAndCheck(
            different,
            Lists.of(
                SpreadsheetComparatorName.with("text")
            )
        );
    }

    @Test
    public void testSetColumnOrRowWithDifferentColumn() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("A=text");

        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames different = names.setColumnOrRow(column);

        assertNotSame(
            names,
            different
        );

        this.columnOrRowAndCheck(
            different,
            column
        );

        this.comparatorNamesAndCheck(
            different,
            Lists.of(
                SpreadsheetComparatorName.with("text")
            )
        );
    }

    @Test
    public void testSetColumnOrRowWithDifferentRow() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("A=text");

        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames different = names.setColumnOrRow(row);

        assertNotSame(
            names,
            different
        );

        this.columnOrRowAndCheck(
            different,
            row
        );

        this.comparatorNamesAndCheck(
            different,
            Lists.of(
                SpreadsheetComparatorName.with("text")
            )
        );
    }

    // setComparatorNameAndDirections...................................................................................

    @Test
    public void testSetComparatorNameWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setComparatorNameAndDirections(null)
        );
    }

    @Test
    public void testSetComparatorNameWithSame() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = this.createObject();
        assertSame(
            names,
            names.setComparatorNameAndDirections(names.comparatorNames())
        );
    }

    @Test
    public void testSetComparatorNameWithDifferent() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnAndNames = SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("A=text");

        final List<SpreadsheetComparatorName> names = Lists.of(
            SpreadsheetComparatorName.with("text-case-insensitive")
        );
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames different = columnAndNames.setComparatorNameAndDirections(names);

        assertNotSame(
            columnAndNames,
            different
        );

        this.columnOrRowAndCheck(
            different,
            columnAndNames.columnOrRow()
        );

        this.comparatorNamesAndCheck(
            different,
            names
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators,
                                     final SpreadsheetColumnOrRowReferenceOrRange columnOrRow) {
        this.checkEquals(
            columnOrRow,
            columnOrRowComparators.columnOrRow(),
            "columnOrRow"
        );
    }

    private void comparatorNamesAndCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators,
                                         final List<SpreadsheetComparatorName> names) {
        this.checkEquals(
            names,
            columnOrRowComparators.comparatorNames(),
            "names"
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentColumnOrRow() {
        this.checkNotEquals(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumnOrRow("C"),
                NAMES
            )
        );
    }

    @Test
    public void testEqualsDifferentColumnOrRowSpreadsheetReferenceKind() {
        this.checkNotEquals(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumnOrRow("$B"),
                NAMES
            )
        );
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                COLUMN_OR_ROW,
                Lists.of(
                    SpreadsheetComparatorName.with("different")
                )
            )
        );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames createObject() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
            COLUMN_OR_ROW,
            NAMES
        );
    }

    // tryParseColumnOrRow..............................................................................................

    @Test
    public void testTryParseColumnOrRowWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowSpreadsheetComparatorNames.tryParseColumnOrRow(null)
        );
    }

    @Test
    public void testTryParseColumnOrRowWithEmpty() {
        this.tryParseColumnOrRowAndCheck("");
    }

    @Test
    public void testTryParseColumnOrRowWithInvalidColumnOrRow() {
        this.tryParseColumnOrRowAndCheck("!");
    }

    @Test
    public void testTryParseColumnOrRowWithInvalidColumnOrRow2() {
        this.tryParseColumnOrRowAndCheck("A/");
    }

    private void tryParseColumnOrRowAndCheck(final String text) {
        this.tryParseColumnOrRowAndCheck(
            text,
            Optional.empty()
        );
    }

    @Test
    public void testTryParseColumnOrRowWithOnlyColumn() {
        this.tryParseColumnOrRowAndCheck(
            "A",
            SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithOnlyColumn2() {
        this.tryParseColumnOrRowAndCheck(
            "$BC",
            SpreadsheetSelection.parseColumn("$BC")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithOnlyRow() {
        this.tryParseColumnOrRowAndCheck(
            "1",
            SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithOnlyColumnNoComparatorNames() {
        this.tryParseColumnOrRowAndCheck(
            "D=",
            SpreadsheetSelection.parseColumn("D")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithComparatorNames() {
        this.tryParseColumnOrRowAndCheck(
            "E=text-1",
            SpreadsheetSelection.parseColumn("E")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithComparatorNames2() {
        this.tryParseColumnOrRowAndCheck(
            "F=text-1,text-2",
            SpreadsheetSelection.parseColumn("F")
        );
    }

    @Test
    public void testTryParseColumnOrRowWithComparatorNames3() {
        this.tryParseColumnOrRowAndCheck(
            "G=text-1,text-2,!!!",
            SpreadsheetSelection.parseColumn("G")
        );
    }

    private void tryParseColumnOrRowAndCheck(final String text,
                                             final SpreadsheetSelection expected) {
        this.tryParseColumnOrRowAndCheck(
            text,
            Optional.of(expected)
        );
    }

    private void tryParseColumnOrRowAndCheck(final String text,
                                             final Optional<SpreadsheetSelection> expected) {
        this.checkEquals(
            expected,
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.tryParseColumnOrRow(text)
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseMissingColumnOrRowFails() {
        final String text = "! text";

        this.parseStringFails(
            text,
            new InvalidCharacterException(text, 0)
        );
    }

    @Test
    public void testParseInvalidColumnFails() {
        final String text = "A@ text";

        this.parseStringFails(
            text,
            new InvalidCharacterException(text, 1)
        );
    }

    @Test
    public void testParseInvalidColumnFails2() {
        final String text = "ABCDEFGHIJKLM text";

        this.parseStringFails(
            text,
            new IllegalArgumentException("Invalid column \"ABCDEFGHIJKLM\" not between \"A\" and \"XFD\"")
        );
    }

    @Test
    public void testParseInvalidRowFails() {
        final String text = "1@ text";

        this.parseStringFails(
            text,
            new InvalidCharacterException(text, 1)
        );
    }

    @Test
    public void testParseInvalidRowFails2() {
        final String text = "1234567890123 text";

        this.parseStringFails(
            text,
            new IllegalArgumentException("Invalid row=1912276171 not between 1 and 1048576")
        );
    }

    @Test
    public void testParseMissingColumnOrRowSeparatorFails() {
        final String text = "C";

        this.parseStringFails(
            text,
            new IllegalArgumentException("Missing '='")
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
        final String text = "D=!text";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf('!')
            )
        );
    }

    @Test
    public void testParseInvalidSeparatorComparatorNameFails2() {
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
                )
            )
        );
    }

    @Test
    public void testParseColumnSpreadsheetComparatorNameSpreadsheetComparatorName() {
        this.parseStringAndCheck(
            "A=text,text-case-insensitive,xyz",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.text()
                        .name(),
                    SpreadsheetComparators.textCaseInsensitive()
                        .name(),
                    SpreadsheetComparatorName.with("xyz")
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
        this.parseListFails(
            null,
            NullPointerException.class
        );
    }

    @Test
    public void testParseListEmptyStringFails() {
        this.parseListFails(
            "",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListMissingColumnOrRowFails() {
        final String text = "=day-of-month";

        this.parseListFails(
            text,
            new InvalidCharacterException(
                text,
                0
            )
        );
    }

    @Test
    public void testParseListColumnMissingEqualsSignFails() {
        final String text = "C";

        this.parseListFails(
            text,
            new IllegalArgumentException("Missing '='")
        );
    }

    @Test
    public void testParseListRowMissingEqualsSignFails() {
        final String text = "12";

        this.parseListFails(
            text,
            new IllegalArgumentException("Missing '='")
        );
    }

    @Test
    public void testParseListMissingInvalidColumnOrRowFails() {
        final String text = "A1=day-of-month";

        this.parseListFails(
            text,
            new InvalidCharacterException(
                text,
                1
            )
        );
    }

    @Test
    public void testParseListInvalidSpreadsheetComparatorNameCharacterFails() {
        this.parseListFails(
            "A1=!day-of-month",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListInvalidSpreadsheetComparatorNameCharacterFails2() {
        this.parseListFails(
            "A1=day-of-month!",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListMissingEmptySpreadsheetComparatorNameFails() {
        this.parseListFails(
            "A1=",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListUnknownSpreadsheetComparatorNameFails() {
        this.parseListFails(
            "A1=unknown",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "A=day-of-month",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "2=day-of-month",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseRow("2"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameSeparator() {
        this.parseListAndCheck(
            "2=day-of-month;",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseRow("2"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListSpreadsheetComparatorNameSpaceFails() {
        final String text = "A=day-of-month ";

        this.parseListFails(
            text,
            new InvalidCharacterException(
                text,
                text.length() - 1
            )
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameCommaFails() {
        this.parseListFails(
            "2=day-of-month,",
            new IllegalArgumentException(
                "Missing comparator name"
            )
        );
    }

    @Test
    public void testParseListAbsoluteColumnSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "$A=day-of-month",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("$A"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListAbsoluteRowSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "$2=day-of-month",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseRow("$2"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "2=day-of-month,month-of-year",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseRow("2"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name(),
                    SpreadsheetComparators.monthOfYear()
                        .name()
                )
            )
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameRowFails() {
        final String text = "A=day-of-month;1=month-of-year";

        this.parseListFails(
            text,
            new IllegalArgumentException("Got Row 1 expected Column")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameColumnFails() {
        final String text = "1=day-of-month;A=month-of-year";

        this.parseListFails(
            text,
            new IllegalArgumentException("Got Column A expected Row")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameDuplicateColumnFails() {
        this.parseListFails(
            "A=day-of-month;A=month-of-year",
            new IllegalArgumentException("Duplicate column A")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameDuplicateColumnFails2() {
        this.parseListFails(
            "A=day-of-month;$A=month-of-year",
            new IllegalArgumentException("Duplicate column $A")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDuplicateRowFails() {
        this.parseListFails(
            "1=day-of-month;1=month-of-year",
            new IllegalArgumentException("Duplicate row 1")
        );
    }

    @Test
    public void testParseListRowSpreadsheetComparatorNameDuplicateRowFails2() {
        this.parseListFails(
            "1=day-of-month;$1=month-of-year",
            new IllegalArgumentException("Duplicate row $1")
        );
    }

    @Test
    public void testParseListColumnSpreadsheetComparatorNameColumnSpreadsheetComparatorName() {
        this.parseListAndCheck(
            "A=day-of-month;B=month-of-year",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("A"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name()
                )
            ),
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("B"),
                Lists.of(
                    SpreadsheetComparators.monthOfYear()
                        .name()
                )
            )
        );
    }

    private void parseListAndCheck(final String text,
                                   final SpreadsheetColumnOrRowSpreadsheetComparatorNames... comparators) {
        this.parseListAndCheck(
            text,
            Lists.of(
                comparators
            )
        );
    }

    private void parseListAndCheck(final String text,
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
    private void parseListFails(final String text,
                                final Class<? extends RuntimeException> expected) {
        assertThrows(
            expected,
            () -> this.parseList(text)
        );
    }

    private void parseListFails(final String text,
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
        final String text = "A=text123";

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
                )
            ),
            "$12=text"
        );
    }

    @Test
    public void testToStringSeveralComparators() {
        this.toStringAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("AB"),
                Lists.of(
                    SpreadsheetComparators.dayOfMonth()
                        .name(),
                    SpreadsheetComparators.monthOfYear()
                        .name(),
                    SpreadsheetComparators.year()
                        .name()
                )
            ),
            "AB=day-of-month,month-of-year,year"
        );
    }

    @Test
    public void testToStringParse() {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames names = SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
            SpreadsheetSelection.parseColumn("AB"),
            Lists.of(
                SpreadsheetComparatorName.with("abc123"),
                SpreadsheetComparatorName.with("xyz456")
            )
        );

        this.checkEquals(
            names,
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse(names.toString()),
            names::toString
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testTextColumn() {
        this.parseAndTextCheck("A=text");
    }

    @Test
    public void testTextRow() {
        this.parseAndTextCheck("23=text");
    }

    @Test
    public void testTextManyComparators() {
        this.parseAndTextCheck("BCD=day-month,month-of-year,year");
    }

    private void parseAndTextCheck(final String text) {
        this.textAndCheck(
            this.parseString(text),
            text
        );
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("AB"),
                Lists.of(
                    SpreadsheetComparatorName.with("text123")
                )
            ),
            "\"AB=text123\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "\"AB=text123,abc456\"",
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
                SpreadsheetSelection.parseColumn("AB"),
                Lists.of(
                    SpreadsheetComparatorName.with("text123"),
                    SpreadsheetComparatorName.with("abc456")
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
