

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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.validation.ValueType;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellStoreTesting<S extends SpreadsheetCellStore> extends SpreadsheetStoreTesting<S, SpreadsheetCellReference, SpreadsheetCell> {

    // loadCellRange....................................................................................................

    @Test
    default void testLoadCellRangeNullCellRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .loadCellRange(
                    null, // range
                    SpreadsheetCellRangeReferencePath.LRTD,
                    0, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testLoadCellRangeNullCellRangePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .loadCellRange(
                    SpreadsheetSelection.ALL_CELLS, // range
                    null, // path
                    0, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testLoadCellRangeNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .loadCellRange(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD,
                    -1, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testLoadCellRangeNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .loadCellRange(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD,
                    0, // offset
                    -1 // count
                )
        );
    }

    default void loadCellRangeAndCheck(final SpreadsheetCellStore store,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetCellRangeReferencePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetCell... cells) {
        this.loadCellRangeAndCheck(
            store,
            range,
            path,
            offset,
            count,
            Sets.of(
                cells
            )
        );
    }

    default void loadCellRangeAndCheck(final SpreadsheetCellStore store,
                                       final SpreadsheetCellRangeReference range,
                                       final SpreadsheetCellRangeReferencePath path,
                                       final int offset,
                                       final int count,
                                       final Set<SpreadsheetCell> cells) {
        this.checkEquals(
            cells,
            store.loadCellRange(
                range,
                path,
                offset,
                count
            ),
            () -> "loadCellRange " + range + " " + path + " " + offset + " " + count
        );
    }

    // deleteCells......................................................................................................

    @Test
    default void testDeleteCellsNullCellRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().deleteCells(null)
        );
    }


    @Test
    default void testColumnCountWhenEmpty() {
        final S store = this.createStore();

        this.columnCountAndCheck(
            store,
            0
        );
    }

    default void columnCountAndCheck(final SpreadsheetCellStore store,
                                     final int expected) {
        this.checkEquals(
            expected,
            store.columnCount(),
            () -> "columnCount for store=" + store
        );
    }

    @Test
    default void testRowCountWhenEmpty() {
        final S store = this.createStore();

        this.rowCountAndCheck(
            store,
            0
        );
    }

    default void rowCountAndCheck(final SpreadsheetCellStore store,
                                  final int expected) {
        this.checkEquals(
            expected,
            store.rowCount(),
            () -> "rowCount for store=" + store);
    }

    @Test
    default void testColumnNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().column(null)
        );
    }

    @Test
    default void testRowNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().row(null)
        );
    }

    @Test
    default void testMaxColumnWidthNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().maxColumnWidth(null)
        );
    }

    @Test
    default void testMaxRowHeightNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().maxRowHeight(null)
        );
    }

    // nextEmptyColumn.....................................................................................................

    default void nextEmptyColumnAndCheck(final SpreadsheetCellStore store,
                                         final SpreadsheetRowReference row) {
        this.nextEmptyColumnAndCheck(
            store,
            row,
            Optional.empty()
        );
    }

    default void nextEmptyColumnAndCheck(final SpreadsheetCellStore store,
                                         final SpreadsheetRowReference row,
                                         final SpreadsheetColumnReference expected) {
        this.nextEmptyColumnAndCheck(
            store,
            row,
            Optional.of(expected)
        );
    }

    default void nextEmptyColumnAndCheck(final SpreadsheetCellStore store,
                                         final SpreadsheetRowReference row,
                                         final Optional<SpreadsheetColumnReference> expected) {
        this.checkEquals(
            expected,
            store.nextEmptyColumn(row)
        );
    }

    // nextEmptyRow.....................................................................................................

    default void nextEmptyRowAndCheck(final SpreadsheetCellStore store,
                                      final SpreadsheetColumnReference column) {
        this.nextEmptyRowAndCheck(
            store,
            column,
            Optional.empty()
        );
    }

    default void nextEmptyRowAndCheck(final SpreadsheetCellStore store,
                                      final SpreadsheetColumnReference column,
                                      final SpreadsheetRowReference expected) {
        this.nextEmptyRowAndCheck(
            store,
            column,
            Optional.of(expected)
        );
    }

    default void nextEmptyRowAndCheck(final SpreadsheetCellStore store,
                                      final SpreadsheetColumnReference column,
                                      final Optional<SpreadsheetRowReference> expected) {
        this.checkEquals(
            expected,
            store.nextEmptyRow(column)
        );
    }

    // findCellsWithValueType...........................................................................................

    @Test
    default void testFindCellsWithValueTypeWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().findCellsWithValueType(
                null,
                SpreadsheetValueType.TEXT,
                1
            )
        );
    }

    @Test
    default void testFindCellsWithValueTypeWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().findCellsWithValueType(
                SpreadsheetSelection.ALL_CELLS,
                null,
                1
            )
        );
    }

    @Test
    default void testFindCellsWithValueTypeWithNegativeMaxFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore().findCellsWithValueType(
                SpreadsheetSelection.ALL_CELLS,
                SpreadsheetValueType.TEXT,
                -1
            )
        );
    }

    @Test
    default void testFindCellsWithValueTypeWithZero() {
        this.findCellsWithValueTypeAndCheck(
            this.createStore(),
            SpreadsheetSelection.ALL_CELLS,
            SpreadsheetValueType.TEXT,
            0
        );
    }

    default void findCellsWithValueTypeAndCheck(final SpreadsheetCellStore store,
                                                final SpreadsheetCellRangeReference cellRange,
                                                final ValueType valueType,
                                                final int max,
                                                final SpreadsheetCell... expected) {
        this.findCellsWithValueTypeAndCheck(
            store,
            cellRange,
            valueType,
            max,
            Sets.of(expected)
        );
    }

    default void findCellsWithValueTypeAndCheck(final SpreadsheetCellStore store,
                                                final SpreadsheetCellRangeReference cellRange,
                                                final ValueType valueType,
                                                final int max,
                                                final Set<SpreadsheetCell> expected) {
        this.checkEquals(
            expected,
            store.findCellsWithValueType(
                cellRange,
                valueType,
                max
            ),
            () -> "findCellsWithValueType " + cellRange + " " + valueType + " max=" + max
        );
    }

    // countCellsWithValueType.........................................................................................

    @Test
    default void testCountCellsWithValueTypeWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().countCellsWithValueType(
                null,
                SpreadsheetValueType.TEXT
            )
        );
    }

    @Test
    default void testCountCellsWithValueTypeWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore().countCellsWithValueType(
                SpreadsheetSelection.ALL_CELLS,
                null
            )
        );
    }

    default void countCellsWithValueTypeAndCheck(final SpreadsheetCellStore store,
                                                 final SpreadsheetCellRangeReference cellRange,
                                                 final ValueType valueType,
                                                 final int expected) {
        this.checkEquals(
            expected,
            store.countCellsWithValueType(
                cellRange,
                valueType
            ),
            () -> "countCellsWithValueType " + cellRange + " " + valueType
        );
    }
}
