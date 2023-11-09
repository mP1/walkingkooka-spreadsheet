

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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellStoreTesting<S extends SpreadsheetCellStore> extends SpreadsheetStoreTesting<S, SpreadsheetCellReference, SpreadsheetCell> {

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
}
