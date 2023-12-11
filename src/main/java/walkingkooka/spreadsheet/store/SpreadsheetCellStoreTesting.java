

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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellStoreTesting<S extends SpreadsheetCellStore> extends SpreadsheetStoreTesting<S, SpreadsheetCellReference, SpreadsheetCell> {

    // loadCells........................................................................................................

    @Test
    default void testLoadCellsNullCellRangeFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .loadCells(
                                null, // range
                                SpreadsheetCellRangePath.LRTD,
                                1
                        )
        );
    }

    @Test
    default void testLoadCellsNullCellRangePathFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .loadCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                null,
                                1
                        )
        );
    }

    @Test
    default void testLoadCellsNegativeMaxFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .loadCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangePath.LRTD,
                                -1
                        )
        );
    }

    default void loadCellsAndCheck(final SpreadsheetCellStore store,
                                   final SpreadsheetCellRange range,
                                   final SpreadsheetCellRangePath path,
                                   final int max,
                                   final SpreadsheetCell... cells) {
        this.loadCellsAndCheck(
                store,
                range,
                path,
                max,
                Sets.of(
                        cells
                )
        );
    }

    default void loadCellsAndCheck(final SpreadsheetCellStore store,
                                   final SpreadsheetCellRange range,
                                   final SpreadsheetCellRangePath path,
                                   final int max,
                                   final Set<SpreadsheetCell> cells) {
        this.checkEquals(
                cells,
                store.loadCells(
                        range,
                        path,
                        max
                ),
                () -> "loadCells " + range + " " + path + " " + max
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
    default void testFindCellsWithValueTypeWithNullValueTypeNameFails() {
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

    default void findCellsWithValueTypeAndCheck(final S store,
                                                final SpreadsheetCellRange cellRange,
                                                final String valueTypeName,
                                                final int max,
                                                final SpreadsheetCell... expected) {
        this.findCellsWithValueTypeAndCheck(
                store,
                cellRange,
                valueTypeName,
                max,
                Sets.of(expected)
        );
    }

    default void findCellsWithValueTypeAndCheck(final S store,
                                                final SpreadsheetCellRange cellRange,
                                                final String valueTypeName,
                                                final int max,
                                                final Set<SpreadsheetCell> expected) {
        this.checkEquals(
                expected,
                store.findCellsWithValueType(
                        cellRange,
                        valueTypeName,
                        max
                ),
                () -> "findCellsWithValueType " + cellRange + " " + valueTypeName + " max=" + max
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
    default void testCountCellsWithValueTypeWithNullValueTypeNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore().countCellsWithValueType(
                        SpreadsheetSelection.ALL_CELLS,
                        null
                )
        );
    }

    default void countCellsWithValueTypeAndCheck(final S store,
                                                 final SpreadsheetCellRange cellRange,
                                                 final String valueTypeName,
                                                 final int expected) {
        this.checkEquals(
                expected,
                store.countCellsWithValueType(
                        cellRange,
                        valueTypeName
                ),
                () -> "countCellsWithValueType " + cellRange + " " + valueTypeName
        );
    }
}
