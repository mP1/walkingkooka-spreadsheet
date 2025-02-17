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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("unchecked")
public interface SpreadsheetExpressionReferenceStoreTesting<S extends SpreadsheetExpressionReferenceStore<T>,
        T extends SpreadsheetExpressionReference & Comparable<T>>
        extends StoreTesting<S, T, Set<SpreadsheetCellReference>>,
        TypeNameTesting<S> {

    @Test
    @Override
    default void testSaveNullFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().save(Sets.of(SpreadsheetSelection.A1)));
    }

    @Test
    default void testAddSaveWatcherFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().addSaveWatcher((a) -> {
        }));
    }

    @Override
    default void testAddSaveWatcherAndRemove() {
    }

    @Test
    default void testDeleteDoesntFireDeleteWatcher() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        references.forEach(v -> store.addCell(ReferenceAndSpreadsheetCellReference.with(id, v)));

        store.delete(id);

        this.checkEquals(
                Lists.of(),
                fired,
                "fired values"
        );
    }

    // addCell..........................................................................................................

    @Test
    default void testAddCellNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().addCell(null));
    }

    @Test
    default void testAddCellWithWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final List<ReferenceAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddCellWatcher(fired::add);

        final ReferenceAndSpreadsheetCellReference<T> referenceAndCell = ReferenceAndSpreadsheetCellReference.with(target, reference);
        store.addCell(referenceAndCell);

        this.checkEquals(Lists.of(referenceAndCell), fired, "fired add reference events");
    }

    // removeCell........................................................................................................

    @Test
    default void testRemoveCellNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().removeCell(null));
    }

    @Test
    default void testRemoveLastCellAddDeleteWatcher() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        references.forEach(v -> store.addCell(ReferenceAndSpreadsheetCellReference.with(id, v)));
        references.forEach(v -> store.removeCell(ReferenceAndSpreadsheetCellReference.with(id, v)));

        this.checkEquals(Lists.of(id), fired, "fired values");
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
        store.addCell(referenceAndCell);
        store.removeCell(referenceAndCell);

        this.checkEquals(
                Lists.of(referenceAndCell),
                fired,
                "fired add reference events"
        );
    }

    @Test
    default void testDeleteWithRemoveCellWatcher() {
        final S store = this.createStore();

        final T reference = this.id();
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        store.saveCells(
                reference,
                Sets.of(cell)
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

    // saveReferences...................................................................................................

    @Test
    default void testSaveCellsNullIdFails() {
        assertThrows(
                NullPointerException.class, 
                () -> this.createStore()
                        .saveCells(
                                null,
                                Sets.of(SpreadsheetSelection.A1)
                        )
        );
    }

    @Test
    default void testSaveCellsNullReferenceFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .saveCells(
                                this.id(),
                                null
                        )
        );
    }

    @Test
    default void testSaveCells() {
        final S store = this.createStore();
        store.saveCells(this.id(), this.value());
    }

    @Test
    default void testSaveCellsDoesntFireDeleteWatchers() {
        final S store = this.createStore();
        store.addDeleteWatcher((v) -> {
            throw new UnsupportedOperationException();
        });
        store.saveCells(this.id(), this.value());
    }

    @Test
    default void testSaveCellsAddCellWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final Set<SpreadsheetCellReference> cells = this.value();

        final List<ReferenceAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddCellWatcher(fired::add);
        store.saveCells(id, cells);

        this.checkEquals(cells.stream()
                        .map(r -> ReferenceAndSpreadsheetCellReference.with(id, r))
                        .collect(Collectors.toList()),
                fired,
                "fired add reference");
    }

    @Test
    default void testSaveCellsReplaceNoneAddCellWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.saveCells(id, Sets.of(b2));

        store.addAddCellWatcher((e) -> {
            throw new UnsupportedOperationException();
        });
        store.addRemoveCellWatcher((e) -> {
            throw new UnsupportedOperationException();
        });
        store.saveCells(id, Sets.of(b2));
    }

    @Test
    default void testSaveCellsReplaceAddCellWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.saveCells(id, Sets.of(b2));

        final List<ReferenceAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddCellWatcher(addFired::add);

        final List<ReferenceAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveCellWatcher(removeFired::add);

        final SpreadsheetCellReference z9 = SpreadsheetSelection.parseCell("Z9");
        store.saveCells(id, Sets.of(z9));

        this.checkEquals(Lists.of(ReferenceAndSpreadsheetCellReference.with(id, z9)),
                addFired,
                "fired add reference");
        this.checkEquals(Lists.of(ReferenceAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    @Test
    default void testSaveCellsReplaceAddCellWatcher2() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.saveCells(id, Sets.of(b2, c3));

        final List<ReferenceAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddCellWatcher(addFired::add);

        final List<ReferenceAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveCellWatcher(removeFired::add);

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
        store.saveCells(id, Sets.of(c3, d4));

        this.checkEquals(Lists.of(ReferenceAndSpreadsheetCellReference.with(id, d4)),
                addFired,
                "fired add reference");
        this.checkEquals(Lists.of(ReferenceAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    /**
     * The key
     */
    @Override
    T id();

    default <TT extends SpreadsheetExpressionReference> void loadAndCheck(final SpreadsheetExpressionReferenceStore<TT> store,
                                                                          final TT id,
                                                                          final SpreadsheetCellReference... cells) {
        this.loadAndCheck(
                store,
                id,
                Sets.of(cells)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void loadAndCheck(final SpreadsheetExpressionReferenceStore<TT> store,
                                                                          final TT id,
                                                                          final Set<SpreadsheetCellReference> cells) {
        if (cells.isEmpty()) {
            this.loadFailCheck(store, id);
        } else {
            StoreTesting.super.loadAndCheck(store, id, cells);

        }

        for (SpreadsheetCellReference cell : cells) {
            final Set<TT> referred = store.findReferencesWithCell(cell);
            if (!referred.contains(id)) {
                fail(store + " loadTargets " + cell + " didnt return id " + id + ", actual: " + referred);
            }
        }
    }

    // findReferencesWithCell...........................................................................................

    @Test
    default void testFindReferencesWithCellWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .findReferencesWithCell(null)
        );
    }

    @SuppressWarnings("unchecked")
    default <TT extends SpreadsheetExpressionReference> void findReferencesWithCellAndCheck(final SpreadsheetExpressionReferenceStore<TT> store,
                                                                                            final SpreadsheetCellReference cell,
                                                                                            final TT... expected) {
        this.findReferencesWithCellAndCheck(
                store,
                cell,
                Sets.of(expected)
        );
    }

    default <TT extends SpreadsheetExpressionReference> void findReferencesWithCellAndCheck(final SpreadsheetExpressionReferenceStore<TT> store,
                                                                                            final SpreadsheetCellReference cell,
                                                                                            final Set<TT> expected) {
        this.checkEquals(
                expected,
                store.findReferencesWithCell(cell),
                "findReferencesWithCell " + cell
        );

        for (TT id : expected) {
            final Optional<Set<SpreadsheetCellReference>> references = store.load(id);
            if (false == references.isPresent() && references.get().contains(cell)) {
                fail(store + " load " + id + " didnt return cell " + cell + ", actual: " + references);
            }
        }
    }

    // TypeNameTesting...........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetExpressionReferenceStore.class.getSimpleName();
    }
}
