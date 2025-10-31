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
import walkingkooka.visit.Visiting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetColumnReference> {

    @Test
    public void testMAX_VALUE_STRING() {
        this.checkEquals(
            "XFE",
            SpreadsheetColumnReference.MAX_VALUE_STRING
        );
    }

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(null));
    }

    @Test
    public void testSetRow() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = column.setRow(row);
        this.checkEquals(column, cell.column(), "column");
        this.checkEquals(row, cell.row(), "row");
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseColumn("Z"),
            "Column not found: \"Z\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("B");
    }

    // replaceReferencesMapper..........................................................................................

    @Test
    public void testReplaceReferenceMapperRowFails() {
        this.replaceReferencesMapperFails(
            SpreadsheetSelection.parseRow("2"),
            "Expected column(s) or cell(s) but got 2"
        );
    }

    @Test
    public void testReplaceReferenceMapperRowRangeFails() {
        this.replaceReferencesMapperFails(
            SpreadsheetSelection.parseRowRange("3:4"),
            "Expected column(s) or cell(s) but got 3:4"
        );
    }

    @Test
    public void testReplaceReferencesMapperColumnSame() {
        this.replaceReferencesMapperAndCheck(
            "A",
            SpreadsheetSelection.parseColumn("A"),
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperColumnSame2() {
        this.replaceReferencesMapperAndCheck(
            "B",
            SpreadsheetSelection.parseColumn("B"),
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperColumn() {
        this.replaceReferencesMapperAndCheck(
            "A",
            SpreadsheetSelection.parseColumn("C"),
            2,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperColumn2() {
        this.replaceReferencesMapperAndCheck(
            "B",
            SpreadsheetSelection.parseColumn("E"),
            3,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperRangeColumn() {
        this.replaceReferencesMapperAndCheck(
            "B",
            SpreadsheetSelection.parseColumnRange("E:Z"),
            3,
            0
        );
    }

    // IfDifferentColumnOrRowTypeFail...................................................................................

    @Test
    public void testIfDifferentColumnOrRowTypeFailWithColumn() {
        this.ifDifferentColumnOrRowTypeFail(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testIfDifferentColumnOrRowTypeFailWithRowFails() {
        this.ifDifferentColumnOrRowTypeFail(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseRow("12"),
            "Got Row 12 expected Column"
        );
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A",
            "A=text;B=text;ZZ=text",
            "Invalid column(s) B, ZZ are not within A"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A",
            "A=text UP"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A",
            "1=text;2=text"
        );
    }

    // columnOrRowReferenceKind.........................................................................................

    @Test
    @Override
    public void testColumnOrRowReferenceKind() {
        this.columnOrRowReferenceKindAndCheck(
            this.createSelection(),
            SpreadsheetColumnOrRowReferenceKind.COLUMN
        );
    }

    // count............................................................................................................

    @Test
    public void testCountA() {
        this.countAndCheck("A", 1);
    }

    @Test
    public void testCountZ() {
        this.countAndCheck("$Z", 1);
    }

    // cellColumnOrRowText..............................................................................................

    @Test
    public void testCellColumnOrRow() {
        this.cellColumnOrRowTextAndCheck("column");
    }

    // containsAll......................................................................................................

    @Test
    public void testContainsAll() {
        this.containsAllAndCheck(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetViewportWindows.parse("A1"),
            false
        );
    }

    // test.............................................................................................................

    @Test
    public void testTestSameColumn() {
        this.testTrue(this.createSelection());
    }

    @Test
    public void testtestEqualsDifferentColumn() {
        this.testFalse(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testTestWithCellDifferentColumn() {
        this.testFalse(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testTestWithCellSameColumn() {
        this.testTrue(
            SpreadsheetSelection.parseColumn("B"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testTestWithRow() {
        this.testFalse(
            SpreadsheetSelection.parseColumn("C"),
            SpreadsheetSelection.parseRow("3")
        );
    }

    // testCell........................................................................................................

    @Test
    public void testTestCellDifferentColumnFalse() {
        final SpreadsheetColumnReference selection = this.createSelection();
        this.testCellAndCheck(
            selection,
            selection.add(1)
                .setRow(this.row()),
            false
        );
    }

    @Test
    public void testTestCellDifferentColumnKindTrue() {
        final SpreadsheetColumnReference selection = this.createSelection();
        this.testCellAndCheck(
            selection,
            selection.setReferenceKind(selection.referenceKind().flip())
                .setRow(this.row()),
            true
        );
    }

    private SpreadsheetRowReference row() {
        return SpreadsheetSelection.parseRow("1");
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck(
            "B",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeLeftEdge() {
        this.testCellRangeAndCheck(
            "C",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeCenter() {
        this.testCellRangeAndCheck(
            "D",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeRightEdge() {
        this.testCellRangeAndCheck(
            "E",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck(
            "F",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
            "A",
            "A",
            true
        );
    }

    @Test
    public void testTestColumnDifferent() {
        this.testColumnAndCheck(
            "Z",
            "A",
            false
        );
    }

    @Test
    public void testTestColumnDifferentReferenceKind() {
        this.testColumnAndCheck(
            "$B",
            "B",
            true
        );
    }

    @Test
    public void testTestColumnDifferentReferenceKind2() {
        this.testColumnAndCheck(
            "B",
            "$B",
            true
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
            "A",
            "1",
            false
        );
    }

    // range............................................................................................................

    @Test
    public void testRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        this.checkEquals(
            Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
            lower.range(upper)
        );
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRangeFails() {
        final SpreadsheetColumnReference selection = this.createSelection();
        this.toColumnOrColumnRangeAndCheck(
            selection,
            selection
        );
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRangeFails() {
        this.toRowOrRowRangeFails();
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAbsolute() {
        final int value = 123;
        this.toRelativeAndCheck(SpreadsheetReferenceKind.ABSOLUTE.column(value), SpreadsheetReferenceKind.RELATIVE.column(value));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck(SpreadsheetReferenceKind.RELATIVE.column(123));
    }

    @Test
    public void testEqualReferenceKindIgnored() {
        this.compareToAndCheckEquals(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
            SpreadsheetReferenceKind.RELATIVE.column(VALUE));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
            SpreadsheetReferenceKind.ABSOLUTE.column(VALUE + 999));
    }

    @Test
    public void testLess2() {
        this.compareToAndCheckLess(SpreadsheetReferenceKind.ABSOLUTE.column(VALUE),
            SpreadsheetReferenceKind.RELATIVE.column(VALUE + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetColumnReference column1 = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference column2 = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference column3 = SpreadsheetSelection.parseColumn("C");
        final SpreadsheetColumnReference column4 = SpreadsheetSelection.parseColumn("$D");

        this.compareToArraySortAndCheck(column3, column1, column4, column2,
            column1, column2, column3, column4);
    }

    // parseColumn.......................................................................................................

    @Test
    public void testParseColumnStarFails() {
        this.parseStringFails(
            "*",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "AB789",
            '7'
        );
    }

    @Test
    public void testParseColumnMoreThanMaxFails() {
        this.parseStringFails(
            "ABCDEFGHIJKL",
            new IllegalColumnArgumentException("Invalid column \"ABCDEFGHIJKL\" not between \"A\" and \"XFE\"")
        );
    }

    @Test
    public void testParseColumnUpperCased() {
        this.parseStringAndCheck("A", SpreadsheetColumnReference.with(0, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnUpperCased2() {
        this.parseStringAndCheck("B", SpreadsheetColumnReference.with(1, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnUpperCasedAbsolute() {
        this.parseStringAndCheck("$C", SpreadsheetColumnReference.with(2, SpreadsheetReferenceKind.ABSOLUTE));
    }

    @Test
    public void testParseColumnLowerCased() {
        this.parseStringAndCheck("d", SpreadsheetColumnReference.with(3, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnLowerCasedAbsolute() {
        this.parseStringAndCheck("$e", SpreadsheetColumnReference.with(4, SpreadsheetReferenceKind.ABSOLUTE));
    }

    @Test
    public void testParseColumnAA() {
        this.parseStringAndCheck("AA", SpreadsheetColumnReference.with(26, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnAAB() {
        this.parseStringAndCheck("AAB", SpreadsheetColumnReference.with(703, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnXFD() {
        this.parseStringAndCheck("XFD", SpreadsheetColumnReference.with(16383, SpreadsheetReferenceKind.RELATIVE));
    }

    @Test
    public void testParseColumnXFEFails() {
        this.parseStringFails("XFE", IllegalArgumentException.class);
    }

    // parseColumnRange....................................................................................................

    @Test
    public void testParseRange() {
        this.checkEquals(
            SpreadsheetColumnRangeReference.with(
                Range.greaterThanEquals(SpreadsheetSelection.parseColumn("B"))
                    .and(Range.lessThanEquals(SpreadsheetSelection.parseColumn("D")))
            ),
            SpreadsheetSelection.parseColumnRange("B:D"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
            SpreadsheetColumnRangeReference.with(
                Range.greaterThanEquals(SpreadsheetSelection.parseColumn("$B"))
                    .and(Range.lessThanEquals(SpreadsheetSelection.parseColumn("$D")))
            ),
            SpreadsheetSelection.parseColumnRange("$B:$D"));
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
    public void testParseAbsolute() {
        this.parseStringAndCheck("$A", SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseStringAndCheck("$B", SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testParseRelative() {
        this.parseStringAndCheck("A", SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseStringAndCheck("B", SpreadsheetReferenceKind.RELATIVE.column(1));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.addAndCheck(
            SpreadsheetSelection.parseColumn("K"),
            2,
            SpreadsheetSelection.parseColumn("M")
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseColumn("K"),
            2,
            SpreadsheetSelection.parseColumn("M")
        );
    }

    // add column/row..................................................................................................

    @Test
    public void testAddNonZeroRowFails() {
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
            SpreadsheetSelection.parseColumn("K"),
            2,
            0,
            SpreadsheetSelection.parseColumn("M")
        );
    }

    @Test
    public void testAddSaturatedNonZeroRowFails() {
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
            SpreadsheetSelection.parseColumn("K"),
            2,
            0,
            SpreadsheetSelection.parseColumn("M")
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeWhenRelative() {
        final SpreadsheetColumnReference reference = SpreadsheetSelection.parseColumn("C");

        this.addIfRelativeAndCheck(
            reference,
            1,
            reference.add(1)
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReference selection = this.createSelection();

        new FakeSpreadsheetSelectionVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetColumnReference s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetSelection.parseColumn("AB"),
            "column AB" + EOL
        );
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
            "A",
            "A1:A" + SpreadsheetColumnOrRowReferenceKind.ROW.lastRelative()
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
            "B",
            "B1:B" + SpreadsheetColumnOrRowReferenceKind.ROW.lastRelative()
        );
    }

    // toCellOrCellRange................................................................................................

    @Test
    public void testToCellOrCellRange() {
        this.toCellOrCellRangeAndCheck(
            "B",
            "B1:B" + SpreadsheetColumnOrRowReferenceKind.ROW.lastRelative()
        );
    }

    // toExpressionReference............................................................................................

    @Test
    public void testToExpressionReference() {
        this.toCellOrCellRangeAndCheck(
            "A",
            "A1:A" + SpreadsheetColumnOrRowReferenceKind.ROW.lastRelative()
        );
    }

    // columnRange...................................................................................................,,,

    @Test
    public void testColumnRangeSpreadsheetColumnRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("B");

        this.checkEquals(
            SpreadsheetColumnRangeReference.with(
                Range.greaterThanEquals(lower)
                    .and(
                        Range.lessThanEquals(upper)
                    )
            ),
            lower.columnRange(upper),
            () -> lower + " columnRange " + upper
        );
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumn() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("A");

        this.toColumnAndCheck(
            column,
            column
        );
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRange() {
        final SpreadsheetColumnReference column = this.createSelection();

        this.toColumnRangeAndCheck(
            SpreadsheetColumnRangeReference.with(Range.singleton(column)),
            column.toColumnRange()
        );
    }

    // toRow.........................................................................................................

    @Test
    public void testToRowFails() {
        this.toRowFails();
    }

    // toRowRange....................................................................................................

    @Test
    public void testToRowRangeFails() {
        this.toRowRangeFails();
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string("!9"));
    }

    @Test
    public void testUnmarshallStringAbsolute() {
        this.unmarshallAndCheck(JsonNode.string("$A"), SpreadsheetReferenceKind.ABSOLUTE.column(0));
    }

    @Test
    public void testUnmarshallStringAbsolute2() {
        this.unmarshallAndCheck(JsonNode.string("$B"), SpreadsheetReferenceKind.ABSOLUTE.column(1));
    }

    @Test
    public void testUnmarshallStringRelative() {
        this.unmarshallAndCheck(JsonNode.string("A"), SpreadsheetReferenceKind.RELATIVE.column(0));
    }

    @Test
    public void testUnmarshallStringRelative2() {
        this.unmarshallAndCheck(JsonNode.string("B"), SpreadsheetReferenceKind.RELATIVE.column(1));
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
        this.maxAndCheck("A", "B", RIGHT);
    }

    @Test
    public void testMaxLess2() {
        this.maxAndCheck("$A", "B", RIGHT);
    }

    @Test
    public void testMaxLess3() {
        this.maxAndCheck("A", "$B", RIGHT);
    }

    @Test
    public void testMaxLess4() {
        this.maxAndCheck("$A", "$B", RIGHT);
    }

    @Test
    public void testMaxEqual() {
        this.maxAndCheck("A", "A", LEFT);
    }

    @Test
    public void testMaxEqual2() {
        this.maxAndCheck("$A", "A", LEFT);
    }

    @Test
    public void testMaxEqual3() {
        this.maxAndCheck("A", "$A", LEFT);
    }

    @Test
    public void testMaxEqual4() {
        this.maxAndCheck("$A", "$A", LEFT);
    }

    @Test
    public void testMaxMore() {
        this.maxAndCheck("B", "A", LEFT);
    }

    @Test
    public void testMaxMore2() {
        this.maxAndCheck("$B", "A", LEFT);
    }

    @Test
    public void testMaxMore3() {
        this.maxAndCheck("B", "$A", LEFT);
    }

    @Test
    public void testMaxMore4() {
        this.maxAndCheck("$B", "$A", LEFT);
    }

    private void maxAndCheck(final String reference,
                             final String other,
                             final boolean RIGHT) {
        this.maxAndCheck(SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(other),
            RIGHT);
    }

    private void maxAndCheck(final SpreadsheetColumnReference reference,
                             final SpreadsheetColumnReference other,
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
        this.minAndCheck("A", "B", LEFT);
    }

    @Test
    public void testMinLess2() {
        this.minAndCheck("$A", "B", LEFT);
    }

    @Test
    public void testMinLess3() {
        this.minAndCheck("A", "$B", LEFT);
    }

    @Test
    public void testMinLess4() {
        this.minAndCheck("$A", "$B", LEFT);
    }

    @Test
    public void testMinEqual() {
        this.minAndCheck("A", "A", LEFT);
    }

    @Test
    public void testMinEqual2() {
        this.minAndCheck("$A", "A", LEFT);
    }

    @Test
    public void testMinEqual3() {
        this.minAndCheck("A", "$A", LEFT);
    }

    @Test
    public void testMinEqual4() {
        this.minAndCheck("$A", "$A", LEFT);
    }

    @Test
    public void testMinRight() {
        this.minAndCheck("B", "A", RIGHT);
    }

    @Test
    public void testMinRight2() {
        this.minAndCheck("$B", "A", RIGHT);
    }

    @Test
    public void testMinRight3() {
        this.minAndCheck("B", "$A", RIGHT);
    }

    @Test
    public void testMinRight4() {
        this.minAndCheck("$B", "$A", RIGHT);
    }

    private void minAndCheck(final String reference,
                             final String other,
                             final boolean left) {
        this.minAndCheck(SpreadsheetSelection.parseColumn(reference),
            SpreadsheetSelection.parseColumn(other),
            left);
    }

    private void minAndCheck(final SpreadsheetColumnReference reference,
                             final SpreadsheetColumnReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
            reference.min(other),
            () -> "min of " + reference + " and " + other);
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenColumnHidden() {
        this.isHiddenAndCheck(
            "A",
            Predicates.is(SpreadsheetSelection.parseColumn("A")),
            Predicates.fake(),
            true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
            "A",
            Predicates.never(),
            Predicates.fake(),
            false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testMoveLeftColumn() {
        this.moveLeftColumnAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A"
        );
    }

    @Test
    public void testMoveLeftColumnFirst() {
        this.moveLeftColumnAndCheck(
            "A",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A"
        );
    }

    @Test
    public void testMoveLeftColumnLast() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.moveLeftColumnAndCheck(
            column,
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS_PREDICATE,
            NO_HIDDEN_ROWS_PREDICATE,
            Optional.of(
                column.add(-1)
            )
        );
    }

    @Test
    public void testMoveLeftColumnSkipsHidden() {
        this.moveLeftColumnAndCheck(
            "D",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testMoveLeftPixels() {
        this.moveLeftPixelsAndCheck(
            "E",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "C",
            Maps.of("A", 5.0, "B", 50.0, "D", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B"
        );
    }

    @Test
    public void testMoveUpRowHidden() {
        this.moveUpRowAndCheck(
            "C",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            ""
        );
    }

    @Test
    public void testMoveUpRow() {
        this.moveUpRowAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testMoveUpPixels() {
        this.moveUpPixelsAndCheck(
            "D",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "D"
        );
    }

    @Test
    public void testRightColumn() {
        this.rightColumnAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C"
        );
    }

    @Test
    public void testRightColumnFirst() {
        this.rightColumnAndCheck(
            "A",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testRightColumnLast() {
        final String column = SpreadsheetReferenceKind.RELATIVE.lastColumn()
            .toString();

        this.rightColumnAndCheck(
            column,
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column
        );
    }

    @Test
    public void testRightColumnSkipsHidden() {
        this.rightColumnAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            "D"
        );
    }

    @Test
    public void testRightPixels() {
        this.rightPixelsAndCheck(
            "E",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "F",
            Maps.of("X", 5.0, "E", 5.0, "G", 50.0, "H", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "H"
        );
    }

    @Test
    public void testDownRow() {
        this.downRowAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testDownRowHidden() {
        this.downRowAndCheck(
            "C",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            ""
        );
    }

    @Test
    public void testDownPixels() {
        this.downPixelsAndCheck(
            "B",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        this.extendRangeAndCheck(
            "B",
            "C",
            "B:C"
        );
    }

    @Test
    public void testExtendRange2() {
        this.extendRangeAndCheck(
            "C",
            "B",
            "B:C"
        );
    }

    @Test
    public void testExtendRangeFirstColumn() {
        this.extendRangeAndCheck(
            "A",
            "A"
        );
    }

    @Override
    SpreadsheetColumnRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseColumnRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeftColumn() {
        this.extendLeftColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.COLUMN,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnFirst() {
        final String column = "A";

        this.extendLeftColumnAndCheck(
            column,
            SpreadsheetViewportAnchor.COLUMN,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column,
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    @Test
    public void testExtendLeftColumnSkipsHidden() {
        this.extendLeftColumnAndCheck(
            "D",
            SpreadsheetViewportAnchor.COLUMN,
            "C",
            NO_HIDDEN_ROWS,
            "B:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "E",
            SpreadsheetViewportAnchor.RIGHT,
            50,
            "D",
            Maps.of("C", 50.0, "B", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:E",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRightColumn() {
        this.extendRightColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightColumnLast() {
        final String column = SpreadsheetReferenceKind.RELATIVE.lastColumn()
            .toString();

        this.extendRightColumnAndCheck(
            column,
            SpreadsheetViewportAnchor.COLUMN,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column,
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    @Test
    public void testExtendRightColumnSkipsHidden() {
        this.extendRightColumnAndCheck(
            "B",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            "B:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "B",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "C",
            Maps.of("D", 50.0, "E", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendUpRow() {
        final String column = "B";

        this.extendUpRowAndCheck(
            column,
            SpreadsheetViewportAnchor.COLUMN,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column,
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    @Test
    public void testExtendUpRowColumnHidden() {
        this.extendUpRowAndCheck(
            "C",
            SpreadsheetViewportAnchor.COLUMN,
            "C",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "B",
            SpreadsheetViewportAnchor.COLUMN,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B",
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    @Test
    public void testExtendDownRow() {
        final String column = "B";

        this.extendDownRowAndCheck(
            column,
            SpreadsheetViewportAnchor.COLUMN,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column,
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    @Test
    public void testExtendDownRowHiddenColumn() {
        this.extendDownRowAndCheck(
            "C",
            SpreadsheetViewportAnchor.COLUMN,
            "C",
            NO_HIDDEN_ROWS,
            "",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "B",
            SpreadsheetViewportAnchor.COLUMN,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B",
            SpreadsheetViewportAnchor.COLUMN
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocused() {
        this.focusedAndCheck(
            "A",
            SpreadsheetViewportAnchor.NONE,
            "A"
        );
    }

    @Test
    public void testFocused2() {
        this.focusedAndCheck(
            "$B",
            SpreadsheetViewportAnchor.NONE,
            "$B"
        );
    }

    // toParserToken....................................................................................................

    @Test
    public void testToParserToken() {
        final String text = "B";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.parseColumn(text),
            SpreadsheetFormulaParserToken.column(
                SpreadsheetSelection.parseColumn(text),
                text
            ),
            SpreadsheetFormulaParsers.column()
        );
    }

    // equalsIgnoreReferenceKind..........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A",
            "$A",
            true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A",
            "B",
            false
        );
    }

    // toString.....................................................................................................

    @Test
    public void testToStringRelative() {
        this.checkToString(0, SpreadsheetReferenceKind.RELATIVE, "A");
    }

    @Test
    public void testToStringRelative2() {
        this.checkToString(25, SpreadsheetReferenceKind.RELATIVE, "Z");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative3() {
        this.checkToString((1 * 26) + 0, SpreadsheetReferenceKind.RELATIVE, "AA");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative4() {
        this.checkToString((1 * 26) + 3, SpreadsheetReferenceKind.RELATIVE, "AD");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testToStringRelative5() {
        this.checkToString((1 * 26 * 26) + (25 * 26) + 12, SpreadsheetReferenceKind.RELATIVE, "AYM");
    }

    @Test
    public void testToStringAbsolute() {
        this.checkToString(0, SpreadsheetReferenceKind.ABSOLUTE, "$A");
    }

    @Test
    public void testToStringAbsolute2() {
        this.checkToString(2, SpreadsheetReferenceKind.ABSOLUTE, "$C");
    }

    @Test
    public void testToStringMaxValue() {
        this.checkToString(SpreadsheetColumnReference.MAX_VALUE, SpreadsheetReferenceKind.RELATIVE, "XFD");
    }

    @Override
    SpreadsheetColumnReference createReference(final int value,
                                               final SpreadsheetReferenceKind kind) {
        return SpreadsheetSelection.column(
            value,
            kind
        );
    }

    @Override
    SpreadsheetColumnReference setReferenceKind(final SpreadsheetColumnReference reference,
                                                final SpreadsheetReferenceKind kind) {
        return reference.setReferenceKind(kind);
    }

    @Override
    SpreadsheetColumnReference setValue(final SpreadsheetColumnReference reference,
                                        final int value) {
        return reference.setValue(value);
    }

    @Override
    int value(final SpreadsheetColumnReference reference) {
        return reference.value();
    }

    @Override
    SpreadsheetReferenceKind referenceKind(final SpreadsheetColumnReference reference) {
        return reference.referenceKind();
    }

    @Override
    int maxValue() {
        return SpreadsheetColumnReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetColumnReference> type() {
        return SpreadsheetColumnReference.class;
    }

    @Override
    Class<? extends IllegalArgumentException> invalidValueExceptionType() {
        return IllegalColumnArgumentException.class;
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetColumnReference parseString(final String text) {
        return SpreadsheetSelection.parseColumn(text);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetColumnReference unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumnReference.unmarshallColumn(from, context);
    }
}
