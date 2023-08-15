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
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetColumnReference> {

    @Test
    public void testMin() {
        final SpreadsheetColumnReference min = SpreadsheetColumnReference.MIN;
        this.checkEquals(0, min.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, min.referenceKind(), "referenceKind");
    }

    @Test
    public void testMax() {
        final SpreadsheetColumnReference max = SpreadsheetColumnReference.MAX;
        this.checkEquals(SpreadsheetColumnReference.MAX_VALUE, max.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, max.referenceKind(), "referenceKind");
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

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("B");
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

    // test.............................................................................................................

    @Test
    public void testTestSameColumn() {
        this.testTrue(this.createSelection());
    }

    @Test
    public void testTestDifferentColumn() {
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

    // toCellRange.....................................................................................................

    @Test
    public void testToCellRangeFails() {
        this.toCellRangeWithNullFunctionFails();
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
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseColumn("B"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseColumn("D")))
                ),
                SpreadsheetSelection.parseColumnRange("B:D"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(
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
        this.checkEquals(
                SpreadsheetSelection.parseColumn("M"),
                SpreadsheetSelection.parseColumn("K").add(2)
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.checkEquals(
                SpreadsheetSelection.parseColumn("M"),
                SpreadsheetSelection.parseColumn("K").addSaturated(2)
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

    // columnRange...................................................................................................,,,

    @Test
    public void testColumnRangeSpreadsheetColumnRange() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("B");

        this.checkEquals(
                SpreadsheetColumnReferenceRange.with(
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
                SpreadsheetColumnReferenceRange.with(Range.singleton(column)),
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
    public void testLeft() {
        this.leftAndCheck(
                "B",
                "A"
        );
    }

    @Test
    public void testLeftFirstColumn() {
        this.leftAndCheck(
                "A",
                "A"
        );
    }

    @Test
    public void testLeftLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.leftAndCheck(
                column,
                column.add(-1)
        );
    }

    @Test
    public void testLeftSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.leftAndCheck(
                "D",
                store,
                "B"
        );
    }

    @Test
    public void testUpColumnHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.upAndCheck(
                "C",
                store
        );
    }

    @Test
    public void testUp() {
        this.upAndCheck(
                "B",
                "B"
        );
    }

    @Test
    public void testRight() {
        this.rightAndCheck(
                "B",
                "C"
        );
    }

    @Test
    public void testRightFirstColumn() {
        this.rightAndCheck(
                "A",
                "B"
        );
    }

    @Test
    public void testRightLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightAndCheck(
                column,
                column
        );
    }

    @Test
    public void testRightSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.rightAndCheck(
                "B",
                store,
                "D"
        );
    }
    
    @Test
    public void testDown() {
        this.downAndCheck(
                "B",
                "B"
        );
    }

    @Test
    public void testDownColumnHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.downAndCheck(
                "C",
                store
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
    SpreadsheetColumnReferenceRange parseRange(final String range) {
        return SpreadsheetSelection.parseColumnRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeft() {
        this.extendLeftAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.NONE,
                "B:C",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftFirstColumn() {
        final String column =     "A";

        this.extendLeftAndCheck(
            column,
                column
        );
    }

    @Test
    public void testExtendLeftSkipsHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("c").column().setHidden(true));

        this.extendLeftAndCheck(
                "D",
                SpreadsheetViewportSelectionAnchor.NONE,
                store,
                "B:D",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRight() {
        this.extendRightAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.NONE,
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRightAndCheck(
                column,
                column
        );
    }

    @Test
    public void testExtendRightSkipsHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("c").column().setHidden(true));

        this.extendRightAndCheck(
                "B",
                SpreadsheetViewportSelectionAnchor.NONE,
                store,
                "B:D",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendUp() {
        final String column = "B";

        this.extendUpAndCheck(
                column,
                column
        );
    }

    @Test
    public void testExtendUpHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("c").column().setHidden(true));

        this.extendUpAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.NONE,
                store
        );
    }

    @Test
    public void testExtendDown() {
        final String column = "B";

        this.extendDownAndCheck(
                column,
                column
        );
    }

    @Test
    public void testExtendDownHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("c").column().setHidden(true));

        this.extendDownAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.NONE,
                store
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocused() {
        this.focusedAndCheck(
                "A",
                SpreadsheetViewportSelectionAnchor.NONE,
                "A"
        );
    }

    @Test
    public void testFocused2() {
        this.focusedAndCheck(
                "$B",
                SpreadsheetViewportSelectionAnchor.NONE,
                "$B"
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
    SpreadsheetColumnReference createReference(final int value, final SpreadsheetReferenceKind kind) {
        return SpreadsheetColumnOrRowReference.column(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetColumnReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetColumnReference> type() {
        return SpreadsheetColumnReference.class;
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
