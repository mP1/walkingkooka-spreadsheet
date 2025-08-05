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

package walkingkooka.spreadsheet.viewport;

import org.junit.jupiter.api.Test;
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportWindowsTest implements CanBeEmptyTesting,
    ClassTesting<SpreadsheetViewportWindows>,
    HashCodeEqualsDefinedTesting2<SpreadsheetViewportWindows>,
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
        final Set<SpreadsheetCellRangeReference> cellRanges = Sets.of(
            SpreadsheetSelection.parseCellRange("A1:C3")
        );
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
            cellRanges,
            windows.cellRanges()
        );

        this.isEmptyAndCheck(
            windows,
            false
        );
    }

    @Test
    public void testWithEmpty() {
        final Set<SpreadsheetCellRangeReference> cellRanges = Sets.empty();
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.with(cellRanges);
        this.checkEquals(
            cellRanges,
            windows.cellRanges()
        );

        this.isEmptyAndCheck(
            windows,
            true
        );
        assertSame(
            SpreadsheetViewportWindows.EMPTY,
            windows
        );
    }

    // cellRanges.......................................................................................................

    @Test
    public void testCellRangesWith() {
        final Set<SpreadsheetCellRangeReference> set = Sets.hash();
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

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
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
                .toArray(SpreadsheetCellRangeReference[]::new)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final SpreadsheetCellRangeReference... window) {
        return this.parseStringAndCheck(
            text,
            Sets.of(window)
        );
    }

    private SpreadsheetViewportWindows parseStringAndCheck(final String text,
                                                           final Set<SpreadsheetCellRangeReference> window) {
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
                              final Optional<SpreadsheetCellRangeReference> last) {
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
                                final Optional<SpreadsheetCellRangeReference> bounds) {
        this.checkEquals(
            bounds,
            windows.bounds(),
            () -> windows + " bounds"
        );
    }

    // Predicate........................................................................................................

    @Test
    public void testTestWithNullFalse() {
        this.testFalse(
            SpreadsheetViewportWindows.parse(""),
            null
        );
    }

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
    public void testTestNotEmptyWindowCellRangeInside() {
        this.testTrue(
            SpreadsheetViewportWindows.parse("A1:D4"),
            SpreadsheetSelection.parseCellRange("B2:C3")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellRangeOutside() {
        this.testFalse(
            SpreadsheetViewportWindows.parse("A1:B2"),
            SpreadsheetSelection.parseCellRange("C3:D4")
        );
    }

    @Test
    public void testTestNotEmptyWindowCellRangeInsideAndOutside() {
        this.testTrue(
            SpreadsheetViewportWindows.parse("B2:D4"),
            SpreadsheetSelection.parseCellRange("A1:C3")
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

    // containsAll......................................................................................................

    @Test
    public void testContainsAllAllOutside() {
        this.containsAllAndCheck(
            "A1:B2",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllTopLeftOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "A1:B2",
            false
        );
    }

    @Test
    public void testContainsAllBottomRightOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllBottomRightOutsideMultipleRanges() {
        this.containsAllAndCheck(
            "B2:C3,Z9:Z10",
            "C3:D4",
            false
        );
    }

    @Test
    public void testContainsAllSame() {
        this.containsAllAndCheck(
            "B2:C3",
            "B2:C3",
            true
        );
    }

    @Test
    public void testContainsAllInside() {
        this.containsAllAndCheck(
            "B2:E5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllInside2() {
        this.containsAllAndCheck(
            "B2:E5",
            "C3",
            true
        );
    }

    @Test
    public void testContainsAllInside3() {
        this.containsAllAndCheck(
            "*",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllInsideMultipleRanges() {
        this.containsAllAndCheck(
            "A1,B2:E5",
            "C3:D4",
            true
        );
    }

    @Test
    public void testContainsAllAndSomeOutside() {
        this.containsAllAndCheck(
            "B2:C3",
            "A1:D4",
            false
        );
    }

    @Test
    public void testContainsAllAndSomeOutside2() {
        this.containsAllAndCheck(
            "B2:C3",
            "*",
            false
        );
    }

    private void containsAllAndCheck(final String windows,
                                     final String test,
                                     final boolean expected) {
        this.containsAllAndCheck(
            SpreadsheetViewportWindows.parse(windows),
            SpreadsheetSelection.parseCellRange(test),
            expected
        );
    }

    private void containsAllAndCheck(final SpreadsheetViewportWindows windows,
                                     final SpreadsheetCellRangeReference test,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            windows.containsAll(test),
            () -> windows + " contains " + test
        );
    }

    // count............................................................................................................

    @Test
    public void testCountEmpty() {
        this.countAndCheck(
            "",
            SpreadsheetSelection.ALL_CELLS.count()
        );
    }

    @Test
    public void testCountStar() {
        this.countAndCheck(
            "*",
            SpreadsheetSelection.ALL_CELLS.count()
        );
    }

    @Test
    public void testCountA1() {
        this.countAndCheck(
            "A1",
            1
        );
    }

    @Test
    public void testCountA1B2C3() {
        this.countAndCheck(
            "A1,B2,C3",
            3
        );
    }

    @Test
    public void testCountA1toB2andC3toE5() {
        this.countAndCheck(
            "A1:B2,C3:E5",
            4 + 9
        );
    }

    private void countAndCheck(final String windows,
                               final long expected) {
        this.countAndCheck(
            SpreadsheetViewportWindows.parse(windows),
            expected
        );
    }

    private void countAndCheck(final SpreadsheetViewportWindows windows,
                               final long expected) {
        this.checkEquals(
            expected,
            windows.count(),
            () -> "count " + windows
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

    // cellss..........................................................................................................

    @Test
    public void testCellsEmpty() {
        this.cellsAndCheck(
            SpreadsheetViewportWindows.EMPTY
        );
    }

    @Test
    public void testCellsOneRange() {
        this.cellsAndCheck(
            "A1:B2",
            "A1",
            "B1",
            "A2",
            "B2"
        );
    }

    @Test
    public void testCellsSeveralRanges() {
        this.cellsAndCheck(
            "A1:B2,C1:D2",
            "A1",
            "B1",
            "C1",
            "D1",
            "A2",
            "B2",
            "C2",
            "D2"
        );
    }

    // A1    C1 D1
    //       C2 D2
    // A3 B3 C3 D3
    // A4 B4 C4 D4
    //
    @Test
    public void testCellsSeveralRanges2() {
        this.cellsAndCheck(
            "A1,A3:B4,C1:D4",
            "A1",
            "C1",
            "D1",
            "C2",
            "D2",
            "A3",
            "B3",
            "C3",
            "D3",
            "A4",
            "B4",
            "C4",
            "D4"
        );
    }

    @Test
    public void testCellsCached() {
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.parse("A1:B2,C3:D4");
        assertSame(
            windows.cells(),
            windows.cells()
        );
    }

    private void cellsAndCheck(final String windows,
                               final String... cells) {
        this.cellsAndCheck(
            SpreadsheetViewportWindows.parse(windows),
            Arrays.stream(cells)
                .map(SpreadsheetSelection::parseCell)
                .toArray(SpreadsheetCellReference[]::new)
        );
    }

    private void cellsAndCheck(final SpreadsheetViewportWindows windows,
                               final SpreadsheetCellReference... cells) {
        this.cellsAndCheck(
            windows,
            Lists.of(cells)
        );
    }

    private void cellsAndCheck(final SpreadsheetViewportWindows windows,
                               final List<SpreadsheetCellReference> cells) {
        this.checkEquals(
            cells,
            windows.cells(),
            windows::toString
        );
    }

    // columns..........................................................................................................

    @Test
    public void testColumnEmpty() {
        this.columnsAndCheck(
            SpreadsheetViewportWindows.EMPTY
        );
    }

    @Test
    public void testColumnOneRange() {
        this.columnsAndCheck(
            "A1:B2",
            "A",
            "B"
        );
    }

    @Test
    public void testColumnSeveralRanges() {
        this.columnsAndCheck(
            "A1:B2,C1:D2",
            "A",
            "B",
            "C",
            "D"
        );
    }

    @Test
    public void testColumnSeveralRanges2() {
        this.columnsAndCheck(
            "A1:B2,A3:B4,C1:D4",
            "A",
            "B",
            "C",
            "D"
        );
    }

    @Test
    public void testColumnsCached() {
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.parse("A1:B2,C3:D4");
        assertSame(
            windows.columns(),
            windows.columns()
        );
    }

    private void columnsAndCheck(final String windows,
                                 final String... columns) {
        this.columnsAndCheck(
            SpreadsheetViewportWindows.parse(windows),
            Arrays.stream(columns)
                .map(SpreadsheetSelection::parseColumn)
                .toArray(SpreadsheetColumnReference[]::new)
        );
    }

    private void columnsAndCheck(final SpreadsheetViewportWindows windows,
                                 final SpreadsheetColumnReference... columns) {
        this.columnsAndCheck(
            windows,
            Lists.of(columns)
        );
    }

    private void columnsAndCheck(final SpreadsheetViewportWindows windows,
                                 final List<SpreadsheetColumnReference> columns) {
        this.checkEquals(
            columns,
            windows.columns(),
            windows::toString
        );
    }

    // rows..........................................................................................................

    @Test
    public void testRowEmpty() {
        this.rowsAndCheck(
            SpreadsheetViewportWindows.EMPTY
        );
    }

    @Test
    public void testRowOneRange() {
        this.rowsAndCheck(
            "A1:B2",
            "1",
            "2"
        );
    }

    @Test
    public void testRowSeveralRanges() {
        this.rowsAndCheck(
            "A1:B2,C3:D4",
            "1",
            "2",
            "3",
            "4"
        );
    }

    @Test
    public void testRowSeveralRanges2() {
        this.rowsAndCheck(
            "A1:B2,A3:B4,C1:D4",
            "1",
            "2",
            "3",
            "4"
        );
    }

    @Test
    public void testRowsCached() {
        final SpreadsheetViewportWindows windows = SpreadsheetViewportWindows.parse("A1:B2,C3:D4");
        assertSame(
            windows.rows(),
            windows.rows()
        );
    }

    private void rowsAndCheck(final String windows,
                              final String... rows) {
        this.rowsAndCheck(
            SpreadsheetViewportWindows.parse(windows),
            Arrays.stream(rows)
                .map(SpreadsheetSelection::parseRow)
                .toArray(SpreadsheetRowReference[]::new)
        );
    }

    private void rowsAndCheck(final SpreadsheetViewportWindows windows,
                              final SpreadsheetRowReference... rows) {
        this.rowsAndCheck(
            windows,
            Lists.of(rows)
        );
    }

    private void rowsAndCheck(final SpreadsheetViewportWindows windows,
                              final List<SpreadsheetRowReference> rows) {
        this.checkEquals(
            rows,
            windows.rows(),
            windows::toString
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
