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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellReferencesStoreTesting<S extends SpreadsheetCellReferencesStore> extends SpreadsheetExpressionReferenceStoreTesting<S, SpreadsheetCellReference> {

    @Override
    default void testAddSaveWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testAddSaveWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testAddDeleteWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    // findCellsWithCellOrCellRange.....................................................................................

    @Test
    default void testFindCellsWithCellOrCellRangeWithNullCellOrCellRangesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findCellsWithCellOrCellRange(
                    null, // reference
                    0, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindCellsWithCellOrCellRangeWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findCellsWithCellOrCellRange(
                    this.id(), // reference
                    -1, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindCellsWithCellOrCellRangeWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findCellsWithCellOrCellRange(
                    this.id(), // reference
                    0, // offset
                    -1 // count
                )
        );
    }

    default void findCellsWithCellOrCellRangeAndCheck(final SpreadsheetCellReferencesStore store,
                                                      final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                      final int offset,
                                                      final int count,
                                                      final SpreadsheetCellReference... expected) {
        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            cellOrCellRange,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default void findCellsWithCellOrCellRangeAndCheck(final SpreadsheetCellReferencesStore store,
                                                      final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                      final int offset,
                                                      final int count,
                                                      final Set<SpreadsheetCellReference> expected) {
        this.checkEquals(
            expected,
            store.findCellsWithCellOrCellRange(
                cellOrCellRange,
                offset,
                count
            ),
            "findCellsWithCellOrCellRange " + cellOrCellRange + " offset=" + offset + ", count=" + count
        );
    }

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetCellReferencesStore.class.getSimpleName();
    }
}
