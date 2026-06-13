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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.StoreTesting;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("unchecked")
public interface SpreadsheetExpressionReferencesStoreTesting<S extends SpreadsheetExpressionReferencesStore<T>,
    T extends SpreadsheetExpressionReference & Comparable<T>>
    extends StoreTesting<S, T, Set<SpreadsheetCellReference>>,
    TypeNameTesting<S> {

    // delete...........................................................................................................

    @Test
    default void testDeleteDoesntFireDeleteWatcher() {
        final Set<SpreadsheetCellReference> cells = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        cells.forEach(
            v -> store.addCell(
                id,
                v
            )
        );

        store.delete(id);

        this.checkEquals(
            Lists.of(),
            fired,
            "fired values"
        );
    }

    @Test
    default void testDeleteWithRemoveCellWatcher() {
        final S store = this.createStore();

        final T reference = this.id();
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        store.addCell(
            reference,
            cell
        );

        final List<ReferenceAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addRemoveCellWatcher(fired::add);

        store.delete(reference);

        this.checkEquals(
            Lists.of(
                ReferenceAndSpreadsheetCellReference.with(
                    reference,
                    cell
                )
            ),
            fired,
            "fired remove reference events"
        );
    }

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

    // addCell..........................................................................................................

    @Test
    default void testAddCellWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addCell(
                    null,
                    SpreadsheetSelection.A1
                )
        );
    }

    @Test
    default void testAddCellWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addCell(
                    this.id(),
                    null
                )
        );
    }

    @Test
    default void testAddCellWithWatcher() {
        final S store = this.createStore();

        final T reference = this.id();
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        final List<ReferenceAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddCellWatcher(fired::add);

        store.addCell(
            reference,
            cell
        );

        this.checkEquals(
            Lists.of(
                ReferenceAndSpreadsheetCellReference.with(
                    reference,
                    cell
                )
            ),
            fired,
            "fired add reference events"
        );
    }

    // removeCell........................................................................................................

    @Test
    default void testRemoveCellNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeCell(null)
        );
    }

    @Test
    default void testRemoveLastCellAddDeleteWatcher() {
        final Set<SpreadsheetCellReference> cells = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        cells.forEach(
            v -> store.addCell(
                id,
                v
            )
        );
        cells.forEach(v -> store.removeCell(ReferenceAndSpreadsheetCellReference.with(id, v)));

        this.checkEquals(
            Lists.of(id),
            fired,
            "fired values"
        );
    }

    @Test
    default void testRemoveCellWithWatcher() {
        final S store = this.createStore();

        final T reference = this.id();
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        final List<ReferenceAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddCellWatcher(fired::add);

        final ReferenceAndSpreadsheetCellReference<T> referenceAndCell = ReferenceAndSpreadsheetCellReference.with(
            reference,
            cell
        );
        store.addCell(
            reference,
            cell
        );
        store.removeCell(referenceAndCell);

        this.checkEquals(
            Lists.of(referenceAndCell),
            fired,
            "fired add reference events"
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
