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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterable.IterableTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportWindowsTest implements ClassTesting<SpreadsheetViewportWindows>,
        HashCodeEqualsDefinedTesting2<SpreadsheetViewportWindows>,
        IterableTesting<SpreadsheetViewportWindows, SpreadsheetCellReference>,
        JsonNodeMarshallingTesting<SpreadsheetViewportWindows>,
        ParseStringTesting<SpreadsheetViewportWindows>,
        PredicateTesting,
        TreePrintableTesting,
        ToStringTesting<SpreadsheetViewportWindows> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportWindows.with(null)
        );
    }

    @Test
    public void testWithOverlappingRangesFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportWindows.parse("A1:B2,A1:C3")
        );

        this.checkEquals(
                "Window component cell-ranges overlap A1:B2 and A1:C3",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithOverlappingRangesFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportWindows.parse("A1:B2,C4,B2:C3")
        );

        this.checkEquals(
                "Window component cell-ranges overlap A1:B2 and B2:C3",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWithOverlappingRangesFails3() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportWindows.parse("A1:B2,C3:D4,E5,C3:C4")
        );

        this.checkEquals(
                "Window component cell-ranges overlap C3:C4 and C3:D4",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testWith() {
        final Set<SpreadsheetCellRange> cellRanges = Sets.of(
                SpreadsheetSelection.parseCellRange("A1:C3")
        );
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
                cellRanges,
                windows.cellRanges()
        );
        this.checkEquals(false, windows.isEmpty());
    }

    @Test
    public void testWithEmpty() {
        final Set<SpreadsheetCellRange> cellRanges = Sets.empty();
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
                cellRanges,
                windows.cellRanges()
        );
        this.checkEquals(true, windows.isEmpty());
        assertSame(
                SpreadsheetViewportWindows.EMPTY,
                windows
        );
    }

    // cellRanges.......................................................................................................

    @Test
    public void testCellRangesWith() {
        final Set<SpreadsheetCellRange> set = Sets.hash();
        set.add(SpreadsheetSelection.parseCellRange("A1"));

        this.cellRangesReadOnly(
                SpreadsheetViewportWindows.with(
                        set
                )
        );
    }

    @Test
    public void testCellRangesParse() {
        this.cellRangesReadOnly(
                SpreadsheetViewportWindows.parse("A1")
        );
    }

    private void cellRangesReadOnly(final SpreadsheetViewportWindows windows) {
        assertThrows(
                UnsupportedOperationException.class,
                () -> windows.cellRanges().add(null)
        );
    }

    // parse............................................................................................................

    public void testParseStringEmptyFails() {
        // nop
    }

    @Test
    public void testParseEmpty() {
        assertSame(
                SpreadsheetViewportWindows.EMPTY,
                this.parseStringAndCheck(
                        "",
                        Sets.empty()
                )
        );
    }

    @Test
    public void testParseOneCell() {
        this.parseStringAndCheck(
                "C1",
                SpreadsheetSelection.parseCellRange("C1")
        );
    }

    @Test
    public void testParseAllCells() {
        this.parseStringAndCheck(
                "*",
                "*",
                "*"
        );
    }

    @Test
    public void testParseMany() {
        this.parseStringAndCheck(
                "A1,B2:C3",
                "A1",
                "B2:C3"
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final String... windows) {
        return this.parseStringAndCheck(
                text,
                Arrays.stream(windows)
                        .map(SpreadsheetSelection::parseCellRange)
                        .toArray(SpreadsheetCellRange[]::new)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final SpreadsheetCellRange... window) {
        return this.parseStringAndCheck(
                text,
                Sets.of(window)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final Set<SpreadsheetCellRange> window) {
        return this.parseStringAndCheck(
                text,
                SpreadsheetViewportWindows.with(window)
        );
    }

    @Override
    public SpreadsheetViewportWindows parseString(final String windows) {
        return SpreadsheetViewportWindows.parse(windows);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // last.............................................................................................................

    @Test
    public void testLastWhenEmpty() {
        this.lastAndCheck(
                "",
                ""
        );
    }

    @Test
    public void testLastWhenOnly() {
        this.lastAndCheck(
                "A1:B2",
                "A1:B2"
        );
    }

    @Test
    public void testLastWhenTwo() {
        this.lastAndCheck(
                "A1:B2,C1:D2",
                "C1:D2"
        );
    }

    // A1 B1 C1
    // A2 B2 C2
    // A3 B3 C3
    @Test
    public void testLastWhenFour() {
        this.lastAndCheck(
                "A1,B1:C1,A2:A3,B2:C3",
                "B2:C3"
        );
    }

    private void lastAndCheck(final String windows,
                              final String last) {
        this.lastAndCheck(
                SpreadsheetViewportWindows.parse(windows),
                Optional.ofNullable(
                        last.isEmpty() ?
                                null :
                                SpreadsheetSelection.parseCellRange(last)
                )
        );
    }

    private void lastAndCheck(final SpreadsheetViewportWindows windows,
                              final Optional<SpreadsheetCellRange> last) {
        this.checkEquals(
                last,
                windows.last(),
                () -> "last of " + windows
        );
    }

    @Test
    public void testLastCached() {
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.parse("A1:B2");
        assertSame(
                windows.last(),
                windows.last()
        );
    }

    // home.............................................................................................................

    @Test
    public void testHomeWhenEmpty() {
        this.homeAndCheck(
                "",
                ""
        );
    }

    @Test
    public void testHomeWhenOnly() {
        this.homeAndCheck(
                "A1:B2",
                "A1"
        );
    }

    @Test
    public void testHomeWhenTwo() {
        this.homeAndCheck(
                "A1:B2,C1:D2",
                "C1"
        );
    }

    // A1 B1 C1
    // A2 B2 C2
    // A3 B3 C3
    @Test
    public void testHomeWhenFour() {
        this.homeAndCheck(
                "A1,B1:C1,A2:A3,B2:C3",
                "B2"
        );
    }

    private void homeAndCheck(final String windows,
                              final String home) {
        this.homeAndCheck(
                SpreadsheetViewportWindows.parse(windows),
                Optional.ofNullable(
                        home.isEmpty() ?
                                null :
                                SpreadsheetSelection.parseCell(home)
                )
        );
    }

    private void homeAndCheck(final SpreadsheetViewportWindows windows,
                              final Optional<SpreadsheetCellReference> home) {
        this.checkEquals(
                home,
                windows.home(),
                () -> "home of " + windows
        );
    }

    // bounds...........................................................................................................

    @Test
    public void testBoundsEmpty() {
        this.boundsAndCheck(
                SpreadsheetViewportWindows.EMPTY,
                Optional.empty()
        );
    }

    @Test
    public void testBoundsOneCellRange() {
        this.boundsAndCheck(
                "A1:B2",
                "A1:B2"
        );
    }

    @Test
    public void testBoundsOneCellRange2() {
        this.boundsAndCheck(
                "C3:D4",
                "C3:D4"
        );
    }

    @Test
    public void testBoundsTwoCellRange() {
        this.boundsAndCheck(
                "A1:B2,C3:D4",
                "A1:D4"
        );
    }

    @Test
    public void testBoundsTwoCellRange2() {
        this.boundsAndCheck(
                "E5:F5,G6:H7",
                "E5:H7"
        );
    }

    @Test
    public void testBoundsFourCells() {
        this.boundsAndCheck(
                "A1,B1:C1,A2:A5,B2:C5",
                "A1:C5"
        );
    }

    private void boundsAndCheck(final String windows,
                                final String bounds) {
        this.boundsAndCheck(
                SpreadsheetViewportWindows.parse(windows),
                Optional.ofNullable(
                        bounds.isEmpty() ?
                                null :
                                SpreadsheetSelection.parseCellRange(bounds)
                )
        );
    }

    private void boundsAndCheck(final SpreadsheetViewportWindows windows,
                                final Optional<SpreadsheetCellRange> bounds) {
        this.checkEquals(
                bounds,
                windows.bounds(),
                () -> windows + " bounds"
        );
    }

    // Iterable.........................................................................................................

    @Test
    public void testIterableAndIterateEmpty() {
        this.iterateAndCheck2(
                ""
        );
    }

    @Test
    public void testIterableAndIterateOneRange() {
        this.iterateAndCheck2(
                "A1",
                "A1"
        );
    }

    @Test
    public void testIterableAndIterateOneRange2() {
        this.iterateAndCheck2(
                "A1:A3",
                "A1",
                "A2",
                "A3"
        );
    }

    @Test
    public void testIterableAndIterateSeveralCellRanges() {
        this.iterateAndCheck2(
                "A1:A2,B1:B2",
                "A1",
                "A2",
                "B1",
                "B2"
        );
    }

    @Test
    public void testIterableAndIterateSeveralCellRanges2() {
        this.iterateAndCheck2(
                "A1:A2,B1:B2,C3",
                "A1",
                "A2",
                "B1",
                "B2",
                "C3"
        );
    }

    private void iterateAndCheck2(final String text,
                                  final String... cellReferences) {
        this.iterateAndCheck(
                SpreadsheetViewportWindows.parse(text)
                        .iterator(),
                Arrays.stream(cellReferences)
                        .map(SpreadsheetSelection::parseCell)
                        .toArray(SpreadsheetCellReference[]::new)
        );
    }

    @Override
    public SpreadsheetViewportWindows createIterable() {
        return this.createObject();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    // Predicate........................................................................................................

    @Test
    public void testTestEmptyWindow() {
        this.testTrue(
                SpreadsheetViewportWindows.parse(""),
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testTestNotEmptyWindowCellInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testTestNotEmptyWindowCellInside2() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2,C3:D4"),
                SpreadsheetSelection.parseCell("C4")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellOutside2() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2,C3:D4"),
                SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testTestNotEmptyWindowColumnInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testTestNotEmptyWindowColumnOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testTestNotEmptyWindowRowInside() {
        this.testTrue(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testTestNotEmptyWindowRowOutside() {
        this.testFalse(
                SpreadsheetViewportWindows.parse("A1:B2"),
                SpreadsheetSelection.parseRow("3")
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintEmpty() {
        this.treePrintAndCheck(
                SpreadsheetViewportWindows.EMPTY,
                "" + EOL
        );
    }

    @Test
    public void testTreePrintNotEmpty() {
        this.treePrintAndCheck(
                this.createObject(),
                "A1:B2" + EOL
        );
    }

    // cells............................................................................................................

    @Test
    public void testCellsWithNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject()
                        .cells(null)
        );
    }

    @Test
    public void testCellsWithLabelFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createObject()
                        .cells(
                                SpreadsheetSelection.labelName("Label123")
                        )
        );
    }

    // cells with cell..................................................................................................

    @Test
    public void testCellsWithCellOneWindow() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.A1,
                "A1"
        );
    }

    @Test
    public void testCellsWithCellOneWindow2() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.parseCell("B2"),
                "B2"
        );
    }

    @Test
    public void testCellsWithCellTwoWindows() {
        this.cellsAndCheck(
                "A1:B2,C3:D4",
                SpreadsheetSelection.parseCell("B2"),
                "B2"
        );
    }

    @Test
    public void testCellsWithCellTwoWindows2() {
        this.cellsAndCheck(
                "A1:B2,C3:D4",
                SpreadsheetSelection.parseCell("C3"),
                "C3"
        );
    }

    @Test
    public void testCellsWithCellThreeWindows() {
        this.cellsAndCheck(
                "A1:B2,C3:D4,E5:F6",
                SpreadsheetSelection.parseCell("F6"),
                "F6"
        );
    }

    // cells with cell range............................................................................................

    @Test
    public void testCellsWithCellRangeOneWindow() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.parseCellRange("A1:A2"),
                "A1",
                "A2"
        );
    }

    @Test
    public void testCellsWithCellRangeOneWindow2() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.parseCellRange("B1:C2"),
                "B1",
                "C1",
                "B2",
                "C2"
        );
    }

    @Test
    public void testCellsWithCellRangeTwoWindowsFirstWindow() {
        this.cellsAndCheck(
                "A1:B2,C3:D4",
                SpreadsheetSelection.parseCellRange("A1:B2"),
                "A1",
                "B1",
                "A2",
                "B2"
        );
    }

    @Test
    public void testCellsWithCellRangeTwoWindowsLastWindow() {
        this.cellsAndCheck(
                "A1:B2,C3:D4",
                SpreadsheetSelection.parseCellRange("C3:D4"),
                "C3",
                "D3",
                "C4",
                "D4"
        );
    }

    @Test
    public void testCellsWithCellRangeTwoWindowsBothWindow() {
        this.cellsAndCheck(
                "A1:B2,C3:D4",
                SpreadsheetSelection.parseCellRange("A1:D4"),
                "A1",
                "B1",
                "A2",
                "B2",
                "C3",
                "D3",
                "C4",
                "D4"
        );
    }

    @Test
    public void testCellsWithCellRangeThreeWindows() {
        this.cellsAndCheck(
                "A1:B2,C3:D4,E5:F6",
                SpreadsheetSelection.parseCellRange("A1:C3"),
                "A1",
                "B1",
                "A2",
                "B2",
                "C3"
        );
    }

    @Test
    public void testCellsWithCellRangeThreeWindows2() {
        this.cellsAndCheck(
                "A1:B2,C3:D4,E5:F6",
                SpreadsheetSelection.parseCellRange("A1:D4"),
                "A1",
                "B1",
                "A2",
                "B2",
                "C3",
                "D3",
                "C4",
                "D4"
        );
    }

    // column...........................................................................................................

    @Test
    public void testCellsWithColumnOneWindow() {
        this.cellsAndCheck(
                "A1:B2",
                SpreadsheetSelection.parseColumn("A"),
                "A1",
                "A2"
        );
    }

    @Test
    public void testCellsWithColumnOneWindow2() {
        this.cellsAndCheck(
                "A1:B2",
                SpreadsheetSelection.parseColumn("B"),
                "B1",
                "B2"
        );
    }

    @Test
    public void testCellsWithColumnTwoWindows() {
        this.cellsAndCheck(
                "A1:B2,A5:B6",
                SpreadsheetSelection.parseColumn("B"),
                "B1",
                "B2",
                "B5",
                "B6"
        );
    }

    // column range.....................................................................................................

    @Test
    public void testCellsWithColumnRangeOneWindow() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.parseColumnRange("A:B"),
                "A1",
                "B1",
                "A2",
                "B2",
                "A3",
                "B3"
        );
    }

    @Test
    public void testCellsWithColumnRangeTwoWindows() {
        this.cellsAndCheck(
                "A1:B2,A5:B6",
                SpreadsheetSelection.parseColumnRange("B:C"),
                "B1",
                "B2",
                "B5",
                "B6"
        );
    }

    // row...........................................................................................................

    @Test
    public void testCellsWithRowOneWindow() {
        this.cellsAndCheck(
                "A1:B2",
                SpreadsheetSelection.parseRow("1"),
                "A1",
                "B1"
        );
    }

    @Test
    public void testCellsWithRowOneWindow2() {
        this.cellsAndCheck(
                "A1:B2",
                SpreadsheetSelection.parseRow("2"),
                "A2",
                "B2"
        );
    }

    @Test
    public void testCellsWithRowTwoWindows() {
        this.cellsAndCheck(
                "A1:B2,D1:E2",
                SpreadsheetSelection.parseRow("2"),
                "A2",
                "B2",
                "D2",
                "E2"
        );
    }

    // row range.....................................................................................................

    @Test
    public void testCellsWithRowRangeOneWindow() {
        this.cellsAndCheck(
                "A1:C3",
                SpreadsheetSelection.parseRowRange("1:2"),
                "A1",
                "B1",
                "C1",
                "A2",
                "B2",
                "C2"
        );
    }

    @Test
    public void testCellsWithRowRangeTwoWindows() {
        this.cellsAndCheck(
                "A1:B2,D1:E4",
                SpreadsheetSelection.parseRowRange("2:3"),
                "A2",
                "B2",
                "D2",
                "E2",
                "D3",
                "E3"
        );
    }
    
    private void cellsAndCheck(final String windows,
                               final SpreadsheetSelection selection,
                               final String... cells) {
        this.cellsAndCheck(
                SpreadsheetViewportWindows.parse(windows),
                selection,
                Arrays.stream(cells)
                        .map(SpreadsheetSelection::parseCell)
                        .toArray(SpreadsheetCellReference[]::new)
        );
    }

    private void cellsAndCheck(final SpreadsheetViewportWindows windows,
                               final SpreadsheetSelection selection,
                               final SpreadsheetCellReference... cells) {
        this.iterateAndCheck(
                windows.cells(selection),
                cells
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                JsonNode.string("A1:B2")
        );
    }

    @Test
    public void testMarshallManyCellRanges() {
        final String string = "A1:B2,C3:D4,E5";

        this.marshallAndCheck(
                SpreadsheetViewportWindows.parse(string),
                JsonNode.string(string)
        );
    }

    @Override
    public SpreadsheetViewportWindows unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetViewportWindows.unmarshall(
                node,
                context
        );
    }

    @Override
    public SpreadsheetViewportWindows createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentCellRanges() {
        this.checkNotEquals(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.parseCellRange("A1:C3")
                        )
                )
        );
    }

    @Override
    public SpreadsheetViewportWindows createObject() {
        return SpreadsheetViewportWindows.with(
                Sets.of(
                        SpreadsheetSelection.parseCellRange("A1:B2")
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "A1:B2"
        );
    }

    @Test
    public void testToStringAllCells() {
        this.toStringAndCheck(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.ALL_CELLS
                        )
                ),
                "*"
        );
    }

    @Test
    public void testToStringSeveralRanges() {
        this.toStringAndCheck(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.parseCellRange("A1:B2"),
                                SpreadsheetSelection.parseCellRange("C3:D4")
                        )
                ),
                "A1:B2,C3:D4"
        );
    }

    // two testToString verify cell-ranges are sorted.

    @Test
    public void testToStringSeveralRanges2() {
        this.toStringAndCheck(
                SpreadsheetViewportWindows.with(
                        Sets.of(
                                SpreadsheetSelection.parseCellRange("C3:D4"),
                                SpreadsheetSelection.parseCellRange("A1:B2")
                        )
                ),
                "A1:B2,C3:D4"
        );
    }

    @Test
    public void testToStringSeveralRanges3() {
        this.toStringAndCheck(
                SpreadsheetViewportWindows.parse("C3:D4,A1:B2"),
                "A1:B2,C3:D4"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportWindows> type() {
        return SpreadsheetViewportWindows.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
