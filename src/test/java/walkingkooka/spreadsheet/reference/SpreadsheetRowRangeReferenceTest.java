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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlFragment;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowRangeReferenceTest extends SpreadsheetColumnOrRowRangeReferenceTestCase<SpreadsheetRowRangeReference, SpreadsheetRowReference>
    implements IterableTesting<SpreadsheetRowRangeReference, SpreadsheetRowReference> {

    @Test
    public void testWith() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
            RangeBound.inclusive(lower),
            RangeBound.inclusive(upper)
        );

        final SpreadsheetRowRangeReference selection = SpreadsheetRowRangeReference.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseRowRange("1:2"),
            "Row Range not found: \"1:2\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("12:34");
    }

    // parse............................................................................................................

    @Test
    public void testParseSingleColumn() {
        this.parseStringAndCheck(
            "1",
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
            SpreadsheetRowRangeReference.with(
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
    public void testParseStar() {
        this.parseStringAndCheck(
            "*",
            SpreadsheetSelection.ALL_ROWS
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

    // containsAll......................................................................................................

    @Test
    public void testContainsAll() {
        this.containsAllAndCheck(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetViewportWindows.parse("A1"),
            false
        );
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellAbove() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.A1,
            false
        );
    }

    @Test
    public void testTestCellLeft() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.parseCell("B2"),
            true
        );
    }

    @Test
    public void testTestCellRight() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.parseCell("D4"),
            true
        );
    }

    @Test
    public void testTestCellBelow() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.parseCell("E5"),
            false
        );
    }

    // testCellRange....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck(
            "1:1",
            "C3:D4",
            false
        );
    }

    @Test
    public void testTestCellRangeBefore2() {
        this.testCellRangeAndCheck(
            "1:2",
            "C3:D4",
            false
        );
    }

    @Test
    public void testTestCellRangeLeftOverlap() {
        this.testCellRangeAndCheck(
            "1:3",
            "C3:D4",
            true
        );
    }

    @Test
    public void testTestCellRangeInside() {
        this.testCellRangeAndCheck(
            "4:5",
            "C3:F6",
            true
        );
    }

    @Test
    public void testTestCellRangeRightOverlap() {
        this.testCellRangeAndCheck(
            "4:5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testTestCellRangeAll() {
        this.testCellRangeAndCheck(
            "1:5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck(
            "5:5",
            "C3:D4",
            false
        );
    }

    @Test
    public void testTestCellRangeAfter2() {
        this.testCellRangeAndCheck(
            "5:6",
            "C3:D4",
            false
        );
    }

    // testColumn.......................................................................................................

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
            "1:2",
            "A",
            false
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

    // setColumnRange...................................................................................................

    @Test
    public void testSetColumnRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection()
                .setColumnRange(null)
        );
    }

    @Test
    public void testSetColumnRange() {
        this.setColumnRangeAndCheck(
            "2:4",
            "B:D",
            "B2:D4"
        );
    }

    @Test
    public void testSetColumnRange2() {
        this.setColumnRangeAndCheck(
            "2",
            "B",
            "B2"
        );
    }

    @Test
    public void testSetColumnRange3() {
        this.setColumnRangeAndCheck(
            "2",
            "B:D",
            "B2:D2"
        );
    }

    private void setColumnRangeAndCheck(final String row,
                                        final String column,
                                        final String expected) {
        this.checkEquals(
            SpreadsheetSelection.parseCellRange(expected),
            SpreadsheetSelection.parseColumnRange(column)
                .setRowRange(
                    SpreadsheetSelection.parseRowRange(row)
                ),
            () -> column + " setColumnRange " + row
        );
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.addAndCheck(
            SpreadsheetSelection.parseRowRange("1:4"),
            2,
            SpreadsheetSelection.parseRowRange("3:6")
        );
    }

    // addSaturated.....................................................................................................

    @Test
    public void testAddSaturated() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseRowRange("1:4"),
            2,
            SpreadsheetSelection.parseRowRange("3:6")
        );
    }

    @Test
    public void testAddSaturated2() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseRowRange("1:4"),
            -1,
            SpreadsheetSelection.parseRowRange("1:3")
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
            SpreadsheetSelection.parseRowRange("3"),
            0,
            2,
            SpreadsheetSelection.parseRowRange("5")
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeAbsolute() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseRowRange("$1"),
            123
        );
    }

    @Test
    public void testAddIfRelativeAbsolute2() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseRowRange("$2:$3"),
            456
        );
    }

    @Test
    public void testAddIfRelativeMixed() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseRowRange("4:$6"),
            1,
            SpreadsheetSelection.parseRowRange("5:$6")
        );
    }

    @Test
    public void testAddIfRelativeMixed2() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseRowRange("$4:6"),
            1,
            SpreadsheetSelection.parseRowRange("$4:7")
        );
    }

    // replaceReferencesMapper..........................................................................................

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
            "2:3",
            SpreadsheetSelection.parseRowRange("5:7"),
            0,
            3
        );
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "1:3",
            "1=text;2=text;33=text",
            "Invalid row(s) 33 are not within 1:3"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "1:3",
            "A=text1;B=text2;C=text3;D=text4"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "1:3",
            "1=text1;2=text2;3=text3"
        );
    }

    // frozenRowsCheck...............................................................................................

    @Test
    public void testFrozenRowsCheck() {
        SpreadsheetSelection.parseRowRange("1")
            .frozenRowsCheck();
    }

    @Test
    public void testFrozenRowsCheck2() {
        SpreadsheetSelection.parseRowRange("1:2")
            .frozenRowsCheck();
    }

    @Test
    public void testFrozenRowsCheck3() {
        SpreadsheetSelection.parseRowRange("1:3")
            .frozenRowsCheck();
    }

    @Test
    public void testFrozenRowsCheckFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseRowRange("2").frozenRowsCheck()
        );

        this.checkEquals(
            "Range must begin at '1' but was \"2\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testFrozenRowsCheckFails2() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.parseRowRange("3:4").frozenRowsCheck()
        );

        this.checkEquals(
            "Range must begin at '1' but was \"3:4\"",
            thrown.getMessage()
        );
    }

    // cellColumnOrRowText..............................................................................................

    @Test
    public void testCellColumnOrRow() {
        this.cellColumnOrRowTextAndCheck("row");
    }

    // isAll............................................................................................................

    @Test
    public void testIsAll() {
        this.isAllAndCheck(
            "1:1",
            false
        );
    }

    @Test
    public void testIsAll2() {
        this.isAllAndCheck(
            "2:3",
            false
        );
    }

    @Test
    public void testIsAll3() {
        this.isAllAndCheck(
            "1:" + SpreadsheetReferenceKind.RELATIVE.lastRow(),
            true
        );
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenBeginHidden() {
        this.isHiddenAndCheck(
            "1:2",
            Predicates.fake(),
            Predicates.is(SpreadsheetSelection.parseRow("1")),
            true
        );
    }

    @Test
    public void testIsHiddenEndHidden() {
        this.isHiddenAndCheck(
            "1:2",
            Predicates.fake(),
            Predicates.is(SpreadsheetSelection.parseRow("2")),
            true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
            "1:2",
            Predicates.fake(),
            Predicates.never(),
            false
        );
    }

    @Test
    public void testIsHiddenHidden2() {
        this.isHiddenAndCheck(
            "1:$1",
            Predicates.fake(),
            Predicates.always(),
            true
        );
    }

    @Test
    public void testIsHiddenNotHidden2() {
        this.isHiddenAndCheck(
            "2:$2",
            Predicates.fake(),
            Predicates.never(),
            false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testMoveLeftColumnAnchorTop() {
        this.moveLeftColumnAndCheck(
            "2:3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2:3"
        );
    }

    @Test
    public void testMoveLeftPixels() {
        this.moveRightPixelsAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "3:4"
        );
    }

    @Test
    public void testMoveUpRowAnchorTop() {
        this.moveUpRowAndCheck(
            "2:3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveUpRowAnchorBottom() {
        this.moveUpRowAndCheck(
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveUpRowAnchorBottom2() {
        this.moveUpRowAndCheck(
            "1:3",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveUpRowFirstAnchorTop() {
        this.moveUpRowAndCheck(
            "1:3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveUpRowFirstAnchorBottom() {
        this.moveUpRowAndCheck(
            "1:2",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "1"
        );
    }

    @Test
    public void testMoveUpPixels() {
        this.moveUpPixelsAndCheck(
            "4:5",
            SpreadsheetViewportAnchor.TOP,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Map.of("4", 50.0, "2", 50.0),
            "2"
        );
    }

    @Test
    public void testMoveRightPixels() {
        this.moveRightPixelsAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "3:4"
        );
    }

    @Test
    public void testMoveDownRowAnchorTop() {
        this.moveDownRowAndCheck(
            "2:4",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "5"
        );
    }

    @Test
    public void testMoveDownRowAnchorBottom() {
        this.moveDownRowAndCheck(
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3"
        );
    }

    @Test
    public void testMoveDownRowFirstAnchorTop() {
        this.moveDownRowAndCheck(
            "1:3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "4"
        );
    }

    @Test
    public void testMoveDownRowFirstAnchorBottom() {
        this.moveDownRowAndCheck(
            "1:3",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveDownRowLastAnchorTop() {
        final String row = SpreadsheetReferenceKind.RELATIVE.lastRow()
            .toString();

        this.moveDownRowAndCheck(
            "1:" + row,
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row
        );
    }

    @Test
    public void testMoveDownRowLastAnchorBottom() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.moveDownRowAndCheck(
            "1:" + row,
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2"
        );
    }

    @Test
    public void testMoveDownPixels() {
        this.moveDownPixelsAndCheck(
            "2:3",
            SpreadsheetViewportAnchor.TOP,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "4",
            Map.of("5", 50.0, "6", 50.0),
            "6"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRangeTop() {
        this.extendRangeAndCheck(
            "2:3",
            "4",
            SpreadsheetViewportAnchor.TOP,
            "2:4"
        );
    }

    @Test
    public void testExtendRangeTopLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendRangeAndCheck(
            "999:" + last,
            "" + last,
            SpreadsheetViewportAnchor.TOP,
            "999:" + last
        );
    }

    @Test
    public void testExtendRangeBottomFirstRow() {
        this.extendRangeAndCheck(
            "2:3",
            "1",
            SpreadsheetViewportAnchor.BOTTOM,
            "1:3"
        );
    }

    @Test
    public void testExtendRangeBottom() {
        this.extendRangeAndCheck(
            "3:4",
            "2",
            SpreadsheetViewportAnchor.BOTTOM,
            "2:4"
        );
    }

    @Test
    public void testExtendRangeTopSame() {
        this.extendRangeAndCheck(
            "1:3",
            "3",
            SpreadsheetViewportAnchor.TOP,
            "1:3"
        );
    }

    @Test
    public void testExtendRangeBottomSame() {
        this.extendRangeAndCheck(
            "1:3",
            "1",
            SpreadsheetViewportAnchor.BOTTOM,
            "1:3"
        );
    }

    @Test
    public void testExtendUpRowSingleLeft() {
        this.extendUpRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpRowSingleRight() {
        this.extendUpRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendDownColumnSingleLeft() {
        this.extendDownRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendDownRowSingleRight() {
        this.extendDownRowAndCheck(
            "3",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Override
    SpreadsheetRowRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseRowRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendUpAnchorBottom() {
        this.extendUpRowAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpAnchorUp() {
        this.extendUpRowAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            "",
            "",
            "3",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendUpAnchorBottomFirstRow() {
        final String row = "1:2";
        final SpreadsheetViewportAnchor anchor = SpreadsheetViewportAnchor.BOTTOM;

        this.extendUpRowAndCheck(
            row,
            anchor,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            anchor
        );
    }

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "6:7",
            SpreadsheetViewportAnchor.BOTTOM,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "5",
            Maps.of("4", 50.0, "3", 50.0),
            "3:7",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendDownAnchorTop() {
        this.extendDownRowAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "3:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendDownAnchorBottom() {
        this.extendDownRowAndCheck(
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "4",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendDownLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendDownRowAndCheck(
            row.add(-1)
                .rowRange(row).toString(),
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row.toString(),
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "1:2",
            SpreadsheetViewportAnchor.TOP,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Maps.of("4", 50.0, "5", 50.0),
            "1:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testExtendLeftColumn() {
        final String range = "2:3";

        this.extendLeftColumnAndCheck(
            range,
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range,
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendRight() {
        final String range = "2:3";

        this.extendRightColumnAndCheck(
            range,
            SpreadsheetViewportAnchor.BOTTOM,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range,
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "2:3",
            SpreadsheetViewportAnchor.TOP,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "2:3",
            SpreadsheetViewportAnchor.TOP
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocusedTop() {
        this.focusedAndCheck(
            "1:2",
            SpreadsheetViewportAnchor.TOP,
            "2"
        );
    }

    @Test
    public void testFocusedBottom() {
        this.focusedAndCheck(
            "$3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "$3"
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRowRangeReference selection = this.createSelection();

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
            protected void visit(final SpreadsheetRowRangeReference s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // toScalar.........................................................................................................

    @Test
    public void testToScalar() {
        this.toScalarAndCheck(
            "1:2",
            SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testToScalar2() {
        this.toScalarAndCheck(
            "$1:2",
            SpreadsheetSelection.parseRow("$1")
        );
    }

    @Test
    public void testToScalar3() {
        this.toScalarAndCheck(
            "2:3",
            SpreadsheetSelection.parseRow("2")
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
            "1",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "1"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
            "2:3",
            "A2:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "3"
        );
    }

    // toCellOrCellRange................................................................................................

    @Test
    public void testToCellOrCellRange() {
        this.toCellRangeAndCheck(
            "1:1",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "1"
        );
    }

    @Test
    public void testToCellOrCellRange2() {
        this.toCellRangeAndCheck(
            "2:3",
            "A2:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "3"
        );
    }

    // toExpressionReference............................................................................................

    @Test
    public void testToExpressionReference() {
        this.toCellOrCellRangeAndCheck(
            "1:2",
            "A1:" + SpreadsheetColumnOrRowReferenceKind.COLUMN.lastRelative() + "2"
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
        this.toRowAndCheck(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testToRow2() {
        this.toRowAndCheck(
            SpreadsheetSelection.parseRowRange("3:4"),
            SpreadsheetSelection.parseRow("3")
        );
    }

    // toRowRange....................................................................................................

    @Test
    public void testToRowRange() {
        final SpreadsheetRowRangeReference range = this.createSelection();

        this.toRowRangeAndCheck(
            range,
            range
        );
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRange() {
        final SpreadsheetRowRangeReference selection = this.createSelection();
        this.toRowOrRowRangeAndCheck(
            selection,
            selection
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
        this.isUnitAndCheck(
            "1:1",
            true
        );
    }

    @Test
    public void testIsSingleDifferentReferenceKindTrue() {
        this.isUnitAndCheck(
            "1:$1",
            true
        );
    }

    @Test
    public void testIsSingleFalse() {
        this.isUnitAndCheck(
            "1:$2",
            false
        );
    }

    // hasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment2() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseRowRange("1:2"),
            UrlFragment.parse("1:2")
        );
    }

    @Test
    public void testUrlFragmentAllRows() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.ALL_ROWS,
            UrlFragment.parse("*")
        );
    }

    // SpreadsheetColumnOrRowReferenceOrRange...........................................................................

    @Test
    public void testColumnOrRowsIterator() {
        this.iterateAndCheck(
            SpreadsheetSelection.parseRowRange("2:4")
                .columnOrRowsIterator(),
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetSelection.parseRow("3"),
            SpreadsheetSelection.parseRow("4")
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(JsonNode.string("2:4"), this.createSelection());
    }

    @Test
    public void testMarshall2() {
        this.marshallAndCheck(
            this.createSelection(),
            JsonNode.string("2:4")
        );
    }

    @Test
    public void testEqualsDifferentColumn() {
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

    // Comparable.......................................................................................................

    @Test
    public void testCompareToEquals() {
        this.compareToAndCheckEquals(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetSelection.parseRowRange("1:2")
        );
    }

    @Test
    public void testCompareToEqualsDifferentKind() {
        this.compareToAndCheckEquals(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetSelection.parseRowRange("$1:$2")
        );
    }

    @Test
    public void testCompareToEqualsLess() {
        this.compareToAndCheckLess(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetSelection.parseRowRange("2:3")
        );
    }

    @Test
    public void testCompareToEqualsLessDifferentKind() {
        this.compareToAndCheckLess(
            SpreadsheetSelection.parseRowRange("1:2"),
            SpreadsheetSelection.parseRowRange("$2:$3")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        final Range<SpreadsheetRowReference> range = Range.with(
            RangeBound.inclusive(lower),
            RangeBound.inclusive(upper)
        );

        this.toStringAndCheck(SpreadsheetRowRangeReference.with(range), "2:4");
    }

    @Test
    public void testToStringAllRows() {
        this.toStringAndCheck(
            SpreadsheetSelection.ALL_ROWS,
            "1:1048576"
        );
    }

    // toStringMaybeStar................................................................................................

    @Test
    public void testToStringMaybeStar() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.parseRowRange("1:2")
        );
    }

    @Test
    public void testToStringMaybeStarAllRows() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.ALL_ROWS,
            "*"
        );
    }

    @Override
    SpreadsheetRowRangeReference createSelection() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        return SpreadsheetRowRangeReference.with(
            Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
            )
        );
    }

    @Override
    SpreadsheetRowRangeReference createSelection(final Range<SpreadsheetRowReference> range) {
        return SpreadsheetRowRangeReference.with(range);
    }

    @Override
    public SpreadsheetRowRangeReference parseString(final String text) {
        return SpreadsheetSelection.parseRowRange(text);
    }

    @Override
    public SpreadsheetRowRangeReference unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallRowRange(node, context);
    }

    @Override
    public Class<SpreadsheetRowRangeReference> type() {
        return SpreadsheetRowRangeReference.class;
    }

    // IterableTesting..................................................................................................

    @Override
    public SpreadsheetRowRangeReference createIterable() {
        return SpreadsheetSelection.parseRowRange("$2:$4");
    }
}
