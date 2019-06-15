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

package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReference;
import walkingkooka.spreadsheet.store.ReadOnlyStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlySpreadsheetReferenceStoreTest extends SpreadsheetReferenceStoreTestCase<ReadOnlySpreadsheetReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference>
        implements ReadOnlyStoreTesting<ReadOnlySpreadsheetReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference, Set<SpreadsheetCellReference>> {

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> store = SpreadsheetReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        ;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        final ReadOnlySpreadsheetReferenceStore readOnly = ReadOnlySpreadsheetReferenceStore.with(store);

        this.loadAndCheck(readOnly, a1, b1); // a1 --> b1
        this.loadReferredAndCheck(readOnly, b1, a1); // b1 --> a1

        this.loadAndCheck(readOnly, b1); // b1 -> nothing
        this.loadReferredAndCheck(readOnly, a1); // a1 --> nothing
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
    public void testAddDeleteWatcherAndDelete2() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Override
    public void testRemoveLastReferenceAddDeleteWatcher() {
    }

    // ids.................................................................................

    @Test
    public final void testIds() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> store = SpreadsheetReferenceStores.treeMap();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference f = this.f99();

        store.saveReferences(a, Sets.of(f));
        store.saveReferences(b, Sets.of(f));
        store.saveReferences(c, Sets.of(f));

        this.idsAndCheck(ReadOnlySpreadsheetReferenceStore.with(store), 0, 3, a, b, c);
    }

    // values.................................................................................

    @Test
    public final void testValues() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> store = SpreadsheetReferenceStores.treeMap();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveReferences(a, f);
        store.saveReferences(b, g);
        store.saveReferences(c, hi);

        this.valuesAndCheck(ReadOnlySpreadsheetReferenceStore.with(store), a, 3, f, g, hi);
    }

    @Test
    public void testSaveReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().saveReferences(this.id(), Sets.of(this.a1()));
        });
    }

    @Override
    public void testSaveReferences() {
    }

    @Override
    public void testSaveReferencesDoesntFireDeleteWatchers() {
    }

    @Override
    public void testSaveReferencesAddReferenceWatcher() {
    }

    @Override
    public void testSaveReferencesReplaceAddReferenceWatcher() {
    }

    @Override
    public void testSaveReferencesReplaceNoneAddReferenceWatcher() {
    }

    @Override
    public void testSaveReferencesReplaceAddReferenceWatcher2() {
    }

    @Test
    public void testAddReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addReference(TargetAndSpreadsheetCellReference.with(this.id(), this.b1()));
        });
    }

    @Override
    public void testAddReferenceWithWatcher() {
    }

    @Test
    public void testRemoveReferenceFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().removeReference(TargetAndSpreadsheetCellReference.with(this.id(), this.b1()));
        });
    }

    @Override
    public void testRemoveReferenceWithWatcher() {
    }

    @Override
    public void testDeleteWithRemoveReferenceWatcher() {
    }

    // count.................................................................................

    @Test
    public void testSaveReferencesAddReferenceRemoveReferenceAndCount() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> store = SpreadsheetReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        ;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> and = TargetAndSpreadsheetCellReference.with(a1, c1);
        store.removeReference(and);
        store.addReference(and);

        this.countAndCheck(ReadOnlySpreadsheetReferenceStore.with(store), 1);
    }

    // ToStringTesting.............................................................

    @Test
    public void testToString() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> store = SpreadsheetReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        ;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        this.toStringAndCheck(ReadOnlySpreadsheetReferenceStore.with(store), store.toString());
    }

    // SpreadsheetReferenceStoreTesting.............................................................

    @Override
    public SpreadsheetCellReference id() {
        return this.a1();
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetCellReference.parse("A1");
    }

    private SpreadsheetCellReference b1() {
        return SpreadsheetCellReference.parse("b1");
    }

    private SpreadsheetCellReference c1() {
        return SpreadsheetCellReference.parse("c1");
    }

    private SpreadsheetCellReference d1() {
        return SpreadsheetCellReference.parse("d1");
    }

    private SpreadsheetCellReference e1() {
        return SpreadsheetCellReference.parse("e1");
    }

    private SpreadsheetCellReference f99() {
        return SpreadsheetCellReference.parse("f99");
    }

    private SpreadsheetCellReference g99() {
        return SpreadsheetCellReference.parse("g99");
    }

    private SpreadsheetCellReference h99() {
        return SpreadsheetCellReference.parse("h99");
    }

    private SpreadsheetCellReference i99() {
        return SpreadsheetCellReference.parse("i99");
    }

    private SpreadsheetCellReference j99() {
        return SpreadsheetCellReference.parse("j99");
    }

    @Override
    public Set<SpreadsheetCellReference> value() {
        return Sets.of(this.a1(), this.b1(), this.c1());
    }

    // StoreTesting.................................................................................

    @Override
    public ReadOnlySpreadsheetReferenceStore<SpreadsheetCellReference> createStore() {
        final SpreadsheetReferenceStore<SpreadsheetCellReference> s = SpreadsheetReferenceStores.treeMap();
        return ReadOnlySpreadsheetReferenceStore.with(s);
    }

    @Override
    public Class<ReadOnlySpreadsheetReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(ReadOnlySpreadsheetReferenceStore.class);
    }
}
