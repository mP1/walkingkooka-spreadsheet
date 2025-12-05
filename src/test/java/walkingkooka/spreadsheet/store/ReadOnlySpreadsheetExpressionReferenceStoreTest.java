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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlySpreadsheetExpressionReferenceStoreTest extends SpreadsheetExpressionReferenceStoreTestCase<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference>
    implements ReadOnlyStoreTesting<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference, Set<SpreadsheetCellReference>>,
    HashCodeEqualsDefinedTesting2<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>> {

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(a1, Sets.of(b1));

        final ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference> readOnly = ReadOnlySpreadsheetExpressionReferenceStore.with(store);

        this.loadAndCheck(readOnly, a1, b1); // a1 --> b1
        this.findReferencesWithCellAndCheck(
            readOnly,
            b1,
            0, // offset
            2, // count
            a1
        ); // b1 --> a1

        this.loadAndCheck(readOnly, b1); // b1 -> nothing
        this.findReferencesWithCellAndCheck(
            readOnly,
            a1,
            0, // offset
            1 // count
        ); // a1 --> nothing
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
    public void testDeleteDoesntFireDeleteWatcher() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testRemoveLastCellAddDeleteWatcher() {
    }

    // ids.................................................................................

    @Test
    public final void testIds() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference f = this.f99();

        store.saveCells(a, Sets.of(f));
        store.saveCells(b, Sets.of(f));
        store.saveCells(c, Sets.of(f));

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

        store.saveCells(a, f);
        store.saveCells(b, g);
        store.saveCells(c, hi);

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
    public void testSaveCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .saveCells(
                    this.id(),
                    Sets.of(
                        this.a1()
                    )
                )
        );
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCells() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCellsDoesntFireDeleteWatchers() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCellsAddCellWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCellsReplaceAddCellWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCellsReplaceNoneAddCellWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveCellsReplaceAddCellWatcher2() {
    }

    @Test
    public void testAddCellFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().addCell(ReferenceAndSpreadsheetCellReference.with(this.id(), this.b1())));
    }

    @Override
    @SuppressWarnings("unused")
    public void testAddCellWithWatcher() {
    }

    @Test
    public void testRemoveCellFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().removeCell(ReferenceAndSpreadsheetCellReference.with(this.id(), this.b1())));
    }

    @Override
    @SuppressWarnings("unused")
    public void testRemoveCellWithWatcher() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testDeleteWithRemoveCellWatcher() {
    }

    // count............................................................................................................

    @Test
    public void testCountAfterSaveCellsAddCellRemoveCell() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(a1, Sets.of(b1, c1));

        final ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference> and = ReferenceAndSpreadsheetCellReference.with(a1, c1);
        store.removeCell(and);
        store.addCell(and);

        this.countAndCheck(ReadOnlySpreadsheetExpressionReferenceStore.with(store), 1);
    }

    // ToStringTesting.............................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(a1, Sets.of(b1, c1));

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
    
    @Override
    public ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference> createStore() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> s = SpreadsheetExpressionReferenceStores.treeMap();
        return ReadOnlySpreadsheetExpressionReferenceStore.with(s);
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store1 = SpreadsheetExpressionReferenceStores.treeMap();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store2 = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store1.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        store2.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        this.checkEquals(
            ReadOnlySpreadsheetExpressionReferenceStore.with(store1),
            ReadOnlySpreadsheetExpressionReferenceStore.with(store2)
        );
    }

    @Test
    public void testEqualsDifferent() {
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> different = SpreadsheetExpressionReferenceStores.treeMap();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        different.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        this.checkNotEquals(
            ReadOnlySpreadsheetExpressionReferenceStore.with(different)
        );
    }

    @Override
    public ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference> createObject() {
        return this.createStore();
    }
    
    // class............................................................................................................
    
    @Override
    public Class<ReadOnlySpreadsheetExpressionReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(ReadOnlySpreadsheetExpressionReferenceStore.class);
    }
}
