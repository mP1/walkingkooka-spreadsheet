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
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlySpreadsheetExpressionReferenceStoreTest extends SpreadsheetExpressionReferenceStoreTestCase<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference>
        implements ReadOnlyStoreTesting<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference, Set<SpreadsheetCellReference>> {

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        final ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference> readOnly = ReadOnlySpreadsheetExpressionReferenceStore.with(store);

        this.loadAndCheck(readOnly, a1, b1); // a1 --> b1
        this.loadTargetsAndCheck(readOnly, b1, a1); // b1 --> a1

        this.loadAndCheck(readOnly, b1); // b1 -> nothing
        this.loadTargetsAndCheck(readOnly, a1); // a1 --> nothing
    }

    @Override
    public void testAddSaveWatcherAndSave() {
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
    }

    @Override
    public void testAddSaveWatcherAndRemove() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testAddDeleteWatcherAndDelete2() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testRemoveLastReferenceAddDeleteWatcher() {
    }

    // ids.................................................................................

    @Test
    public final void testIds() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference f = this.f99();

        store.saveReferences(a, Sets.of(f));
        store.saveReferences(b, Sets.of(f));
        store.saveReferences(c, Sets.of(f));

        this.idsAndCheck(ReadOnlySpreadsheetExpressionReferenceStore.with(store), 0, 3, a, b, c);
    }

    // values.................................................................................

    @Test
    public final void testValues() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveReferences(a, f);
        store.saveReferences(b, g);
        store.saveReferences(c, hi);

        //noinspection unchecked
        this.valuesAndCheck(
                ReadOnlySpreadsheetExpressionReferenceStore.with(store),
                0,
                3,
                f,
                g,
                hi
        );
    }

    @Test
    public void testSaveReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().saveReferences(this.id(), Sets.of(this.a1())));
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferences() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferencesDoesntFireDeleteWatchers() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferencesAddReferenceWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferencesReplaceAddReferenceWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferencesReplaceNoneAddReferenceWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveReferencesReplaceAddReferenceWatcher2() {
    }

    @Test
    public void testAddReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().addReference(TargetAndSpreadsheetCellReference.with(this.id(), this.b1())));
    }

    @Override
    @SuppressWarnings("unused")
    public void testAddReferenceWithWatcher() {
    }

    @Test
    public void testRemoveReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().removeReference(TargetAndSpreadsheetCellReference.with(this.id(), this.b1())));
    }

    @Override
    @SuppressWarnings("unused")
    public void testRemoveReferenceWithWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testDeleteWithRemoveReferenceWatcher() {
    }

    // count.................................................................................

    @Test
    public void testSaveReferencesAddReferenceRemoveReferenceAndCount() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> and = TargetAndSpreadsheetCellReference.with(a1, c1);
        store.removeReference(and);
        store.addReference(and);

        this.countAndCheck(ReadOnlySpreadsheetExpressionReferenceStore.with(store), 1);
    }

    // ToStringTesting.............................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        this.toStringAndCheck(ReadOnlySpreadsheetExpressionReferenceStore.with(store), store.toString());
    }

    // SpreadsheetExpressionReferenceStoreTesting.............................................................

    @Override
    public SpreadsheetCellReference id() {
        return this.a1();
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetSelection.A1;
    }

    private SpreadsheetCellReference b1() {
        return SpreadsheetSelection.parseCell("b1");
    }

    private SpreadsheetCellReference c1() {
        return SpreadsheetSelection.parseCell("c1");
    }

    private SpreadsheetCellReference f99() {
        return SpreadsheetSelection.parseCell("f99");
    }

    private SpreadsheetCellReference g99() {
        return SpreadsheetSelection.parseCell("g99");
    }

    private SpreadsheetCellReference h99() {
        return SpreadsheetSelection.parseCell("h99");
    }

    private SpreadsheetCellReference i99() {
        return SpreadsheetSelection.parseCell("i99");
    }

    @Override
    public Set<SpreadsheetCellReference> value() {
        return Sets.of(this.a1(), this.b1(), this.c1());
    }

    // StoreTesting.................................................................................

    @Override
    public ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference> createStore() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> s = SpreadsheetExpressionReferenceStores.treeMap();
        return ReadOnlySpreadsheetExpressionReferenceStore.with(s);
    }

    @Override
    public Class<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(ReadOnlySpreadsheetExpressionReferenceStore.class);
    }
}
