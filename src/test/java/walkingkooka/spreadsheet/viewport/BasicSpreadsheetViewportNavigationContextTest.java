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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharacterConstant;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetViewportNavigationContextTest implements ClassTesting<BasicSpreadsheetViewportNavigationContext>,
    SpreadsheetViewportNavigationContextTesting<BasicSpreadsheetViewportNavigationContext>,
    ToStringTesting<BasicSpreadsheetViewportNavigationContext> {

    private final static Function<SpreadsheetColumnReference, Double> COLUMN_TO_WIDTH = (c) -> {
        throw new UnsupportedOperationException();
    };

    private final static Function<SpreadsheetRowReference, Double> ROW_TO_HEIGHT = (r) -> {
        throw new UnsupportedOperationException();
    };

    private final static Function<SpreadsheetViewport, SpreadsheetViewportWindows> WINDOWS_FUNCTION = (v) -> {
        throw new UnsupportedOperationException();
    };

    @Test
    public void testWithNullColumnHiddenFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetViewportNavigationContext
                .with(
                    null,
                    COLUMN_TO_WIDTH,
                    Predicates.fake(),
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
        );
    }

    @Test
    public void testWithNullColumnWidthFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetViewportNavigationContext
                .with(
                    Predicates.fake(),
                    null,
                    Predicates.fake(),
                    ROW_TO_HEIGHT,
                    WINDOWS_FUNCTION
                )
        );
    }

    @Test
    public void testWithNullRowHiddenFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(),
                COLUMN_TO_WIDTH,
                null,
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            )
        );
    }

    @Test
    public void testWithNullRowHeightsFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetViewportNavigationContext
                .with(
                    Predicates.fake(),
                    COLUMN_TO_WIDTH,
                    Predicates.fake(),
                    null,
                    WINDOWS_FUNCTION
                )
        );
    }

    @Test
    public void testWithNullWindowsFunctionFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetViewportNavigationContext
                .with(
                    Predicates.fake(),
                    COLUMN_TO_WIDTH,
                    Predicates.fake(),
                    ROW_TO_HEIGHT,
                    null
                )
        );
    }

    @Test
    public void testIsColumnHidden() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        final BasicSpreadsheetViewportNavigationContext context = BasicSpreadsheetViewportNavigationContext.with(
            Predicates.is(column),
            COLUMN_TO_WIDTH,
            Predicates.fake(),
            ROW_TO_HEIGHT,
            WINDOWS_FUNCTION
        );
        this.isColumnHiddenAndCheck(
            context,
            column,
            true
        );

        this.isColumnHiddenAndCheck(
            context,
            column.add(1),
            false
        );
    }

    @Test
    public void testIsRowHidden() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");

        final BasicSpreadsheetViewportNavigationContext context = BasicSpreadsheetViewportNavigationContext.with(
            Predicates.fake(),
            COLUMN_TO_WIDTH,
            Predicates.is(row),
            ROW_TO_HEIGHT,
            WINDOWS_FUNCTION
        );

        this.isRowHiddenAndCheck(
            context,
            row,
            true
        );

        this.isRowHiddenAndCheck(
            context,
            row.add(1),
            false
        );
    }

    // leftColumn.......................................................................................................

    @Test
    public void testLeftFirstColumnNotHidden() {
        this.moveLeftAndCheck(
            "",
            "A",
            "A"
        );
    }

    @Test
    public void testLeftFirstColumnHidden() {
        this.moveLeftAndCheck(
            "A",
            "A"
        );
    }

    @Test
    public void testLeftAllColumnsHiddenIncludingGiven() {
        this.moveLeftAndCheck(
            "A,B,C",
            "C"
        );
    }

    @Test
    public void testMoveLeft() {
        this.moveLeftAndCheck(
            "",
            "B",
            "A"
        );
    }

    @Test
    public void testMoveLeft2() {
        this.moveLeftAndCheck(
            "",
            "D",
            "C"
        );
    }

    @Test
    public void testMoveLeftFirstColumn() {
        this.moveLeftAndCheck(
            "",
            "A",
            "A"
        );
    }

    @Test
    public void testMoveLeftSkips() {
        this.moveLeftAndCheck(
            "C,D",
            "E",
            "B"
        );
    }

    @Test
    public void testMoveLeftSkipsFirstColumn() {
        this.moveLeftAndCheck(
            "B,C",
            "D",
            "A"
        );
    }

    @Test
    public void testLeftColumnAllLeftHidden() {
        this.moveLeftAndCheck(
            "A,B",
            "C",
            "C"
        );
    }

    private void moveLeftAndCheck(final String columnHidden,
                                  final String column) {
        this.moveLeftColumnAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                hiddenColumns(columnHidden),
                COLUMN_TO_WIDTH,
                Predicates.fake(),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            column
        );
    }

    private void moveLeftAndCheck(final String columnHidden,
                                  final String column,
                                  final String expected) {
        this.moveLeftColumnAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                hiddenColumns(columnHidden),
                COLUMN_TO_WIDTH,
                Predicates.fake(),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            column,
            expected
        );
    }

    // rightColumn......................................................................................................

    @Test
    public void testRightLastColumnHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.moveRightAndCheck(
            last.toString(),
            last.toString()
        );
    }

    @Test
    public void testRightAllColumnsHiddenIncludingGiven() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.moveRightAndCheck(
            last.add(-2) + "," + last.add(-1) + "," + last,
            last.add(-2).toString()
        );
    }

    @Test
    public void testMoveRight() {
        this.moveRightAndCheck(
            "B",
            "B",
            "C"
        );
    }

    @Test
    public void testMoveRight2() {
        this.moveRightAndCheck(
            "C",
            "B",
            "D"
        );
    }

    @Test
    public void testMoveRightSkips() {
        this.moveRightAndCheck(
            "B,C",
            "B",
            "D"
        );
    }

    @Test
    public void testMoveRight3() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.moveRightAndCheck(
            last.add(-2) + "," + last.add(-1),
            last.add(-3).toString(),
            last.toString()
        );
    }

    @Test
    public void testMoveRightColumnAllMoveRightHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.moveRightAndCheck(
            last.add(-1) + "," + last,
            last.add(-2).toString(),
            last.add(-2).toString()
        );
    }

    private void moveRightAndCheck(final String columnHidden,
                                   final String column) {
        this.moveRightColumnAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                hiddenColumns(columnHidden),
                COLUMN_TO_WIDTH,
                Predicates.fake(),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            column
        );
    }

    private void moveRightAndCheck(final String columnHidden,
                                   final String column,
                                   final String expected) {
        this.moveRightColumnAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                hiddenColumns(columnHidden),
                COLUMN_TO_WIDTH,
                Predicates.fake(),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            column,
            expected
        );
    }

    // upRow...................................................................................................

    @Test
    public void testUpFirstRowNotHidden() {
        this.moveUpAndCheck(
            "",
            "1",
            "1"
        );
    }

    @Test
    public void testUpFirstRowHidden() {
        this.moveUpAndCheck(
            "1",
            "1"
        );
    }

    @Test
    public void testUpAllRowsHiddenIncludingGiven() {
        this.moveUpAndCheck(
            "1,2,3",
            "3"
        );
    }

    @Test
    public void testMoveUp() {
        this.moveUpAndCheck(
            "",
            "2",
            "1"
        );
    }

    @Test
    public void testMoveUp2() {
        this.moveUpAndCheck(
            "",
            "4",
            "3"
        );
    }

    @Test
    public void testMoveUp3() {
        this.moveUpAndCheck(
            "",
            "1",
            "1"
        );
    }

    @Test
    public void testMoveUpSkips() {
        this.moveUpAndCheck(
            "3,4",
            "5",
            "2"
        );
    }

    @Test
    public void testMoveUp4() {
        this.moveUpAndCheck(
            "2,3",
            "4",
            "1"
        );
    }

    @Test
    public void testMoveUpRowAllUpHidden() {
        this.moveUpAndCheck(
            "1,2",
            "3",
            "3"
        );
    }

    private void moveUpAndCheck(final String rowHidden,
                                final String row) {
        this.moveUpRowAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(),
                COLUMN_TO_WIDTH,
                this.hiddenRows(rowHidden),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            row
        );
    }

    private void moveUpAndCheck(final String rowHidden,
                                final String row,
                                final String expected) {
        this.moveUpRowAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(),
                COLUMN_TO_WIDTH,
                this.hiddenRows(rowHidden),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            row,
            expected
        );
    }

    // downRow..........................................................................................................

    @Test
    public void testDownLastRowHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
            last.toString(),
            last.toString()
        );
    }

    @Test
    public void testDownAllRowsHiddenIncludingGiven() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
            last.add(-2) + "," + last.add(-1) + "," + last,
            last.add(-2).toString()
        );
    }

    @Test
    public void testDownRow() {
        this.downRowAndCheck(
            "2",
            "2",
            "3"
        );
    }

    @Test
    public void testDownRow2() {
        this.downRowAndCheck(
            "3",
            "2",
            "4"
        );
    }

    @Test
    public void testDownRowSkips() {
        this.downRowAndCheck(
            "2,3",
            "2",
            "4"
        );
    }

    @Test
    public void testDownRowSkipsLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
            last.add(-2) + "," + last.add(-1),
            last.add(-3).toString(),
            last.toString()
        );
    }

    @Test
    public void testDownRowAllDownHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
            last.add(-1) + "," + last,
            last.add(-2).toString(),
            last.add(-2).toString()
        );
    }

    private void downRowAndCheck(final String rowHidden,
                                 final String row) {
        this.moveDownRowAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(),
                COLUMN_TO_WIDTH,
                hiddenRows(rowHidden),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            row
        );
    }

    // leftPixels.......................................................................................................

    @Test
    public void testLeftPixelsFirst() {
        this.leftPixelsAndCheck(
            "A",
            1,
            "",
            Maps.empty(),
            "A"
        );
    }

    @Test
    public void testLeftPixelsHiddenFirst() {
        this.leftPixelsAndCheck(
            "A",
            1,
            "A",
            Maps.empty()

        );
    }

    @Test
    public void testLeftPixelsHiddenFirst2() {
        this.leftPixelsAndCheck(
            "B",
            100,
            "A",
            Maps.of("B", 50.0),
            "B"
        );
    }

    @Test
    public void testLeftPixelsNotFirst() {
        this.leftPixelsAndCheck(
            "B",
            10,
            "",
            Maps.of("A", 50.0, "B", 50.0),
            "A"
        );
    }

    @Test
    public void testLeftPixelsNotFirst2() {
        this.leftPixelsAndCheck(
            "D",
            99,
            "",
            Maps.of("B", 40.0, "C", 60.0, "D", 100.0),
            "B"
        );
    }

    @Test
    public void testLeftPixelsNotFirst3() {
        this.leftPixelsAndCheck(
            "D",
            101,
            "",
            Maps.of("A", 50.0, "B", 40.0, "C", 60.0),
            "A"
        );
    }

    @Test
    public void testLeftPixelsNotFirstSkipHidden() {
        this.leftPixelsAndCheck(
            "D",
            101,
            "C",
            Maps.of("A", 50.0, "B", 40.0, "C", 50.0, "D", 60.0),
            "A"
        );
    }

    private void leftPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String columnsHidden,
                                    final Map<String, Double> columnWidths) {
        this.leftPixelsAndCheck(
            start,
            pixels,
            columnsHidden,
            columnWidths,
            Optional.empty()
        );
    }

    private void leftPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String columnsHidden,
                                    final Map<String, Double> columnWidths,
                                    final String expected) {
        this.leftPixelsAndCheck(
            start,
            pixels,
            columnsHidden,
            columnWidths,
            Optional.of(expected)
        );
    }

    private void leftPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String columnsHidden,
                                    final Map<String, Double> columnWidths,
                                    final Optional<String> expected) {
        this.leftPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            this.hiddenColumns(columnsHidden),
            this.columnToWidth(columnWidths),
            expected.map(SpreadsheetSelection::parseColumn)
        );
    }

    private void leftPixelsAndCheck(final SpreadsheetColumnReference start,
                                    final int pixels,
                                    final Predicate<SpreadsheetColumnReference> columnsHidden,
                                    final Function<SpreadsheetColumnReference, Double> columnWidths,
                                    final Optional<SpreadsheetColumnReference> expected) {
        this.moveLeftPixelsAndCheck(
            start,
            pixels,
            BasicSpreadsheetViewportNavigationContext.with(
                columnsHidden,
                columnWidths::apply,
                Predicates.fake(), // rows hidden
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            expected
        );
    }

    // rightPixels........................................................................................................

    private final static String LAST_COLUMN = SpreadsheetReferenceKind.RELATIVE.lastColumn().toString();

    private final static String LAST_COLUMN_2 = SpreadsheetReferenceKind.RELATIVE.lastColumn().add(-1).toString();

    private final static String LAST_COLUMN_3 = SpreadsheetReferenceKind.RELATIVE.lastColumn().add(-2).toString();

    private final static String LAST_COLUMN_4 = SpreadsheetReferenceKind.RELATIVE.lastColumn().add(-3).toString();

    @Test
    public void testRightPixelsLast() {
        this.rightPixelsAndCheck(
            LAST_COLUMN,
            1,
            "",
            Maps.empty(),
            LAST_COLUMN
        );
    }

    @Test
    public void testRightPixelsHiddenLast() {
        this.rightPixelsAndCheck(
            LAST_COLUMN,
            1,
            LAST_COLUMN,
            Maps.empty()

        );
    }

    @Test
    public void testRightPixelsHiddenLast2() {
        this.rightPixelsAndCheck(
            LAST_COLUMN_2,
            100,
            LAST_COLUMN,
            Maps.of(LAST_COLUMN_2, 50.0),
            LAST_COLUMN_2
        );
    }

    @Test
    public void testRightPixelsNotLast() {
        this.rightPixelsAndCheck(
            LAST_COLUMN_2,
            10,
            "",
            Maps.of(LAST_COLUMN, 50.0, LAST_COLUMN_2, 50.0),
            LAST_COLUMN
        );
    }

    @Test
    public void testRightPixelsNotLast2() {
        this.rightPixelsAndCheck(
            LAST_COLUMN_4,
            99,
            "",
            Maps.of(LAST_COLUMN_2, 40.0, LAST_COLUMN_3, 60.0, LAST_COLUMN_4, 100.0),
            LAST_COLUMN_2
        );
    }

    @Test
    public void testRightPixelsNotLast3() {
        this.rightPixelsAndCheck(
            LAST_COLUMN_4,
            101,
            "",
            Maps.of(LAST_COLUMN, 50.0, LAST_COLUMN_2, 40.0, LAST_COLUMN_3, 60.0),
            LAST_COLUMN
        );
    }

    @Test
    public void testRightPixelsNotLastSkipHidden() {
        this.rightPixelsAndCheck(
            LAST_COLUMN_4,
            101,
            LAST_COLUMN_3,
            Maps.of(LAST_COLUMN, 50.0, LAST_COLUMN_2, 40.0, LAST_COLUMN_3, 50.0, LAST_COLUMN_4, 60.0),
            LAST_COLUMN
        );
    }

    private void rightPixelsAndCheck(final String start,
                                     final int pixels,
                                     final String columnsHidden,
                                     final Map<String, Double> columnWidths) {
        this.rightPixelsAndCheck(
            start,
            pixels,
            columnsHidden,
            columnWidths,
            Optional.empty()
        );
    }

    private void rightPixelsAndCheck(final String start,
                                     final int pixels,
                                     final String columnsHidden,
                                     final Map<String, Double> columnWidths,
                                     final String expected) {
        this.rightPixelsAndCheck(
            start,
            pixels,
            columnsHidden,
            columnWidths,
            Optional.of(expected)
        );
    }

    private void rightPixelsAndCheck(final String start,
                                     final int pixels,
                                     final String columnsHidden,
                                     final Map<String, Double> columnWidths,
                                     final Optional<String> expected) {
        this.rightPixelsAndCheck(
            SpreadsheetSelection.parseColumn(start),
            pixels,
            this.hiddenColumns(columnsHidden),
            this.columnToWidth(columnWidths),
            expected.map(SpreadsheetSelection::parseColumn)
        );
    }

    private void rightPixelsAndCheck(final SpreadsheetColumnReference start,
                                     final int pixels,
                                     final Predicate<SpreadsheetColumnReference> columnsHidden,
                                     final Function<SpreadsheetColumnReference, Double> columnWidths,
                                     final Optional<SpreadsheetColumnReference> expected) {
        this.moveRightPixelsAndCheck(
            start,
            pixels,
            BasicSpreadsheetViewportNavigationContext.with(
                columnsHidden,
                columnWidths::apply,
                Predicates.fake(), // rows hidden
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            expected
        );
    }

    // upPixels........................................................................................................

    @Test
    public void testUpPixelsFirst() {
        this.upPixelsAndCheck(
            "1",
            1,
            "",
            Maps.empty(),
            "1"
        );
    }

    @Test
    public void testUpPixelsHiddenFirst() {
        this.upPixelsAndCheck(
            "1",
            1,
            "1",
            Maps.empty()

        );
    }

    @Test
    public void testUpPixelsHiddenFirst2() {
        this.upPixelsAndCheck(
            "2",
            100,
            "1",
            Maps.of("2", 50.0),
            "2"
        );
    }

    @Test
    public void testUpPixelsNotFirst() {
        this.upPixelsAndCheck(
            "2",
            10,
            "",
            Maps.of("1", 50.0, "2", 50.0),
            "1"
        );
    }

    @Test
    public void testUpPixelsNotFirst2() {
        this.upPixelsAndCheck(
            "4",
            99,
            "",
            Maps.of("2", 40.0, "3", 60.0, "4", 100.0),
            "2"
        );
    }

    @Test
    public void testUpPixelsNotFirst3() {
        this.upPixelsAndCheck(
            "4",
            101,
            "",
            Maps.of("1", 50.0, "2", 40.0, "3", 60.0),
            "1"
        );
    }

    @Test
    public void testUpPixelsNotFirstSkipHidden() {
        this.upPixelsAndCheck(
            "4",
            101,
            "3",
            Maps.of("1", 50.0, "2", 40.0, "3", 50.0, "4", 60.0),
            "1"
        );
    }

    private void upPixelsAndCheck(final String start,
                                  final int pixels,
                                  final String rowsHidden,
                                  final Map<String, Double> rowHeights) {
        this.upPixelsAndCheck(
            start,
            pixels,
            rowsHidden,
            rowHeights,
            Optional.empty()
        );
    }

    private void upPixelsAndCheck(final String start,
                                  final int pixels,
                                  final String rowsHidden,
                                  final Map<String, Double> rowHeights,
                                  final String expected) {
        this.upPixelsAndCheck(
            start,
            pixels,
            rowsHidden,
            rowHeights,
            Optional.of(expected)
        );
    }

    private void upPixelsAndCheck(final String start,
                                  final int pixels,
                                  final String rowsHidden,
                                  final Map<String, Double> rowHeights,
                                  final Optional<String> expected) {
        this.upPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            this.hiddenRows(rowsHidden),
            this.rowToHeight(rowHeights),
            expected.map(SpreadsheetSelection::parseRow)
        );
    }

    private void upPixelsAndCheck(final SpreadsheetRowReference start,
                                  final int pixels,
                                  final Predicate<SpreadsheetRowReference> rowsHidden,
                                  final Function<SpreadsheetRowReference, Double> rowHeights,
                                  final Optional<SpreadsheetRowReference> expected) {
        this.moveUpPixelsAndCheck(
            start,
            pixels,
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(), // columns hidden
                COLUMN_TO_WIDTH,
                rowsHidden,
                rowHeights::apply,
                WINDOWS_FUNCTION
            ),
            expected
        );
    }

    // downPixels........................................................................................................

    private final static String LAST_ROW = SpreadsheetReferenceKind.RELATIVE.lastRow().toString();

    private final static String LAST_ROW_2 = SpreadsheetReferenceKind.RELATIVE.lastRow().add(-1).toString();

    private final static String LAST_ROW_3 = SpreadsheetReferenceKind.RELATIVE.lastRow().add(-2).toString();

    private final static String LAST_ROW_4 = SpreadsheetReferenceKind.RELATIVE.lastRow().add(-3).toString();

    @Test
    public void testDownPixelsLast() {
        this.downPixelsAndCheck(
            LAST_ROW,
            1,
            "",
            Maps.empty(),
            LAST_ROW
        );
    }

    @Test
    public void testDownPixelsHiddenLast() {
        this.downPixelsAndCheck(
            LAST_ROW,
            1,
            LAST_ROW,
            Maps.empty()

        );
    }

    @Test
    public void testDownPixelsHiddenLast2() {
        this.downPixelsAndCheck(
            LAST_ROW_2,
            100,
            LAST_ROW,
            Maps.of(LAST_ROW_2, 50.0),
            LAST_ROW_2
        );
    }

    @Test
    public void testDownPixelsNotLast() {
        this.downPixelsAndCheck(
            LAST_ROW_2,
            10,
            "",
            Maps.of(LAST_ROW, 50.0, LAST_ROW_2, 50.0),
            LAST_ROW
        );
    }

    @Test
    public void testDownPixelsNotLast2() {
        this.downPixelsAndCheck(
            LAST_ROW_4,
            99,
            "",
            Maps.of(LAST_ROW_2, 40.0, LAST_ROW_3, 60.0, LAST_ROW_4, 100.0),
            LAST_ROW_2
        );
    }

    @Test
    public void testDownPixelsNotLast3() {
        this.downPixelsAndCheck(
            LAST_ROW_4,
            101,
            "",
            Maps.of(LAST_ROW, 50.0, LAST_ROW_2, 40.0, LAST_ROW_3, 60.0),
            LAST_ROW
        );
    }

    @Test
    public void testDownPixelsNotLastSkipHidden() {
        this.downPixelsAndCheck(
            LAST_ROW_4,
            101,
            LAST_ROW_3,
            Maps.of(LAST_ROW, 50.0, LAST_ROW_2, 40.0, LAST_ROW_3, 50.0, LAST_ROW_4, 60.0),
            LAST_ROW
        );
    }

    private void downRowAndCheck(final String rowHidden,
                                 final String row,
                                 final String expected) {
        this.moveDownRowAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(),
                COLUMN_TO_WIDTH,
                hiddenRows(rowHidden),
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            row,
            expected
        );
    }

    private void downPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String rowsHidden,
                                    final Map<String, Double> rowWidths) {
        this.downPixelsAndCheck(
            start,
            pixels,
            rowsHidden,
            rowWidths,
            Optional.empty()
        );
    }

    private void downPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String rowsHidden,
                                    final Map<String, Double> rowWidths,
                                    final String expected) {
        this.downPixelsAndCheck(
            start,
            pixels,
            rowsHidden,
            rowWidths,
            Optional.of(expected)
        );
    }

    private void downPixelsAndCheck(final String start,
                                    final int pixels,
                                    final String rowsHidden,
                                    final Map<String, Double> rowHeights,
                                    final Optional<String> expected) {
        this.downPixelsAndCheck(
            SpreadsheetSelection.parseRow(start),
            pixels,
            this.hiddenRows(rowsHidden),
            this.rowToHeight(rowHeights),
            expected.map(SpreadsheetSelection::parseRow)
        );
    }

    private void downPixelsAndCheck(final SpreadsheetRowReference start,
                                    final int pixels,
                                    final Predicate<SpreadsheetRowReference> rowsHidden,
                                    final Function<SpreadsheetRowReference, Double> rowHeights,
                                    final Optional<SpreadsheetRowReference> expected) {
        this.downPixelsAndCheck(
            start,
            pixels,
            BasicSpreadsheetViewportNavigationContext.with(
                Predicates.fake(), // hidden columns
                COLUMN_TO_WIDTH,
                rowsHidden,
                rowHeights::apply,
                WINDOWS_FUNCTION
            ),
            expected
        );
    }

    // helpers.........................................................................................................

    private Predicate<SpreadsheetColumnReference> hiddenColumns(final String columns) {
        return hiddenPredicate(
            columns,
            SpreadsheetSelection::parseColumn
        );
    }

    private Predicate<SpreadsheetRowReference> hiddenRows(final String rows) {
        return hiddenPredicate(
            rows,
            SpreadsheetSelection::parseRow
        );
    }

    private static <T extends SpreadsheetSelection> Predicate<T> hiddenPredicate(final String columnOrRows,
                                                                                 final Function<String, T> parser) {
        return (columnOrRow) -> CharacterConstant.COMMA.parse(
            columnOrRows,
            parser
        ).contains(columnOrRow);
    }

    private Function<SpreadsheetColumnReference, Double> columnToWidth(final Map<String, Double> columnToWidths) {
        return columnOrRowToWidthOrHeight(
            columnToWidths,
            SpreadsheetSelection::parseColumn
        );
    }

    private Function<SpreadsheetRowReference, Double> rowToHeight(final Map<String, Double> rowToHeights) {
        return columnOrRowToWidthOrHeight(
            rowToHeights,
            SpreadsheetSelection::parseRow
        );
    }

    private <T extends SpreadsheetSelection> Function<T, Double> columnOrRowToWidthOrHeight(final Map<String, Double> columnOrRowToWidthOrHeight,
                                                                                            final Function<String, T> parser) {
        final Map<T, Double> map = columnOrRowToWidthOrHeight.entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    e -> parser.apply(e.getKey()),
                    Map.Entry::getValue
                )
            );
        return (columnOrRow) -> {
            final Double length = map.get(columnOrRow);
            this.checkNotEquals(
                null,
                length,
                () -> "Missing " + columnOrRow + " parse " + columnOrRowToWidthOrHeight
            );
            return length;
        };
    }

    // Object...........................................................................................................

    @Test
    public void testToString() {
        final Predicate<SpreadsheetColumnReference> column = Predicates.fake();
        final Predicate<SpreadsheetRowReference> row = Predicates.fake();

        this.toStringAndCheck(
            BasicSpreadsheetViewportNavigationContext.with(
                column,
                COLUMN_TO_WIDTH,
                row,
                ROW_TO_HEIGHT,
                WINDOWS_FUNCTION
            ),
            column + " " + COLUMN_TO_WIDTH + " " + row + " " + ROW_TO_HEIGHT + " " + WINDOWS_FUNCTION
        );
    }

    @Override
    public BasicSpreadsheetViewportNavigationContext createContext() {
        return BasicSpreadsheetViewportNavigationContext.with(
            Predicates.fake(),
            COLUMN_TO_WIDTH,
            Predicates.fake(),
            ROW_TO_HEIGHT,
            WINDOWS_FUNCTION
        );
    }

    @Override
    public Class<BasicSpreadsheetViewportNavigationContext> type() {
        return BasicSpreadsheetViewportNavigationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
