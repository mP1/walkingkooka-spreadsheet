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
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnReferenceRangeTest extends SpreadsheetColumnOrRowReferenceRangeTestCase<SpreadsheetColumnReferenceRange, SpreadsheetColumnReference>
        implements IterableTesting<SpreadsheetColumnReferenceRange, SpreadsheetColumnReference> {

    @Test
    public void testAll() {
        this.toStringAndCheck(
                SpreadsheetColumnReferenceRange.ALL,
                "A:XFD"
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

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
                "C",
                1
        );
    }

    @Test
    public void testCount() {
        this.countAndCheck(
                "C:D",
                2
        );
    }

    // test............................................................................................................

    @Test
    public void testTestBefore() {
        this.testFalse(SpreadsheetSelection.parseCell("A1"));
    }

    @Test
    public void testTestLeft() {
        this.testTrue(SpreadsheetSelection.parseCell("B1"));
    }

    @Test
    public void testTestRight() {
        this.testTrue(SpreadsheetSelection.parseCell("D2"));
    }

    @Test
    public void testTestAfter() {
        this.testFalse(SpreadsheetSelection.parseCell("E1"));
    }

    // testCellRange....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck(
                "A:A",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeBefore2() {
        this.testCellRangeAndCheck(
                "A:B",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftOverlap() {
        this.testCellRangeAndCheck(
                "A:C",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeInside() {
        this.testCellRangeAndCheck(
                "D:E",
                "C3:F6",
                true
        );
    }

    @Test
    public void testTestCellRangeRightOverlap() {
        this.testCellRangeAndCheck(
                "D:E",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAll() {
        this.testCellRangeAndCheck(
                "A:E",
                "C3:D4",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck(
                "E:E",
                "C3:D4",
                false
        );
    }

    @Test
    public void testTestCellRangeAfter2() {
        this.testCellRangeAndCheck(
                "E:F",
                "C3:D4",
                false
        );
    }

    // testColumn....................................................................................................

    @Test
    public void testTestColumnBefore() {
        this.testColumnAndCheck(
                "A:B",
                "C",
                false
        );
    }

    @Test
    public void testTestColumnBefore2() {
        this.testColumnAndCheck(
                "A:B",
                "D",
                false
        );
    }

    @Test
    public void testTestColumnLeftOverlap() {
        this.testColumnAndCheck(
                "B:C",
                "B",
                true
        );
    }

    @Test
    public void testTestColumnInside() {
        this.testColumnAndCheck(
                "D:F",
                "E",
                true
        );
    }

    @Test
    public void testTestColumnRightOverlap() {
        this.testColumnAndCheck(
                "D:E",
                "E",
                true
        );
    }

    @Test
    public void testTestColumnAfter() {
        this.testColumnAndCheck(
                "D:E",
                "F",
                false
        );
    }

    @Test
    public void testTestColumnAfter2() {
        this.testColumnAndCheck(
                "E:F",
                "H",
                false
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
                "E:F",
                "1",
                false
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

    // frozenColumnsCheck...............................................................................................

    @Test
    public void testFrozenColumnsCheck() {
        SpreadsheetSelection.parseColumnRange("A")
                .frozenColumnsCheck();
    }

    @Test
    public void testFrozenColumnsCheck2() {
        SpreadsheetSelection.parseColumnRange("A:B")
                .frozenColumnsCheck();
    }

    @Test
    public void testFrozenColumnsCheck3() {
        SpreadsheetSelection.parseColumnRange("A:C")
                .frozenColumnsCheck();
    }

    @Test
    public void testFrozenColumnsCheckFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseColumnRange("B").frozenColumnsCheck()
        );

        this.checkEquals(
                "Range must begin at 'A' but was \"B\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testFrozenColumnsCheckFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseColumnRange("C:E").frozenColumnsCheck()
        );

        this.checkEquals(
                "Range must begin at 'A' but was \"C:E\"",
                thrown.getMessage()
        );
    }

    // isAll............................................................................................................

    @Test
    public void testIsAll() {
        this.isAllAndCheck(
                "A:A",
                false
        );
    }

    @Test
    public void testIsAll2() {
        this.isAllAndCheck(
                "B:C",
                false
        );
    }

    @Test
    public void testIsAll3() {
        this.isAllAndCheck(
                "A:" + SpreadsheetReferenceKind.RELATIVE.lastColumn(),
                true
        );
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenBeginHidden() {
        this.isHiddenAndCheck(
                "A:B",
                Predicates.is(SpreadsheetSelection.parseColumn("A")),
                Predicates.fake(),
                true
        );
    }

    @Test
    public void testIsHiddenEndHidden() {
        this.isHiddenAndCheck(
                "A:B",
                Predicates.is(SpreadsheetSelection.parseColumn("B")),
                Predicates.fake(),
                true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
                "A:B",
                Predicates.never(),
                Predicates.fake(),
                false
        );
    }

    @Test
    public void testIsHiddenHidden2() {
        this.isHiddenAndCheck(
                "A:$A",
                Predicates.always(),
                Predicates.fake(),
                true
        );
    }

    @Test
    public void testIsHiddenNotHidden2() {
        this.isHiddenAndCheck(
                "A:$A",
                Predicates.never(),
                Predicates.fake(),
                false
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
    public void testLeftSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.leftAndCheck(
                "D:E",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                store,
                "B"
        );
    }

    @Test
    public void testUpAnchorLeft() {
        final String range = "B:C";

        this.upAndCheck(
                range,
                SpreadsheetViewportSelectionAnchor.LEFT,
                range
        );
    }

    @Test
    public void testUpHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.upAndCheck(
                "C:D",
                store
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
    public void testRightSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("E").column().setHidden(true));

        this.rightAndCheck(
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                store,
                "F"
        );
    }

    @Test
    public void testDownAnchorLeft() {
        final String range = "B:C";

        this.downAndCheck(
                range,
                SpreadsheetViewportSelectionAnchor.LEFT,
                range
        );
    }

    @Test
    public void testDownHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.downAndCheck(
                "C:D",
                store
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
                "C",
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testExtendLeftAnchorRightFirstColumn() {
        final String range = "A:B";

        this.extendLeftAndCheck(
                range,
                SpreadsheetViewportSelectionAnchor.RIGHT,
                range,
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
                SpreadsheetViewportSelectionAnchor.RIGHT,
                column.setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
        );
    }

    @Test
    public void testExtendLeftColumnSingleLeft() {
        this.extendLeftAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B:C",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnSingleRight() {
        this.extendLeftAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "B:C",
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRightColumnSingleLeft() {
        this.extendRightAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightColumnSingleRight() {
        this.extendRightAndCheck(
                "C",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "C:D",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testExtendUp() {
        final String row = "B:C";

        this.extendUpAndCheck(
                row,
                SpreadsheetViewportSelectionAnchor.RIGHT,
                row,
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testExtendDown() {
        final String row = "B:C";

        this.extendDownAndCheck(
                row,
                SpreadsheetViewportSelectionAnchor.RIGHT,
                row,
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocusedLeft() {
        this.focusedAndCheck(
                "A:B",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B"
        );
    }

    @Test
    public void testFocusedRight() {
        this.focusedAndCheck(
                "$C:D",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "$C"
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

    // toCellRange.....................................................................................................

    @Test
    public void testToCellRange() {
        this.toCellRangeAndCheck(
                "A",
                "A2"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
                "B:C",
                "C3"
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("B:D"), this.createSelection());
    }

    @Test
    public void testMarshall2() {
        this.marshallAndCheck(
                this.createSelection(),
                JsonNode.string("B:D")
        );
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

    // Comparable.......................................................................................................

    @Test
    public void testCompareToEquals() {
        this.compareToAndCheckEquals(
                SpreadsheetSelection.parseColumnRange("A:B" ),
                SpreadsheetSelection.parseColumnRange("A:B" )
        );
    }

    @Test
    public void testCompareToEqualsDifferentKind() {
        this.compareToAndCheckEquals(
                SpreadsheetSelection.parseColumnRange("A:B" ),
                SpreadsheetSelection.parseColumnRange("$A:$B" )
        );
    }

    @Test
    public void testCompareToEqualsLess() {
        this.compareToAndCheckLess(
                SpreadsheetSelection.parseColumnRange("A:B" ),
                SpreadsheetSelection.parseColumnRange("B:C" )
        );
    }

    @Test
    public void testCompareToEqualsLessDifferentKind() {
        this.compareToAndCheckLess(
                SpreadsheetSelection.parseColumnRange("A:B" ),
                SpreadsheetSelection.parseColumnRange("$B:$C" )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetColumnReferenceRange.with(range), "B:D");
    }

    @Override
    SpreadsheetColumnReferenceRange createSelection() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

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
