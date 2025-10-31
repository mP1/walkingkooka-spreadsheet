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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.Range;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetRowReference> {

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.row(23).setColumn(null));
    }

    @Test
    public void testSetColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = row.setColumn(column);
        this.checkEquals(column, cell.column(), "column");
        this.checkEquals(row, cell.row(), "row");
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseRow("123"),
            "Row not found: \"123\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("123");
    }

    // columnOrRowReferenceKind.........................................................................................

    @Test
    @Override
    public void testColumnOrRowReferenceKind() {
        this.columnOrRowReferenceKindAndCheck(
            this.createSelection(),
            SpreadsheetColumnOrRowReferenceKind.ROW
        );
    }

    // count............................................................................................................

    @Test
    public void testCountA() {
        this.countAndCheck("2", 1);
    }

    @Test
    public void testCountZ() {
        this.countAndCheck("$99", 1);
    }

    // containsAll......................................................................................................

    @Test
    public void testContainsAll() {
        this.containsAllAndCheck(
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetViewportWindows.parse("A1"),
            false
        );
    }

    // IfDifferentColumnOrRowTypeFail...................................................................................

    @Test
    public void testIfDifferentColumnOrRowTypeFailWithRow() {
        this.ifDifferentColumnOrRowTypeFail(
            SpreadsheetSelection.parseRow("12"),
            SpreadsheetSelection.parseRow("34")
        );
    }

    @Test
    public void testIfDifferentColumnOrRowTypeFailWithColumnFails() {
        this.ifDifferentColumnOrRowTypeFail(
            SpreadsheetSelection.parseRow("56"),
            SpreadsheetSelection.parseColumn("AB"),
            "Got Column AB expected Row"
        );
    }

    // testTestRow......................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
            "3",
            "2",
            false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testRowAndCheck(
            "3",
            "4",
            false
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
            "3",
            "3",
            true
        );
    }

    @Test
    public void testTestRow2() {
        this.testRowAndCheck(
            "$3",
            "3",
            true
        );
    }

    // test.............................................................................................................

    @Test
    public void testTestSameRown() {
        this.testTrue(this.createSelection());
    }

    @Test
    public void testtestEqualsDifferentRow() {
        this.testFalse(
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    public void testTestWithCellDifferentRow() {
        this.testFalse(
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testTestWithCellSameRow() {
        this.testTrue(
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testTestWithColumn() {
        this.testFalse(
            SpreadsheetSelection.parseRow("3"),
            SpreadsheetSelection.parseColumn("C")
        );
    }

    // cellColumnOrRowText..............................................................................................

    @Test
    public void testCellColumnOrRow() {
        this.cellColumnOrRowTextAndCheck("row");
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellDifferentRowFalse() {
        final SpreadsheetRowReference selection = this.createSelection();

        this.testCellAndCheck(
            selection,
            selection.add(1)
                .setColumn(this.columnReference()),
            false
        );
    }

    @Test
    public void testTestCellDifferentRowKindTrue() {
        final SpreadsheetRowReference selection = this.createSelection();

        this.testCellAndCheck(
            selection,
            selection.setReferenceKind(selection.referenceKind().flip())
                .setColumn(this.columnReference()),
            true
        );
    }

    private SpreadsheetColumnReference columnReference() {
        return SpreadsheetSelection.parseColumn("A");
    }

    // range............................................................................................................

    @Test
    public void testRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        this.checkEquals(
            Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
            lower.range(upper)
        );
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "1",
            "1=text;2=text;33=text",
            "Invalid row(s) 2, 33 are not within 1"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "1",
            "A=text UP;B=text DOWN;C=text UP;D=text DOWN"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "1",
            "1=text"
        );
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAbsolute() {
        final int value = 123;
        this.toRelativeAndCheck(SpreadsheetReferenceKind.ABSOLUTE.row(value), SpreadsheetReferenceKind.RELATIVE.row(value));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck(SpreadsheetReferenceKind.RELATIVE.row(123));
    }

    @Test
    public void testEqualReferenceKindIgnored() {
        this.compareToAndCheckEquals(
            SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
            SpreadsheetReferenceKind.RELATIVE.row(VALUE));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(
            SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
            SpreadsheetReferenceKind.ABSOLUTE.row(VALUE + 999));
    }

    @Test
    public void testLess2() {
        this.compareToAndCheckLess(
            SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
            SpreadsheetReferenceKind.RELATIVE.row(VALUE + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetRowReference row1 = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference row3 = SpreadsheetSelection.parseRow("3");
        final SpreadsheetRowReference row4 = SpreadsheetSelection.parseRow("$4");

        this.compareToArraySortAndCheck(row3, row1, row4, row2,
            row1, row2, row3, row4);
    }

    // parseString.....................................................................................................

    @Test
    public void testParseEmptyFails() {
        this.parseStringFails("", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidFails() {
        this.parseStringFails("!9", IllegalArgumentException.class);
    }

    @Test
    public void testParseStarFails() {
        this.parseStringFails(
            "*",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseValueMoreThanMaxFails() {
        this.parseStringFails(
            "12345678",
            new IllegalRowArgumentException("Invalid row=12345677 not between 0 and 1048576")
        );
    }

    @Test
    public void testParseAbsolute() {
        this.parseStringAndCheck("$1", SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseStringAndCheck("$2", SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testParseRelative() {
        this.parseStringAndCheck("1", SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseStringAndCheck("2", SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // parseRowRange....................................................................................................

    @Test
    public void testParseRange() {
        this.checkEquals(
            SpreadsheetRowRangeReference.with(
                Range.greaterThanEquals(SpreadsheetSelection.parseRow("2"))
                    .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("4")))
            ),
            SpreadsheetSelection.parseRowRange("2:4"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
            SpreadsheetRowRangeReference.with(
                Range.greaterThanEquals(SpreadsheetSelection.parseRow("$2"))
                    .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("$5")))
            ),
            SpreadsheetSelection.parseRowRange("$2:$5"));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.addAndCheck(
            SpreadsheetSelection.parseRow("7"),
            2,
            SpreadsheetSelection.parseRow("9")
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseRow("7"),
            2,
            SpreadsheetSelection.parseRow("9")
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeWhenRelative() {
        final SpreadsheetRowReference reference = SpreadsheetSelection.parseRow("3");

        this.addIfRelativeAndCheck(
            reference,
            1,
            reference.add(1)
        );
    }

    // add column/row..................................................................................................

    @Test
    public void testAddNonZeroColumnFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSelection()
                .add(
                    1,
                    1
                )
        );
    }

    @Test
    public void testAddColumnAndRow() {
        this.addColumnRowAndCheck(
            SpreadsheetSelection.parseRow("3"),
            0,
            2,
            SpreadsheetSelection.parseRow("5")
        );
    }

    @Test
    public void testAddSaturatedNonZeroColumnFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSelection()
                .addSaturated(
                    1,
                    1
                )
        );
    }

    @Test
    public void testAddSaturatedColumnAndRow() {
        this.addSaturatedColumnRowAndCheck(
            SpreadsheetSelection.parseRow("3"),
            0,
            2,
            SpreadsheetSelection.parseRow("5")
        );
    }

    // replaceReferencesMapper..........................................................................................

    @Test
    public void testReplaceReferenceMapperColumnFails() {
        this.replaceReferencesMapperFails(
            SpreadsheetSelection.parseColumn("B"),
            "Expected rows(s) or cell(s) but got B"
        );
    }

    @Test
    public void testReplaceReferenceMapperColumnRangeFails() {
        this.replaceReferencesMapperFails(
            SpreadsheetSelection.parseColumnRange("C:D"),
            "Expected rows(s) or cell(s) but got C:D"
        );
    }

    @Test
    public void testReplaceReferencesMapperRowSame() {
        this.replaceReferencesMapperAndCheck(
            "1",
            SpreadsheetSelection.parseRow("1"),
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperRowSame2() {
        this.replaceReferencesMapperAndCheck(
            "23",
            SpreadsheetSelection.parseRow("$23"),
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperRow() {
        this.replaceReferencesMapperAndCheck(
            "1",
            SpreadsheetSelection.parseRow("3"),
            0,
            2
        );
    }

    @Test
    public void testReplaceReferencesMapperRow2() {
        this.replaceReferencesMapperAndCheck(
            "2",
            SpreadsheetSelection.parseRow("5"),
            0,
            3
        );
    }


    @Test
    public void testReplaceReferencesMapperRowRange() {
        this.replaceReferencesMapperAndCheck(
            "2",
            SpreadsheetSelection.parseRowRange("5:7"),
            0,
            3
        );
    }

    // max.............................................................................................................

    private final static boolean LEFT = true;
    private final static boolean RIGHT = !LEFT;

    @Test
    public void testMaxNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().max(null));
    }

    @Test
    public void testMaxLess() {
        this.maxAndCheck("5", "6", RIGHT);
    }

    @Test
    public void testMaxLess2() {
        this.maxAndCheck("$5", "6", RIGHT);
    }

    @Test
    public void testMaxLess3() {
        this.maxAndCheck("5", "$6", RIGHT);
    }

    @Test
    public void testMaxLess4() {
        this.maxAndCheck("$5", "$6", RIGHT);
    }

    @Test
    public void testMaxEqual() {
        this.maxAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMaxEqual2() {
        this.maxAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMaxEqual3() {
        this.maxAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMaxEqual4() {
        this.maxAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMaxMore() {
        this.maxAndCheck("6", "5", LEFT);
    }

    @Test
    public void testMaxMore2() {
        this.maxAndCheck("$6", "5", LEFT);
    }

    @Test
    public void testMaxMore3() {
        this.maxAndCheck("6", "$5", LEFT);
    }

    @Test
    public void testMaxMore4() {
        this.maxAndCheck("$6", "$5", LEFT);
    }

    private void maxAndCheck(final String reference,
                             final String other,
                             final boolean RIGHT) {
        this.maxAndCheck(SpreadsheetSelection.parseRow(reference),
            SpreadsheetSelection.parseRow(other),
            RIGHT);
    }

    private void maxAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
            reference.max(other),
            () -> "max of " + reference + " and " + other);
    }

    // min.............................................................................................................

    @Test
    public void testMinNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().min(null));
    }

    @Test
    public void testMinLess() {
        this.minAndCheck("5", "6", LEFT);
    }

    @Test
    public void testMinLess2() {
        this.minAndCheck("$5", "6", LEFT);
    }

    @Test
    public void testMinLess3() {
        this.minAndCheck("5", "$6", LEFT);
    }

    @Test
    public void testMinLess4() {
        this.minAndCheck("$5", "$6", LEFT);
    }

    @Test
    public void testMinEqual() {
        this.minAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMinEqual2() {
        this.minAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMinEqual3() {
        this.minAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMinEqual4() {
        this.minAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMinRight() {
        this.minAndCheck("6", "5", RIGHT);
    }

    @Test
    public void testMinRight2() {
        this.minAndCheck("$6", "5", RIGHT);
    }

    @Test
    public void testMinRight3() {
        this.minAndCheck("6", "$5", RIGHT);
    }

    @Test
    public void testMinRight4() {
        this.minAndCheck("$6", "$5", RIGHT);
    }

    private void minAndCheck(final String reference,
                             final String other,
                             final boolean left) {
        this.minAndCheck(SpreadsheetSelection.parseRow(reference),
            SpreadsheetSelection.parseRow(other),
            left);
    }

    private void minAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
            reference.min(other),
            () -> "min of " + reference + " and " + other);
    }

    // toCell...........................................................................................................

    @Test
    public void testToCellFails() {
        this.toCellFails();
    }

    // toCellRange......................................................................................................

    @Test
    public void testToCellRange() {
        this.toCellRangeAndCheck(
            "1",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "1"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
            "2",
            "A2:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "2"
        );
    }

    // toCellOrCellRange................................................................................................

    @Test
    public void testToCellOrCellRange() {
        this.toCellOrCellRangeAndCheck(
            "1",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "1"
        );
    }

    // toExpressionReference............................................................................................

    @Test
    public void testToExpressionReference() {
        this.toCellOrCellRangeAndCheck(
            "1",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "1"
        );
    }

    @Test
    public void testToExpressionReference2() {
        this.toCellOrCellRangeAndCheck(
            "2",
            "A2:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "2"
        );
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumnFails() {
        this.toColumnFails();
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRangeFails() {
        this.toColumnRangeFails();
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRangeFails() {
        this.toColumnOrColumnRangeFails();
    }

    // toRow............................................................................................................

    @Test
    public void testToRow() {
        final SpreadsheetRowReference row = this.createSelection();

        this.toRowAndCheck(
            row,
            row
        );
    }

    // toRowRange....................................................................................................

    @Test
    public void testToRowRange() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseRowRange("1")
        );
    }

    @Test
    public void testToRowRange2() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetSelection.parseRowRange("2")
        );
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRange() {
        final SpreadsheetRowReference selection = this.createSelection();
        this.toRowOrRowRangeAndCheck(
            selection,
            selection
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck(
            "2",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeLeftEdge() {
        this.testCellRangeAndCheck(
            "3",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeCenter() {
        this.testCellRangeAndCheck(
            "4",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeRightEdge() {
        this.testCellRangeAndCheck(
            "5",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck(
            "6",
            "C3:E5",
            false
        );
    }

    // testTestColumn...................................................................................................

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
            "1",
            "A",
            false
        );
    }

    // testRowRange...................................................................................................,,,

    @Test
    public void testRowRangeSpreadsheetRowRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("2");

        this.checkEquals(
            SpreadsheetRowRangeReference.with(
                Range.greaterThanEquals(lower)
                    .and(
                        Range.lessThanEquals(upper)
                    )
            ),
            lower.rowRange(upper),
            () -> lower + " rowRange " + upper
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetSelection.parseRow("12"),
            "row 12" + EOL
        );
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string("!9"));
    }

    @Test
    public void testUnmarshallStringAbsolute() {
        this.unmarshallAndCheck(JsonNode.string("$1"), SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testUnmarshallStringAbsolute2() {
        this.unmarshallAndCheck(JsonNode.string("$2"), SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testUnmarshallStringRelative() {
        this.unmarshallAndCheck(JsonNode.string("1"), SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testUnmarshallStringRelative2() {
        this.unmarshallAndCheck(JsonNode.string("2"), SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenColumnHidden() {
        this.isHiddenAndCheck(
            "1",
            Predicates.fake(),
            Predicates.is(SpreadsheetSelection.parseRow("1")),
            true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
            "1",
            Predicates.fake(),
            Predicates.never(),
            false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testMoveLeftColumn() {
        this.moveLeftColumnAndCheck(
            "1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveLeftColumnHidden() {
        this.moveLeftColumnAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            "2",
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveLeftPixels() {
        this.moveRightPixelsAndCheck(
            "3",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "3"
        );
    }

    @Test
    public void testMoveUpRow() {
        this.moveUpRowAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveUpRowFirst() {
        this.moveUpRowAndCheck(
            "1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveUpRowSkipsHidden() {
        this.moveUpRowAndCheck(
            "4",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "3",
            "2"
        );
    }

    @Test
    public void testMoveUpRowLast() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.moveUpRowAndCheck(
            row.toString(),
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row.add(-1).toString()
        );
    }

    @Test
    public void testMoveUpPixels() {
        this.moveUpPixelsAndCheck(
            "5",
            SpreadsheetViewportAnchor.TOP,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "4",
            Map.of("2", 50.0, "3", 50.0),
            "2"
        );
    }

    @Test
    public void testMoveRightColumn() {
        this.moveRightColumnAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveRightColumnHidden() {
        this.moveRightColumnAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "2",
            ""
        );
    }

    @Test
    public void testMoveRightPixels() {
        this.moveRightPixelsAndCheck(
            "3",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "3"
        );
    }

    @Test
    public void testMoveDownRow() {
        this.moveDownRowAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3"
        );
    }

    @Test
    public void testMoveDownRowFirst() {
        this.moveDownRowAndCheck(
            "1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveDownRowLast() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.moveDownRowAndCheck(
            row.toString(),
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row.toString()
        );
    }

    @Test
    public void testMoveDownRowSkipsHidden() {
        this.moveDownRowAndCheck(
            "2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "3",
            "4"
        );
    }

    @Test
    public void testDownPixels() {
        this.downPixelsAndCheck(
            "2",
            SpreadsheetViewportAnchor.TOP,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Map.of("4", 50.0, "5", 50.0),
            "5"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        this.extendRangeAndCheck(
            "2",
            "3",
            "2:3"
        );
    }

    @Test
    public void testExtendRange2() {
        this.extendRangeAndCheck(
            "3",
            "2",
            "2:3"
        );
    }

    @Test
    public void testExtendRangeFirstRow() {
        this.extendRangeAndCheck(
            "1",
            "1"
        );
    }

    @Test
    public void testExtendRangeSame() {
        final String range = "123";

        this.extendRangeAndCheck(
            range,
            range
        );
    }

    @Override
    SpreadsheetRowRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseRowRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendUpRow() {
        this.extendUpRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpFirstRow() {
        final String row = "1";

        this.extendUpRowAndCheck(
            row,
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendUpRowSkipsHidden() {
        this.extendUpRowAndCheck(
            "4",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            "3",
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "6",
            SpreadsheetViewportAnchor.ROW,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "5",
            Maps.of("4", 50.0, "3", 50.0),
            "3:6",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendDownRow() {
        this.extendDownRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendDownRowLast() {
        final String row = SpreadsheetReferenceKind.RELATIVE.lastRow()
            .toString();

        this.extendDownRowAndCheck(
            row,
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendDownRowSkipsHidden() {
        this.extendDownRowAndCheck(
            "2",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            "3",
            "2:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "2",
            SpreadsheetViewportAnchor.ROW,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Maps.of("4", 50.0, "5", 50.0),
            "2:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendLeftColumn() {
        final String row = "2";

        this.extendLeftColumnAndCheck(
            row,
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendLeftColumnHidden() {
        this.extendLeftColumnAndCheck(
            "3",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            "3",
            "",
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "2",
            SpreadsheetViewportAnchor.ROW,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "2",
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendRightColumn() {
        final String row = "2";

        this.extendRightColumnAndCheck(
            row,
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.ROW
        );
    }

    @Test
    public void testExtendRightColumnHidden() {
        this.extendRightColumnAndCheck(
            "3",
            SpreadsheetViewportAnchor.ROW,
            NO_HIDDEN_COLUMNS,
            "3",
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "2",
            SpreadsheetViewportAnchor.ROW,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "2",
            SpreadsheetViewportAnchor.ROW
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocused() {
        this.focusedAndCheck(
            "1",
            SpreadsheetViewportAnchor.NONE,
            "1"
        );
    }

    @Test
    public void testFocused2() {
        this.focusedAndCheck(
            "$2",
            SpreadsheetViewportAnchor.NONE,
            "$2"
        );
    }

    // toParserToken....................................................................................................

    @Test
    public void testToParserToken() {
        final String text = "2";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.parseRow(text),
            SpreadsheetFormulaParserToken.row(
                SpreadsheetSelection.parseRow(text),
                text
            ),
            SpreadsheetFormulaParsers.row()
        );
    }

    // equalsIgnoreReferenceKind..........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
            "1",
            "$1",
            true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "1",
            "2",
            false
        );
    }

    // toString........................................................................

    @Test
    public void testToStringRelative() {
        this.checkToString(0, SpreadsheetReferenceKind.RELATIVE, "1");
    }

    @Test
    public void testToStringRelative2() {
        this.checkToString(123, SpreadsheetReferenceKind.RELATIVE, "124");
    }

    @Test
    public void testToStringAbsolute() {
        this.checkToString(0, SpreadsheetReferenceKind.ABSOLUTE, "$1");
    }

    @Override
    SpreadsheetRowReference createReference(final int value,
                                            final SpreadsheetReferenceKind kind) {
        return SpreadsheetSelection.row(
            value,
            kind
        );
    }

    @Override
    SpreadsheetRowReference setReferenceKind(final SpreadsheetRowReference reference,
                                             final SpreadsheetReferenceKind kind) {
        return reference.setReferenceKind(kind);
    }

    @Override
    SpreadsheetRowReference setValue(final SpreadsheetRowReference reference,
                                     final int value) {
        return reference.setValue(value);
    }

    @Override
    int value(final SpreadsheetRowReference reference) {
        return reference.value();
    }

    @Override
    SpreadsheetReferenceKind referenceKind(final SpreadsheetRowReference reference) {
        return reference.referenceKind();
    }

    @Override
    int maxValue() {
        return SpreadsheetRowReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetRowReference> type() {
        return SpreadsheetRowReference.class;
    }

    @Override
    Class<? extends IllegalArgumentException> invalidValueExceptionType() {
        return IllegalRowArgumentException.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetRowReference unmarshall(final JsonNode from,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetRowReference.unmarshallRow(from, context);
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetRowReference parseString(final String text) {
        return SpreadsheetSelection.parseRow(text);
    }
}
