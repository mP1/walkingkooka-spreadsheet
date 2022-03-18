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
import walkingkooka.collect.RangeBound;
import walkingkooka.collect.iterable.IterableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceRangeTest extends SpreadsheetColumnOrRowReferenceRangeTestCase<SpreadsheetRowReferenceRange, SpreadsheetRowReference>
        implements IterableTesting<SpreadsheetRowReferenceRange, SpreadsheetRowReference> {

    @Test
    public void testWith() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        final SpreadsheetRowReferenceRange selection = SpreadsheetRowReferenceRange.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    // parse............................................................................................................

    @Test
    public void testParseSingleColumn() {
        this.parseStringAndCheck(
                "1",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("1")
                        )
                )
        );
    }

    @Test
    public void testParseSingleColumn2() {
        this.parseStringAndCheck(
                "23",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("23")
                        )
                )
        );
    }

    @Test
    public void testParseSame() {
        this.parseStringAndCheck(
                "1:1",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("1")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent() {
        this.parseStringAndCheck(
                "1:1",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("1")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent2() {
        this.parseStringAndCheck(
                "1:$1",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("1")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent3() {
        this.parseStringAndCheck(
                "$23:23",
                SpreadsheetRowReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseRow("$23")
                        )
                )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
                "1:23",
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseRow("1")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseRow("23")
                                )
                        )
                )
        );
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck(
                "1:$23",
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseRow("1")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseRow("$23")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSwap() {
        this.parseStringAndCheck(
                "1:23",
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseRow("1")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseRow("23")
                                )
                        )
                )
        );
    }

    // count............................................................................................................

    @Test
    public void testCountSingleton() {
        this.countAndCheck(
                SpreadsheetSelection.parseRowRange("2"),
                1
        );
    }

    @Test
    public void testCount() {
        this.countAndCheck(
                SpreadsheetSelection.parseRowRange("3:5"),
                3
        );
    }

    // test............................................................................................................

    @Test
    public void testTestAbove() {
        this.testFalse(SpreadsheetCellReference.parseCell("A1"));
    }

    @Test
    public void testTestLeft() {
        this.testTrue(SpreadsheetCellReference.parseCell("B2"));
    }

    @Test
    public void testTestRight() {
        this.testTrue(SpreadsheetCellReference.parseCell("D4"));
    }

    @Test
    public void testTestBelow() {
        this.testFalse(SpreadsheetCellReference.parseCell("E5"));
    }

    // testCellRange....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck2(
                "1:1",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeBefore2() {
        this.testCellRangeAndCheck2(
                "1:2",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftOverlap() {
        this.testCellRangeAndCheck2(
                "1:3",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeInside() {
        this.testCellRangeAndCheck2(
                "4:5",
                "C3:F6",
                true
        );
    }

    @Test
    public void testTestCellRangeRightOverlap() {
        this.testCellRangeAndCheck2(
                "4:5",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAll() {
        this.testCellRangeAndCheck2(
                "1:5",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck2(
                "5:5",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeAfter2() {
        this.testCellRangeAndCheck2(
                "5:6",
                "C3:D4",
                false
        );
    }

    private void testCellRangeAndCheck2(final String rowRange,
                                        final String cellRange,
                                        final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetSelection.parseRowRange(rowRange),
                SpreadsheetSelection.parseCellRange(cellRange),
                expected
        );
    }

    // testRow..........................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
                "2:2",
                "1",
                false
        );
    }

    @Test
    public void testTestRowAbove2() {
        this.testRowAndCheck(
                "2:3",
                "1",
                false
        );
    }

    @Test
    public void testTestRowTopOverlap() {
        this.testRowAndCheck(
                "1:3",
                "1",
                true
        );
    }

    @Test
    public void testTestRowInside() {
        this.testRowAndCheck(
                "4:6",
                "5",
                true
        );
    }

    @Test
    public void testTestRowBottomOverlap() {
        this.testRowAndCheck(
                "4:5",
                "5",
                true
        );
    }

    @Test
    public void testTestRowAll() {
        this.testRowAndCheck(
                "2:2",
                "2",
                true
        );
    }

    @Test
    public void testTestRowAfter() {
        this.testRowAndCheck(
                "5:5",
                "6",
                false
        );
    }

    @Test
    public void testTestRowAfter2() {
        this.testRowAndCheck(
                "5:6",
                "8",
                false
        );
    }

    private void testRowAndCheck(final String rowRange,
                                 final String row,
                                 final boolean expected) {
        this.checkEquals(
                expected,
                SpreadsheetSelection.parseRowRange(rowRange)
                        .testRow(SpreadsheetSelection.parseRow(row)),
                () -> rowRange + " testRow " + row
        );
    }

    // setColumnReferenceRange............................................................................................

    @Test
    public void testSetColumnReferenceRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setColumnReferenceRange(null));
    }

    @Test
    public void testSetColumnReferenceRange() {
        this.setColumnReferenceRangeAndCheck("2:4", "B:D", "B2:D4");
    }

    @Test
    public void testSetColumnReferenceRange2() {
        this.setColumnReferenceRangeAndCheck("2", "B", "B2");
    }

    @Test
    public void testSetColumnReferenceRange3() {
        this.setColumnReferenceRangeAndCheck("2", "B:D", "B2:D2");
    }

    private void setColumnReferenceRangeAndCheck(final String row,
                                                 final String column,
                                                 final String range) {
        this.checkEquals(
                SpreadsheetSelection.parseCellRange(range),
                SpreadsheetSelection.parseColumnRange(column).setRowReferenceRange(SpreadsheetSelection.parseRowRange(row)),
                () -> column + " setRowReferenceRange " + row
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testLeftAnchorTop() {
        this.leftAndCheck(
                "2:3",
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testUpAnchorTop() {
        this.upAndCheck(
                "2:3",
                SpreadsheetViewportSelectionAnchor.TOP,
                "2"
        );
    }

    @Test
    public void testUpAnchorBottom() {
        this.upAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "1"
        );
    }

    @Test
    public void testUpAnchorBottom2() {
        this.upAndCheck(
                "1:3",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "1"
        );
    }

    @Test
    public void testUpFirstRowAnchorTop() {
        this.upAndCheck(
                "1:3",
                SpreadsheetViewportSelectionAnchor.TOP,
                "2"
        );
    }

    @Test
    public void testUpFirstRowAnchorBottom() {
        this.upAndCheck(
                "1:2",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "1:1"
        );
    }

    @Test
    public void testRightAnchorTop() {
        this.leftAndCheck(
                "2:3",
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testDownAnchorTop() {
        this.downAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "5"
        );
    }

    @Test
    public void testDownAnchorBottom() {
        this.downAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "3"
        );
    }

    @Test
    public void testDownFirstRowAnchorTop() {
        this.downAndCheck(
                "1:3",
                SpreadsheetViewportSelectionAnchor.TOP,
                "4"
        );
    }

    @Test
    public void testDownFirstRowAnchorBottom() {
        this.downAndCheck(
                "1:3",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "2"
        );
    }

    @Test
    public void testDownLastRowAnchorTop() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downAndCheck(
                "1:" + row,
                SpreadsheetViewportSelectionAnchor.TOP,
                row
        );
    }

    @Test
    public void testDownLastRowAnchorBottom() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downAndCheck(
                "1:" + row,
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "2"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRangeTop() {
        this.extendRangeAndCheck(
                "2:3",
                "4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "2:4"
        );
    }

    @Test
    public void testExtendRangeTopLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendRangeAndCheck(
                "999:" + last,
                "" + last,
                SpreadsheetViewportSelectionAnchor.TOP,
                "999:" + last
        );
    }

    @Test
    public void testExtendRangeBottomFirstRow() {
        this.extendRangeAndCheck(
                "2:3",
                "1",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "1:3"
        );
    }

    @Test
    public void testExtendRangeBottom() {
        this.extendRangeAndCheck(
                "3:4",
                "2",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "2:4"
        );
    }

    @Test
    public void testExtendRangeTopSame() {
        this.extendRangeAndCheck(
                "1:3",
                "3",
                SpreadsheetViewportSelectionAnchor.TOP,
                "1:3"
        );
    }

    @Test
    public void testExtendRangeBottomSame() {
        this.extendRangeAndCheck(
                "1:3",
                "1",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "1:3"
        );
    }

    @Override
    SpreadsheetRowReferenceRange parseRange(final String range) {
        return SpreadsheetSelection.parseRowRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendUpAnchorBottom() {
        this.extendUpAndCheck(
                "3:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpAnchorUp() {
        this.extendUpAndCheck(
                "3:4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "3"
        );
    }

    @Test
    public void testExtendUpAnchorBottomFirstRow() {
        this.extendUpAndCheck(
                "1:2",
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendDownAnchorTop() {
        this.extendDownAndCheck(
                "3:4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "3:5",
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testExtendDownAnchorBottom() {
        this.extendDownAndCheck(
                "3:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "4",
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testExtendDownLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendDownAndCheck(
                row.add(-1)
                        .rowRange(row),
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testExtendLeft() {
        this.extendLeftAndCheck(
                "2:3"
        );
    }

    @Test
    public void testExtendRight() {
        this.extendRightAndCheck(
                "2:3"
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRowReferenceRange selection = this.createSelection();

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
            protected void visit(final SpreadsheetRowReferenceRange s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // simplify.........................................................................................................

    @Test
    public void testSimplifyDifferentBeginAndEnd() {
        this.simplifyAndCheck(
                "1:2"
        );
    }

    @Test
    public void testSimplifyBeginAndEndDifferentKind() {
        this.simplifyAndCheck(
                "1:$2"
        );
    }

    @Test
    public void testSimplifyBeginAndEndSame() {
        this.simplifyAndCheck(
                "1:1",
                SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testSimplifyBeginAndEndSame2() {
        this.simplifyAndCheck(
                "$1:1",
                SpreadsheetSelection.parseRow("$1")
        );
    }

    @Test
    public void testSimplifyBeginAndEndSame3() {
        this.simplifyAndCheck(
                "1:$1",
                SpreadsheetSelection.parseRow("1")
        );
    }

    // IterableTesting..................................................................................................

    @Test
    public void testIterable() {
        this.iterateUsingHasNextAndCheck(
                this.createIterable().iterator(),
                SpreadsheetSelection.parseRow("2"),
                SpreadsheetSelection.parseRow("3"),
                SpreadsheetSelection.parseRow("4")
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRowRange("12:34"),
                "row-range 12:34" + EOL
        );
    }

    // isSingle.........................................................................................................

    @Test
    public void testIsSingleTrue() {
        this.isSingleAndCheck(
                "1:1",
                true
        );
    }

    @Test
    public void testIsSingleDifferentReferenceKindTrue() {
        this.isSingleAndCheck(
                "1:$1",
                true
        );
    }

    @Test
    public void testIsSingleFalse() {
        this.isSingleAndCheck(
                "1:$2",
                false
        );
    }

    // json.............................................................................................................

    @Test
    public void testFromJson() {
        this.unmarshallAndCheck(JsonNode.string("2:4"), this.createSelection());
    }

    @Test
    public void testToJson() {
        this.marshallAndCheck(this.createSelection(), JsonNode.string("2:4"));
    }

    @Test
    public void testDifferentColumn() {
        this.checkNotEquals(SpreadsheetSelection.parseRowRange("55:66"));
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1:3",
                "$1:3",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1:3",
                "2:3",
                false
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1:3",
                "1:$3",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1:3",
                "2:3",
                false
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetRowReferenceRange.with(range), "2:4");
    }

    @Override
    SpreadsheetRowReferenceRange createSelection() {
        final SpreadsheetRowReference lower = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetRowReference.parseRow("4");

        return SpreadsheetRowReferenceRange.with(
                Range.with(
                        RangeBound.inclusive(lower),
                        RangeBound.inclusive(upper)
                )
        );
    }

    @Override
    SpreadsheetRowReferenceRange createSelection(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetRowReferenceRange.with(range);
    }

    @Override
    public SpreadsheetRowReferenceRange parseString(final String text) {
        return SpreadsheetSelection.parseRowRange(text);
    }

    @Override
    public SpreadsheetRowReferenceRange unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallRowRange(node, context);
    }

    @Override
    public Class<SpreadsheetRowReferenceRange> type() {
        return SpreadsheetRowReferenceRange.class;
    }

    // IterableTesting..................................................................................................

    @Override
    public SpreadsheetRowReferenceRange createIterable() {
        return SpreadsheetSelection.parseRowRange("$2:$4");
    }
}
