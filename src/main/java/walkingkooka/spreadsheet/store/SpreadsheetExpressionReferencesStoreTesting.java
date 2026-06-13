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
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.StoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("unchecked")
public interface SpreadsheetExpressionReferencesStoreTesting<S extends SpreadsheetExpressionReferencesStore<T>,
    T extends SpreadsheetExpressionReference & Comparable<T>>
    extends StoreTesting<S, T, Set<SpreadsheetCellReference>>,
    TypeNameTesting<S> {

    // load.............................................................................................................

    default <TT extends SpreadsheetExpressionReference> void loadAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                          final TT id,
                                                                          final SpreadsheetCellReference... expected) {
        this.loadAndCheck(
            store,
            id,
            Sets.of(expected)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void loadAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                          final TT id,
                                                                          final Set<SpreadsheetCellReference> expected) {
        if (expected.isEmpty()) {
            this.loadAndCheck(store, id);
        } else {
            StoreTesting.super.loadAndCheck(store, id, expected);

        }

        for (SpreadsheetCellReference cell : expected) {
            final Set<TT> references = store.findReferencesWithCell(
                cell,
                0,
                Integer.MAX_VALUE
            );
            if (!references.contains(id)) {
                fail(store + " load " + cell + " didnt return id " + id + ", actual: " + references);
            }
        }
    }

    // addValue.........................................................................................................

    @Test
    default void testAddValueWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    null,
                    SpreadsheetSelection.A1
                )
        );
    }

    @Test
    default void testAddValueWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    this.id(),
                    null
                )
        );
    }

    // removeValue......................................................................................................

    @Test
    default void testRemoveValueWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeValue(
                    null,
                    SpreadsheetSelection.A1
                )
        );
    }

    @Test
    default void testRemoveValueWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeValue(
                    this.id(),
                    null
                )
        );
    }

    // findCellsWithReference...........................................................................................

    @Test
    default void testFindCellsWithReferenceWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findCellsWithReference(
                    null, // reference
                    0, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindCellsWithReferenceWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findCellsWithReference(
                    this.id(), // reference
                    -1, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindCellsWithReferenceWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findCellsWithReference(
                    this.id(), // reference
                    0, // offset
                    -1 // count
                )
        );
    }

    default <TT extends SpreadsheetExpressionReference> void findCellsWithReferenceAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                                            final TT reference,
                                                                                            final int offset,
                                                                                            final int count,
                                                                                            final SpreadsheetCellReference... expected) {
        this.findCellsWithReferenceAndCheck(
            store,
            reference,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void findCellsWithReferenceAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                                            final TT reference,
                                                                                            final int offset,
                                                                                            final int count,
                                                                                            final Set<SpreadsheetCellReference> expected) {
        this.checkEquals(
            expected,
            store.findCellsWithReference(
                reference,
                offset,
                count
            ),
            "findCellsWithReference " + reference + " offset=" + offset + ", count=" + count
        );
    }

    // findReferencesWithCell...........................................................................................

    @Test
    default void testFindReferencesWithCellWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findReferencesWithCell(
                    null,
                    0, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindReferencesWithCellWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findReferencesWithCell(
                    SpreadsheetSelection.A1,
                    -1, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindReferencesWithCellWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findReferencesWithCell(
                    SpreadsheetSelection.A1,
                    0, // offset
                    -1 // count
                )
        );
    }


    @SuppressWarnings("unchecked")
    default <TT extends SpreadsheetExpressionReference> void findReferencesWithCellAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                                            final SpreadsheetCellReference cell,
                                                                                            final int offset,
                                                                                            final int count,
                                                                                            final TT... expected) {
        this.findReferencesWithCellAndCheck(
            store,
            cell,
            offset,
            count,
            Sets.of(expected)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void findReferencesWithCellAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                                            final SpreadsheetCellReference cell,
                                                                                            final int offset,
                                                                                            final int count,
                                                                                            final Set<TT> expected) {
        this.checkEquals(
            expected,
            store.findReferencesWithCell(
                cell,
                offset,
                count
            ),
            "findReferencesWithCell " + cell + " offset=" + offset + ", count=" + count
        );
    }

    // removeReferencesWithCell.........................................................................................

    @Test
    default void testRemoveReferencesWithCellWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeReferencesWithCell(null)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void removeReferencesWithCellAndCheck(final SpreadsheetExpressionReferencesStore<TT> store,
                                                                                              final SpreadsheetCellReference cell) {
        store.removeReferencesWithCell(cell);

        // just deleted references so find should not find it
        this.findReferencesWithCellAndCheck(
            store,
            cell,
            0, // offset
            1 // count
        );
    }

    // StoreTesting.....................................................................................................

    /**
     * The key
     */
    @Override
    T id();

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetExpressionReferencesStore.class.getSimpleName();
    }
}
