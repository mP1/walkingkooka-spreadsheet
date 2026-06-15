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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlySpreadsheetExpressionReferencesStoreTest extends SpreadsheetExpressionReferencesStoreTestCase<ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference>, SpreadsheetCellReference>
    implements ReadOnlyStoreTesting<ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference>, SpreadsheetCellReference, SpreadsheetCellReference>,
    HashCodeEqualsDefinedTesting2<ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference>> {

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetCellReference B1 = SpreadsheetSelection.parseCell("b1");

    private final static SpreadsheetCellReference C1 = SpreadsheetSelection.parseCell("c1");

    private final static SpreadsheetCellReference F99 = SpreadsheetSelection.parseCell("f99");

    private final static SpreadsheetCellReference G99 = SpreadsheetSelection.parseCell("g99");

    private final static SpreadsheetCellReference H99 = SpreadsheetSelection.parseCell("h99");

    private final static SpreadsheetCellReference I99 = SpreadsheetSelection.parseCell("i99");

    @Test
    public void testAddCellsAndLoad() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferencesStores.treeMap();

        store.addValue(
            A1,
            B1
        );

        final ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference> readOnly = ReadOnlySpreadsheetExpressionReferencesStore.with(store);

        this.findValuesByIdAndCheck(
            readOnly,
            A1,
            B1
        ); // a1 --> b1
        this.findIdsByValueAndCheck(
            readOnly,
            B1,
            0, // offset
            2, // count
            A1
        ); // b1 --> a1

        this.findValuesByIdAndCheck(
            readOnly,
            B1
        ); // b1 -> nothing

        this.findIdsByValueAndCheck(
            readOnly,
            A1,
            0, // offset
            1 // count
        ); // a1 --> nothing
    }

    @Override
    public void testAddStoreWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherOnceAndSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddStoreWatcherOnceAndDelete() {
        throw new UnsupportedOperationException();
    }

    // ids..............................................................................................................

    @Test
    public final void testIds() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferencesStores.treeMap();

        store.addValue(
            A1,
            F99
        );
        store.addValue(
            B1,
            F99
        );
        store.addValue(
            C1,
            F99
        );

        this.idsAndCheck(
            ReadOnlySpreadsheetExpressionReferencesStore.with(store),
            0,
            3,
            A1, B1, C1
        );
    }

    // values...........................................................................................................

    @Test
    public final void testValues() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferencesStores.treeMap();

        store.addValue(
            A1,
            F99
        );
        store.addValue(
            B1,
            G99
        );
        store.addValue(
            C1,
            H99
        );
        store.addValue(
            C1,
            I99
        );

        //noinspection unchecked
        this.valuesAndCheck(
            ReadOnlySpreadsheetExpressionReferencesStore.with(store),
            0,
            3,
            F99, G99, H99
        );
    }

    @Test
    public void testAddValueFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .addValue(
                    this.id(),
                    B1
                )
        );
    }

    @Test
    public void testRemoveValueFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .removeValue(
                    this.id(),
                    B1
                )
        );
    }

    // count............................................................................................................

    @Test
    public void testCountAfterAddValueRemoveValue() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferencesStores.treeMap();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        final ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference> and = ReferenceAndSpreadsheetCellReference.with(A1, C1);
        store.removeValue(
            A1,
            C1
        );
        store.addValue(
            A1,
            C1
        );

        this.countAndCheck(
            ReadOnlySpreadsheetExpressionReferencesStore.with(store),
            2
        );
    }

    // SpreadsheetExpressionReferencesStoreTesting.......................................................................

    @Override
    public SpreadsheetCellReference id() {
        return A1;
    }

    @Override
    public SpreadsheetCellReference value() {
        return A1;
    }

    @Override
    public ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference> createStore() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> s = SpreadsheetExpressionReferencesStores.treeMap();
        return ReadOnlySpreadsheetExpressionReferencesStore.with(s);
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store1 = SpreadsheetExpressionReferencesStores.treeMap();
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store2 = SpreadsheetExpressionReferencesStores.treeMap();

        store1.addValue(
            A1,
            B1
        );
        store1.addValue(
            A1,
            C1
        );

        store2.addValue(
            A1,
            B1
        );
        store2.addValue(
            A1,
            C1
        );

        this.checkEquals(
            ReadOnlySpreadsheetExpressionReferencesStore.with(store1),
            ReadOnlySpreadsheetExpressionReferencesStore.with(store2)
        );
    }

    @Test
    public void testEqualsDifferent() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> different = SpreadsheetExpressionReferencesStores.treeMap();

        different.addValue(
            A1,
            B1
        );
        different.addValue(
            A1,
            C1
        );

        this.checkNotEquals(
            ReadOnlySpreadsheetExpressionReferencesStore.with(different)
        );
    }

    @Override
    public ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference> createObject() {
        return this.createStore();
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = SpreadsheetExpressionReferencesStores.treeMap();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        this.toStringAndCheck(
            ReadOnlySpreadsheetExpressionReferencesStore.with(store),
            store.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<ReadOnlySpreadsheetExpressionReferencesStore<SpreadsheetCellReference>> type() {
        return Cast.to(ReadOnlySpreadsheetExpressionReferencesStore.class);
    }
}
