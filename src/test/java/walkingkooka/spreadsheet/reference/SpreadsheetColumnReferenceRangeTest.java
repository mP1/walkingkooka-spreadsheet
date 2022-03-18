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

public final class SpreadsheetColumnReferenceRangeTest extends SpreadsheetColumnOrRowReferenceRangeTestCase<SpreadsheetColumnReferenceRange, SpreadsheetColumnReference>
        implements IterableTesting<SpreadsheetColumnReferenceRange, SpreadsheetColumnReference> {

    @Test
    public void testWith() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        final SpreadsheetColumnReferenceRange selection = SpreadsheetColumnReferenceRange.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    // parse............................................................................................................

    @Test
    public void testParseSingleColumn() {
        this.parseStringAndCheck(
                "A",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("A")
                        )
                )
        );
    }

    @Test
    public void testParseSingleColumn2() {
        this.parseStringAndCheck(
                "BC",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("BC")
                        )
                )
        );
    }

    @Test
    public void testParseSame() {
        this.parseStringAndCheck(
                "A:A",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("A")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent() {
        this.parseStringAndCheck(
                "A:A",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("A")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent2() {
        this.parseStringAndCheck(
                "A:$A",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("A")
                        )
                )
        );
    }

    @Test
    public void testParseEquivalent3() {
        this.parseStringAndCheck(
                "$BC:BC",
                SpreadsheetColumnReferenceRange.with(
                        Range.singleton(
                                SpreadsheetSelection.parseColumn("$BC")
                        )
                )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
                "A:BC",
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseColumn("A")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseColumn("BC")
                                )
                        )
                )
        );
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck(
                "A:$BC",
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseColumn("A")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseColumn("$BC")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSwap() {
        this.parseStringAndCheck(
                "BC:A",
                SpreadsheetColumnReferenceRange.with(
                        Range.greaterThanEquals(
                                SpreadsheetSelection.parseColumn("A")
                        ).and(
                                Range.lessThanEquals(
                                        SpreadsheetSelection.parseColumn("BC")
                                )
                        )
                )
        );
    }

    // count............................................................................................................

    @Test
    public void testCountSingleton() {
        this.countAndCheck(
                SpreadsheetSelection.parseColumnRange("C"),
                1
        );
    }

    @Test
    public void testCount() {
        this.countAndCheck(
                SpreadsheetSelection.parseColumnRange("C:D"),
                2
        );
    }

    // test............................................................................................................

    @Test
    public void testTestBefore() {
        this.testFalse(SpreadsheetCellReference.parseCell("A1"));
    }

    @Test
    public void testTestLeft() {
        this.testTrue(SpreadsheetCellReference.parseCell("B1"));
    }

    @Test
    public void testTestRight() {
        this.testTrue(SpreadsheetCellReference.parseCell("D2"));
    }

    @Test
    public void testTestAfter() {
        this.testFalse(SpreadsheetCellReference.parseCell("E1"));
    }

    // testCellRange....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck2(
                "A:A",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeBefore2() {
        this.testCellRangeAndCheck2(
                "A:B",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftOverlap() {
        this.testCellRangeAndCheck2(
                "A:C",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeInside() {
        this.testCellRangeAndCheck2(
                "D:E",
                "C3:F6",
                true
        );
    }

    @Test
    public void testTestCellRangeRightOverlap() {
        this.testCellRangeAndCheck2(
                "D:E",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAll() {
        this.testCellRangeAndCheck2(
                "A:E",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck2(
                "E:E",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeAfter2() {
        this.testCellRangeAndCheck2(
                "E:F",
                "C3:D4",
                false
        );
    }

    private void testCellRangeAndCheck2(final String columnRange,
                                        final String cellRange,
                                        final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetSelection.parseColumnRange(columnRange),
                SpreadsheetSelection.parseCellRange(cellRange),
                expected
        );
    }

    // testColumn....................................................................................................

    @Test
    public void testTestColumnBefore() {
        this.testColumnAndCheck2(
                "A:B",
                "C",
                false
        );
    }

    @Test
    public void testTestColumnBefore2() {
        this.testColumnAndCheck2(
                "A:B",
                "D",
                false
        );
    }

    @Test
    public void testTestColumnLeftOverlap() {
        this.testColumnAndCheck2(
                "B:C",
                "B",
                true
        );
    }

    @Test
    public void testTestColumnInside() {
        this.testColumnAndCheck2(
                "D:F",
                "E",
                true
        );
    }

    @Test
    public void testTestColumnRightOverlap() {
        this.testColumnAndCheck2(
                "D:E",
                "E",
                true
        );
    }

    @Test
    public void testTestColumnAfter() {
        this.testColumnAndCheck2(
                "D:E",
                "F",
                false
        );
    }

    @Test
    public void testTestColumnAfter2() {
        this.testColumnAndCheck2(
                "E:F",
                "H",
                false
        );
    }

    private void testColumnAndCheck2(final String columnRange,
                                     final String column,
                                     final boolean expected) {
        this.checkEquals(
                expected,
                SpreadsheetSelection.parseColumnRange(columnRange)
                        .testColumn(SpreadsheetSelection.parseColumn(column)),
                columnRange + ".testColumn(" + column + ")"
        );
    }

    // setRowReferenceRange............................................................................................

    @Test
    public void testSetRowReferenceRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setRowReferenceRange(null));
    }

    @Test
    public void testSetRowReferenceRange() {
        this.setRowReferenceRangeAndCheck("B:D", "2:4", "B2:D4");
    }

    @Test
    public void testSetRowReferenceRange2() {
        this.setRowReferenceRangeAndCheck("B", "2", "B2");
    }

    @Test
    public void testSetRowReferenceRange3() {
        this.setRowReferenceRangeAndCheck("B:D", "2", "B2:D2");
    }

    private void setRowReferenceRangeAndCheck(final String column,
                                              final String row,
                                              final String range) {
        this.checkEquals(
                SpreadsheetSelection.parseCellRange(range),
                SpreadsheetSelection.parseColumnRange(column).setRowReferenceRange(SpreadsheetSelection.parseRowRange(row)),
                () -> column + " setRowReferenceRange " + row
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testLeftAnchorLeft() {
        this.leftAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B"
        );
    }

    @Test
    public void testLeftAnchorRight() {
        this.leftAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "A"
        );
    }

    @Test
    public void testLeftFirstColumnAnchorLeft() {
        this.leftAndCheck(
                "A:C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B"
        );
    }

    @Test
    public void testLeftFirstColumnAnchorRight() {
        this.leftAndCheck(
                "A:C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "A"
        );
    }

    @Test
    public void testUpAnchorLeft() {
        this.upAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testRightAnchorLeft() {
        this.rightAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "D"
        );
    }

    @Test
    public void testRightAnchorRight() {
        this.rightAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "C"
        );
    }

    @Test
    public void testRightFirstColumnAnchorLeft() {
        this.rightAndCheck(
                "A:C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "D"
        );
    }

    @Test
    public void testRightFirstColumnAnchorRight() {
        this.rightAndCheck(
                "A:C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "B"
        );
    }

    @Test
    public void testDownAnchorLeft() {
        this.downAndCheck(
                "B:C",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRangeLeft() {
        this.extendRangeAndCheck(
                "B:C",
                "D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B:D"
        );
    }

    @Test
    public void testExtendRangeLeftLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRangeAndCheck(
                "Z:" + last,
                "" + last,
                SpreadsheetViewportSelectionAnchor.LEFT,
                "Z:" + last
        );
    }

    @Test
    public void testExtendRangeRightFirstColumn() {
        this.extendRangeAndCheck(
                "B:C",
                "A",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "A:C"
        );
    }

    @Test
    public void testExtendRangeRight() {
        this.extendRangeAndCheck(
                "C:D",
                "B",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "B:D"
        );
    }

    @Test
    public void testExtendRangeLeftSame() {
        this.extendRangeAndCheck(
                "A:C",
                "C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "A:C"
        );
    }

    @Test
    public void testExtendRangeRightSame() {
        this.extendRangeAndCheck(
                "A:C",
                "A",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "A:C"
        );
    }

    @Override
    SpreadsheetColumnReferenceRange parseRange(final String range) {
        return SpreadsheetSelection.parseColumnRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeftAnchorRight() {
        this.extendLeftAndCheck(
                "C:D",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "B:D",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftAnchorLeft() {
        this.extendLeftAndCheck(
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "C"
        );
    }

    @Test
    public void testExtendLeftAnchorRightFirstColumn() {
        this.extendLeftAndCheck(
                "A:B",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRightAnchorLeft() {
        this.extendRightAndCheck(
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "C:E",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightAnchorRight() {
        this.extendRightAndCheck(
                "C:D",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "D",
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testExtendRightLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRightAndCheck(
                column.add(-1).columnRange(column),
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendUp() {
        this.extendUpAndCheck(
                "B:C"
        );
    }

    @Test
    public void testExtendDown() {
        this.extendDownAndCheck(
                "B:C"
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReferenceRange selection = this.createSelection();

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
            protected void visit(final SpreadsheetColumnReferenceRange s) {
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
                "A:B"
        );
    }

    @Test
    public void testSimplifyBeginAndEndDifferentKind() {
        this.simplifyAndCheck(
                "A:$B"
        );
    }

    @Test
    public void testSimplifyBeginAndEndSame() {
        this.simplifyAndCheck(
                "A:A",
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testSimplifyBeginAndEndSame2() {
        this.simplifyAndCheck(
                "$A:A",
                SpreadsheetSelection.parseColumn("$A")
        );
    }

    // IterableTesting..................................................................................................

    @Test
    public void testIterable() {
        this.iterateAndCheck(
                this.createIterable().iterator(),
                SpreadsheetSelection.parseColumn("B"),
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetSelection.parseColumn("D")
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseColumnRange("A:B"),
                "column-range A:B" + EOL
        );
    }

    // isSingle.........................................................................................................

    @Test
    public void testIsSingleTrue() {
        this.isSingleAndCheck(
                "A:A",
                true
        );
    }

    @Test
    public void testIsSingleDifferentReferenceKindTrue() {
        this.isSingleAndCheck(
                "A:$A",
                true
        );
    }

    @Test
    public void testIsSingleFalse() {
        this.isSingleAndCheck(
                "A:$B",
                false
        );
    }

    // json.............................................................................................................

    @Test
    public void testFromJson() {
        this.unmarshallAndCheck(JsonNode.string("B:D"), this.createSelection());
    }

    @Test
    public void testToJson() {
        this.marshallAndCheck(this.createSelection(), JsonNode.string("B:D"));
    }

    @Test
    public void testDifferentColumn() {
        this.checkNotEquals(SpreadsheetSelection.parseColumnRange("Y:Z"));
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A:C",
                "$A:C",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindBeginDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A:C",
                "B:C",
                false
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A:C",
                "A:$C",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindEndDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A:C",
                "B:C",
                false
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetColumnReferenceRange.with(range), "B:D");
    }

    @Override
    SpreadsheetColumnReferenceRange createSelection() {
        final SpreadsheetColumnReference lower = SpreadsheetColumnReference.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetColumnReference.parseColumn("D");

        return SpreadsheetColumnReferenceRange.with(
                Range.with(
                        RangeBound.inclusive(lower),
                        RangeBound.inclusive(upper)
                )
        );
    }

    @Override
    SpreadsheetColumnReferenceRange createSelection(final Range<SpreadsheetColumnReference> range) {
        return SpreadsheetColumnReferenceRange.with(range);
    }

    @Override
    public SpreadsheetColumnReferenceRange parseString(final String text) {
        return SpreadsheetSelection.parseColumnRange(text);
    }

    @Override
    public SpreadsheetColumnReferenceRange unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallColumnRange(node, context);
    }

    @Override
    public Class<SpreadsheetColumnReferenceRange> type() {
        return SpreadsheetColumnReferenceRange.class;
    }

    // IterableTesting..................................................................................................

    @Override
    public SpreadsheetColumnReferenceRange createIterable() {
        return SpreadsheetSelection.parseColumnRange("$B:$D");
    }
}
