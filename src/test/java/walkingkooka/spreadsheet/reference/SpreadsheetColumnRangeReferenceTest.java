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
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnRangeReferenceTest extends SpreadsheetColumnOrRowRangeReferenceTestCase<SpreadsheetColumnRangeReference, SpreadsheetColumnReference>
    implements IterableTesting<SpreadsheetColumnRangeReference, SpreadsheetColumnReference> {

    @Test
    public void testWith() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        final Range<SpreadsheetColumnReference> range = Range.with(
            RangeBound.inclusive(lower),
            RangeBound.inclusive(upper)
        );

        final SpreadsheetColumnRangeReference selection = SpreadsheetColumnRangeReference.with(range);
        assertSame(range, selection.range(), "range");
        assertSame(lower, selection.begin(), "begin");
        assertSame(upper, selection.end(), "end");
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseColumnRange("C:D"),
            "Column Range not found: \"C:D\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("C:D");
    }

    // parse............................................................................................................

    @Test
    public void testParseSingleColumn() {
        this.parseStringAndCheck(
            "A",
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
            SpreadsheetColumnRangeReference.with(
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
    public void testParseStar() {
        this.parseStringAndCheck(
            "*",
            SpreadsheetSelection.ALL_COLUMNS
        );
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.addAndCheck(
            SpreadsheetSelection.parseColumnRange("A:D"),
            2,
            SpreadsheetSelection.parseColumnRange("C:F")
        );
    }

    // addSaturated.....................................................................................................

    @Test
    public void testAddSaturated() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseColumnRange("A:D"),
            2,
            SpreadsheetSelection.parseColumnRange("C:F")
        );
    }

    @Test
    public void testAddSaturated1() {
        this.addSaturatedAndCheck(
            SpreadsheetSelection.parseColumnRange("A:D"),
            -1,
            SpreadsheetSelection.parseColumnRange("A:C")
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
            SpreadsheetSelection.parseColumnRange("K"),
            2,
            0,
            SpreadsheetSelection.parseColumnRange("M")
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeAbsolute() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseColumnRange("$A"),
            123
        );
    }

    @Test
    public void testAddIfRelativeAbsolute2() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseColumnRange("$B:$C"),
            456
        );
    }

    @Test
    public void testAddIfRelativeMixed() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseColumnRange("D:$F"),
            1,
            SpreadsheetSelection.parseColumnRange("E:$F")
        );
    }

    @Test
    public void testAddIfRelativeMixed2() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseColumnRange("$D:F"),
            1,
            SpreadsheetSelection.parseColumnRange("$D:G")
        );
    }

    // replaceReferencesMapper..........................................................................................

    @Test
    public void testReplaceReferencesMapperColumn() {
        this.replaceReferencesMapperAndCheck(
            "A:B",
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
            "B:D",
            SpreadsheetSelection.parseColumn("E"),
            3,
            0
        );
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A:C",
            "A=TEXT;B=TEXT;ZZ=TEXT",
            "Invalid column(s) ZZ are not within A:C"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A:C",
            "A=text UP;B=text DOWN;C=text UP"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A:C",
            "1=text;2=text"
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

    // containsAll......................................................................................................

    @Test
    public void testContainsAll() {
        this.containsAllAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetViewportWindows.parse("A1"),
            false
        );
    }

    // testCelll........................................................................................................

    @Test
    public void testTestCellBefore() {
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
            SpreadsheetSelection.parseCell("B1"),
            true
        );
    }

    @Test
    public void testTestCellRight() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.parseCell("D2"),
            true
        );
    }

    @Test
    public void testTestCellAfter() {
        this.testCellAndCheck(
            this.createSelection(),
            SpreadsheetSelection.parseCell("E1"),
            false
        );
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

    // setRowRange......................................................................................................

    @Test
    public void testSetRowRangeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSelection()
                .setRowRange(null)
        );
    }

    @Test
    public void testSetRowRange1() {
        this.setRowRangeAndCheck("B:D", "2:4", "B2:D4");
    }

    @Test
    public void testSetRowRange2() {
        this.setRowRangeAndCheck("B", "2", "B2");
    }

    @Test
    public void testSetRowRange3() {
        this.setRowRangeAndCheck("B:D", "2", "B2:D2");
    }

    private void setRowRangeAndCheck(final String column,
                                     final String row,
                                     final String range) {
        this.checkEquals(
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseColumnRange(column).setRowRange(SpreadsheetSelection.parseRowRange(row)),
            () -> column + " setRowRange " + row
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

    // cellColumnOrRowText..............................................................................................

    @Test
    public void testCellColumnOrRow() {
        this.cellColumnOrRowTextAndCheck("column");
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
    public void testLeftColumnAnchorLeft() {
        this.leftColumnAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testLeftColumnAnchorRight() {
        this.leftColumnAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A"
        );
    }

    @Test
    public void testLeftColumnFirstAnchorLeft() {
        this.leftColumnAndCheck(
            "A:C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testLeftColumnFirstAnchorRight() {
        this.leftColumnAndCheck(
            "A:C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A"
        );
    }

    @Test
    public void testLeftColumnSkipsHidden() {
        this.leftColumnAndCheck(
            "D:E",
            SpreadsheetViewportAnchor.RIGHT,
            "C",
            "",
            "B"
        );
    }

    @Test
    public void testLeftPixels() {
        this.leftPixelsAndCheck(
            "D:E",
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
    public void testUpRowAnchorLeft() {
        final String range = "B:C";

        this.upRowAndCheck(
            range,
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range
        );
    }

    @Test
    public void testUpRowHiddenColumn() {
        this.upRowAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.COLUMN,
            "C",
            NO_HIDDEN_ROWS,
            ""
        );
    }

    @Test
    public void testUpPixels() {
        this.upPixelsAndCheck(
            "D:E",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "D:E"
        );
    }

    @Test
    public void testRightColumnAnchorLeft() {
        this.rightColumnAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D"
        );
    }

    @Test
    public void testRightColumnAnchorRight() {
        this.rightColumnAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C"
        );
    }

    @Test
    public void testRightColumnFirstAnchorLeft() {
        this.rightColumnAndCheck(
            "A:C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D"
        );
    }

    @Test
    public void testRightColumnFirstAnchorRight() {
        this.rightColumnAndCheck(
            "A:C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B"
        );
    }

    @Test
    public void testRightColumnSkipsHidden() {
        this.rightColumnAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            "E",
            NO_HIDDEN_ROWS,
            "F"
        );
    }

    @Test
    public void testRightPixels() {
        this.rightPixelsAndCheck(
            "D:E",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "F",
            Maps.of("E", 5.0, "G", 50.0, "H", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "H"
        );
    }

    @Test
    public void testDownRowAnchorLeft() {
        final String range = "B:C";

        this.downRowAndCheck(
            range,
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range
        );
    }

    @Test
    public void testDownRowHiddenColumn() {
        this.downRowAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.COLUMN,
            "C",
            NO_HIDDEN_ROWS,
            ""
        );
    }

    @Test
    public void testDownPixels() {
        this.downPixelsAndCheck(
            "A:B",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "C",
            Maps.of("D", 5.0, "E", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "A:B"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRangeLeft() {
        this.extendRangeAndCheck(
            "B:C",
            "D",
            SpreadsheetViewportAnchor.LEFT,
            "B:D"
        );
    }

    @Test
    public void testExtendRangeLeftLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRangeAndCheck(
            "Z:" + last,
            "" + last,
            SpreadsheetViewportAnchor.LEFT,
            "Z:" + last
        );
    }

    @Test
    public void testExtendRangeRightFirstColumn() {
        this.extendRangeAndCheck(
            "B:C",
            "A",
            SpreadsheetViewportAnchor.RIGHT,
            "A:C"
        );
    }

    @Test
    public void testExtendRangeRight() {
        this.extendRangeAndCheck(
            "C:D",
            "B",
            SpreadsheetViewportAnchor.RIGHT,
            "B:D"
        );
    }

    @Test
    public void testExtendRangeLeftSame() {
        this.extendRangeAndCheck(
            "A:C",
            "C",
            SpreadsheetViewportAnchor.LEFT,
            "A:C"
        );
    }

    @Test
    public void testExtendRangeRightSame() {
        this.extendRangeAndCheck(
            "A:C",
            "A",
            SpreadsheetViewportAnchor.RIGHT,
            "A:C"
        );
    }

    @Override
    SpreadsheetColumnRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseColumnRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeftColumnAnchorRight() {
        this.extendLeftColumnAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnAnchorLeft() {
        this.extendLeftColumnAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendLeftColumnAnchorRightFirstColumn() {
        final String range = "A:B";

        this.extendLeftColumnAndCheck(
            range,
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            range,
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "E:F",
            SpreadsheetViewportAnchor.RIGHT,
            50,
            "D",
            Maps.of("C", 50.0, "B", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:F",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRightColumnAnchorLeft() {
        this.extendRightColumnAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightColumnAnchorRight() {
        this.extendRightColumnAndCheck(
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "D",
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendRightColumnLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.extendRightColumnAndCheck(
            column.add(-1).columnRange(column).toString(),
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column.toString(),
            SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testExtendLeftColumnSingleLeft() {
        this.extendLeftColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnSingleRight() {
        this.extendLeftColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendRightColumnSingleLeft() {
        this.extendRightColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.LEFT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightColumnSingleRight() {
        this.extendRightColumnAndCheck(
            "C",
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "D",
            Maps.of("E", 50.0, "F", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:F",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testExtendUpRow() {
        final String row = "B:C";

        this.extendUpRowAndCheck(
            row,
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.RIGHT,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendDownRow() {
        final String row = "B:C";

        this.extendDownRowAndCheck(
            row,
            SpreadsheetViewportAnchor.RIGHT,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            row,
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "B:C",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "",
            Maps.empty(),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B:C",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocusedLeft() {
        this.focusedAndCheck(
            "A:B",
            SpreadsheetViewportAnchor.LEFT,
            "B"
        );
    }

    @Test
    public void testFocusedRight() {
        this.focusedAndCheck(
            "$C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "$C"
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnRangeReference selection = this.createSelection();

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
            protected void visit(final SpreadsheetColumnRangeReference s) {
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
            "A:B",
            SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testToScalar2() {
        this.toScalarAndCheck(
            "$A:B",
            SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testToScalar3() {
        this.toScalarAndCheck(
            "B:C",
            SpreadsheetSelection.parseColumn("B")
        );
    }

    // toCell...........................................................................................................

    @Test
    public void testToCell() {
        this.toCellAndCheck(
            "A:B",
            "A1"
        );
    }

    @Test
    public void testToCell2() {
        this.toCellAndCheck(
            "B:C",
            "B1"
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
        this.isUnitAndCheck(
            "A:A",
            true
        );
    }

    @Test
    public void testIsSingleDifferentReferenceKindTrue() {
        this.isUnitAndCheck(
            "A:$A",
            true
        );
    }

    @Test
    public void testIsSingleFalse() {
        this.isUnitAndCheck(
            "A:$B",
            false
        );
    }

    // hasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment2() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B"),
            UrlFragment.parse("A:B")
        );
    }

    @Test
    public void testUrlFragmentAllColumns() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.ALL_COLUMNS,
            UrlFragment.parse("*")
        );
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumn() {
        this.toColumnAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumn("A")
        );
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRange() {
        final SpreadsheetColumnRangeReference range = SpreadsheetSelection.parseColumnRange("A:B");

        this.toColumnRangeAndCheck(
            range,
            range
        );
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRange() {
        final SpreadsheetColumnRangeReference selection = this.createSelection();
        this.toColumnOrColumnRangeAndCheck(
            selection,
            selection.toColumnRange()
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

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRangeFails() {
        this.toRowOrRowRangeFails();
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
    public void testEqualsDifferentColumn() {
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
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testCompareToEqualsDifferentKind() {
        this.compareToAndCheckEquals(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumnRange("$A:$B")
        );
    }

    @Test
    public void testCompareToEqualsLess() {
        this.compareToAndCheckLess(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testCompareToEqualsLessDifferentKind() {
        this.compareToAndCheckLess(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetSelection.parseColumnRange("$B:$C")
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

        this.toStringAndCheck(SpreadsheetColumnRangeReference.with(range), "B:D");
    }

    @Test
    public void testToStringAllColumns() {
        this.toStringAndCheck(
            SpreadsheetSelection.ALL_COLUMNS,
            "A:XFD"
        );
    }

    // toStringMaybeStar................................................................................................

    @Test
    public void testToStringMaybeStar() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testToStringMaybeStarAllColumns() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.ALL_COLUMNS,
            "*"
        );
    }

    @Override
    SpreadsheetColumnRangeReference createSelection() {
        final SpreadsheetColumnReference lower = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetColumnReference upper = SpreadsheetSelection.parseColumn("D");

        return SpreadsheetColumnRangeReference.with(
            Range.with(
                RangeBound.inclusive(lower),
                RangeBound.inclusive(upper)
            )
        );
    }

    @Override
    SpreadsheetColumnRangeReference createSelection(final Range<SpreadsheetColumnReference> range) {
        return SpreadsheetColumnRangeReference.with(range);
    }

    @Override
    public SpreadsheetColumnRangeReference parseString(final String text) {
        return SpreadsheetSelection.parseColumnRange(text);
    }

    @Override
    public SpreadsheetColumnRangeReference unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.unmarshallColumnRange(node, context);
    }

    @Override
    public Class<SpreadsheetColumnRangeReference> type() {
        return SpreadsheetColumnRangeReference.class;
    }

    // IterableTesting..................................................................................................

    @Override
    public SpreadsheetColumnRangeReference createIterable() {
        return SpreadsheetSelection.parseColumnRange("$B:$D");
    }
}
