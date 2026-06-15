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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.FakeMultiValueStoreWatcher;

import java.util.TreeMap;

public class TreeMapSpreadsheetExpressionReferencesStoreTest extends SpreadsheetExpressionReferencesStoreTestCase<TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference>, SpreadsheetCellReference>
    implements HashCodeEqualsDefinedTesting2<TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference>> {

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetCellReference B1 = SpreadsheetSelection.parseCell("b1");

    private final static SpreadsheetCellReference B2 = SpreadsheetSelection.parseCell("B2");

    private final static SpreadsheetCellReference C1 = SpreadsheetSelection.parseCell("c1");

    private final static SpreadsheetCellReference C3 = SpreadsheetSelection.parseCell("C3");

    private final static SpreadsheetCellReference D1 = SpreadsheetSelection.parseCell("d1");

    private final static SpreadsheetCellReference D4 = SpreadsheetSelection.parseCell("D4");

    private final static SpreadsheetCellReference E1 = SpreadsheetSelection.parseCell("e1");

    private final static SpreadsheetCellReference E5 = SpreadsheetSelection.parseCell("E5");

    private final static SpreadsheetCellReference F99 = SpreadsheetSelection.parseCell("f99");

    private final static SpreadsheetCellReference G99 = SpreadsheetSelection.parseCell("g99");

    private final static SpreadsheetCellReference H99 = SpreadsheetSelection.parseCell("h99");

    private final static SpreadsheetCellReference I99 = SpreadsheetSelection.parseCell("i99");

    private final static SpreadsheetCellReference J99 = SpreadsheetSelection.parseCell("j99");

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

    // delete...........................................................................................................

    @Test
    public void testDeleteAfterAddCells() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.delete(A1);

        this.countAndCheck(
            store,
            0
        );
    }

    @Test
    public void testDeleteAfterSaveCells2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            C1,
            D1
        );

        store.delete(A1);

        this.findValuesByIdAndCheck(
            store,
            A1
        );

        this.findValuesByIdAndCheck(
            store,
            C1,
            D1
        );
    }

    @Test
    public void testDeleteAfterAddValueAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.delete(
            A1.toAbsolute()
        );

        this.countAndCheck(
            store,
            0
        );
    }

    @Test
    public void testDeleteAfterSaveLabels() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");

        store.addValue(
            label1,
            A1
        );

        store.delete(label1);

        this.countAndCheck(
            store,
            0
        );
    }

    // ids..............................................................................................................

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

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
            store,
            0, // from
            3, // to
            A1, B1, C1 // expected
        );
    }

    @Test
    public final void testIdsAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            F99.toAbsolute()
        );
        store.addValue(
            B1.toAbsolute(),
            F99
        );
        store.addValue(
            C1.toAbsolute(),
            F99.toAbsolute()
        );

        this.idsAndCheck(
            store,
            0, // from
            3, // to
            A1.toRelative(), B1.toRelative(), C1.toRelative() // expected
        );
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

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
            F99
        );
        this.idsAndCheck(
            store,
            1, // from
            2, // to
            B1, C1 // expected
        );
    }

    @Test
    public void testIdsWithoutCells() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        ); // a1 --> b1

        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1
        ); // b1 --> a1

        this.findValuesByIdAndCheck(
            store,
            B1
        ); // b1 -> nothing

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        ); // a1 --> nothing

        this.idsAndCheck(
            store,
            0,
            2,
            A1
        ); // b1 doesnt exist because it has 0 references to it
    }

    // values...........................................................................................................

    @Test
    public final void testValues() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

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

        this.valuesAndCheck(
            store,
            0,
            3,
            F99,
            G99,
            H99
        );
    }

    @Test
    public final void testValuesAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1.toAbsolute(),
            F99
        );
        store.addValue(
            B1.toAbsolute(),
            G99
        );
        store.addValue(
            C1.toAbsolute(),
            H99
        );
        store.addValue(
            C1,
            I99
        );

        this.valuesAndCheck(
            store,
            0,
            3,
            F99, G99, H99
        );
    }

    @Test
    public final void testValuesWindow() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

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
            D1,
            I99
        );

        this.valuesAndCheck(
            store,
            1,
            2,
            G99,
            H99
        );
    }

    @Test
    public void testValuesWithoutCells() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        ); // a1 --> b1

        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1
        ); // b1 --> a1

        this.findValuesByIdAndCheck(
            store,
            B1
        ); // b1 -> nothing

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            2 // count
        ); // a1 --> nothing

        this.valuesAndCheck(
            store,
            0,
            2,
            B1
        );
    }

    // addValue.........................................................................................................

    @Test
    public void testAddValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
    }

    @Test
    public void testAddValue2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.addValue(
            C1,
            D1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            C1,
            D1
        );
    }

    @Test
    public void testAddValueAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1.toAbsolute(),
            B1.toAbsolute()
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
    }

    @Test
    public void testAddValueSameTarget() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.addValue(
            A1,
            C1
        );

        store.addValue(
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1, C1
        );
    }

    @Test
    public void testAddValuesAddValueAndLoad() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.addValue(
            A1,
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1, C1
        );
        this.findValuesByIdAndCheck(
            store,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2, // count
            A1
        );
    }

    @Test
    public void testAddValuesAddValueAndLoad2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.addValue(
            A1,
            C1
        );

        store.addValue(
            D1,
            E1
        );
        store.addValue(
            D1,
            A1
        );
        store.addValue(
            D1,
            B1
        );
        store.addValue(
            D1,
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1, C1
        );
        this.findValuesByIdAndCheck(
            store,
            D1,
            A1, B1, C1, E1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            2, // count
            D1
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1, D1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            3, // count
            A1, D1
        );
        this.findIdsByValueAndCheck(
            store,
            E1,
            0, // offset
            2, // count
            D1
        );
    }

    @Test
    public void testAddValueWithWatcher() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        this.fired = false;

        store.addStoreWatcher(
            new FakeMultiValueStoreWatcher<>() {
                @Override
                public void onValueAdded(final SpreadsheetCellReference id,
                                         final SpreadsheetCellReference value) {
                    checkEquals(A1, id);
                    checkEquals(B1, value);

                    fired = true;
                }
            }
        );

        store.addValue(
            A1,
            B1
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // removeValue......................................................................................................

    @Test
    public void testRemoveValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.removeValue(
            A1,
            B1
        );

        this.countAndCheck(
            store,
            0
        );
    }

    @Test
    public void testRemoveValue2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            C1,
            D1
        );

        store.removeValue(
            C1,
            D1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            C1
        );
    }

    @Test
    public void testRemoveValueAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.removeValue(
            A1.toAbsolute(),
            B1
        );

        this.countAndCheck(
            store,
            0
        );
    }

    @Test
    public void testRemoveValueManyValues() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.removeValue(
            A1,
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
    }

    @Test
    public void testRemoveValueManyValuesAbsolute() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.removeValue(
            A1.toAbsolute(),
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
    }

    @Test
    public void testRemoveValueUnknownAndLoad() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        store.removeValue(
            A1,
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            2 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2 // count
        );
    }

    @Test
    public void testRemoveValueWithWatcher() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );

        this.fired = false;

        store.addStoreWatcher(
            new FakeMultiValueStoreWatcher<>() {
                @Override
                public void onValueRemoved(final SpreadsheetCellReference id,
                                           final SpreadsheetCellReference value) {
                    checkEquals(A1, id);
                    checkEquals(B1, value);

                    fired = true;
                }
            }
        );

        store.removeValue(
            A1,
            B1
        );

        this.countAndCheck(
            store,
            0
        );
    }

    private boolean fired;

    @Test
    public void testAddValuesRemoveValueAndLoad() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.removeValue(
            A1,
            C1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2 // count
        );
    }

    @Test
    public void testAddValueAndRemoveValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.addValue(
            A1,
            C1
        );
        store.addValue(
            A1,
            D1
        );
        store.addValue(
            A1,
            E1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1, C1, D1, E1
        );
        this.findValuesByIdAndCheck(
            store,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );
        this.findValuesByIdAndCheck(
            store,
            D1
        );
        this.findValuesByIdAndCheck(
            store,
            E1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            1, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            D1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            E1,
            0, // offset
            2, // count
            A1
        );
    }

    @Test
    public void testAddValueAndRemoveValue2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.addValue(
            A1,
            C1
        );
        store.addValue(
            A1,
            D1
        );
        store.addValue(
            A1,
            E1
        );
        store.addValue(
            B1,
            C1
        );
        store.addValue(
            B1,
            D1
        );
        store.addValue(
            B1,
            E1
        );

        store.removeValue(
            A1,
            E1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            B1, C1, D1
        );
        this.findValuesByIdAndCheck(
            store,
            B1,
            C1, D1, E1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );
        this.findValuesByIdAndCheck(
            store,
            D1
        );
        this.findValuesByIdAndCheck(
            store,
            E1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            1, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2, // count
            A1, B1
        );
        this.findIdsByValueAndCheck(
            store,
            D1,
            0, // offset
            3, // count
            A1, B1
        );
        this.findIdsByValueAndCheck(
            store,
            E1,
            0, // offset
            2, // count
            B1
        );
    }

    @Test
    public void testAddValueAndRemoveValue3() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );
        store.removeValue(
            A1,
            B1
        );

        this.findValuesByIdAndCheck(
            store,
            A1,
            C1
        );
        this.findValuesByIdAndCheck(
            store,
            B1
        );
        this.findValuesByIdAndCheck(
            store,
            C1
        );
        this.findValuesByIdAndCheck(
            store,
            D1
        );
        this.findValuesByIdAndCheck(
            store,
            E1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            B1,
            0, // offset
            2 // count
        );
        this.findIdsByValueAndCheck(
            store,
            C1,
            0, // offset
            2, // count
            A1
        );
        this.findIdsByValueAndCheck(
            store,
            D1,
            0, // offset
            1 // count
        );
        this.findIdsByValueAndCheck(
            store,
            E1,
            0, // offset
            2 // count
        );
    }

    // findValuesById...................................................................................................

    @Test
    public void testFindValuesById() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        this.findValuesByIdAndCheck2(
            store,
            A1,
            0, // offset
            2, // count
            B1, C1
        );

        store.removeValue(
            B1,
            A1
        );
    }

    @Test
    public void testFindValuesById2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label3");

        store.addValue(
            label1,
            a1
        );
        store.addValue(
            label2,
            b2
        );
        store.addValue(
            label3,
            c3
        );

        this.findValuesByIdAndCheck2(
            store,
            label1,
            0, // offset
            3, // count
            a1
        );
    }

    @Test
    public void testFindValuesByIdAndOffset() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addValue(
            label1,
            A1
        );
        store.addValue(
            label1,
            B2
        );
        store.addValue(
            label2,
            C3
        );

        this.findValuesByIdAndCheck2(
            store,
            label1,
            1, // offset
            2, // count
            B2
        );
    }

    @Test
    public void testFindValuesByIdAndCount() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addValue(
            label1,
            A1
        );
        store.addValue(
            label1,
            B2
        );
        store.addValue(
            label2,
            C3
        );

        this.findValuesByIdAndCheck2(
            store,
            label1,
            0, // offset
            1, // count
            A1
        );
    }

    @Test
    public void testFindValuesByIdAndOffsetAndCount() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addValue(
            label1,
            A1
        );
        store.addValue(
            label1,
            B2
        );
        store.addValue(
            label1,
            C3
        );
        store.addValue(
            label1,
            D4
        );
        store.addValue(
            label2,
            E5
        );

        this.findValuesByIdAndCheck2(
            store,
            label1,
            1, // offset
            2, // count
            B2, C3
        );
    }

    // findReferencesWithCell...........................................................................................

    @Test
    public void testFindIdsByValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            B1,
            A1
        );
        store.addValue(
            C1,
            A1
        );

        this.findIdsByValueAndCheck(
            store,
            A1,
            0, // offset
            2, // count
            B1, C1
        );

        store.removeValue(
            B1,
            A1
        );
    }

    // removeByValue....................................................................................................

    @Test
    public void testRemoveByValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferencesStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetLabelName b1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName c1 = SpreadsheetSelection.labelName("Label2");

        store.addValue(
            b1,
            a1
        );
        store.addValue(
            c1,
            a1
        );

        this.findIdsByValueAndCheck(
            store,
            a1,
            0, // offset
            2, // count
            b1, c1
        );

        store.removeByValue(a1);

        this.findIdsByValueAndCheck(
            store,
            a1
        );
    }

    // count............................................................................................................

    @Test
    public void testCountWhenSaveReferencesAddValueRemoveValue() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.removeValue(
            A1,
            C1
        );
        store.addValue(
            A1,
            C1
        );

        this.countAndCheck(
            store,
        2
        );
    }

    // ToStringTesting..................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store = this.createStore();

        store.addValue(
            A1,
            B1
        );
        store.addValue(
            A1,
            C1
        );

        store.addValue(
            SpreadsheetSelection.parseCell("e5"),
            SpreadsheetSelection.A1
        );
        store.addValue(
            SpreadsheetSelection.parseCell("e5"),
            C1
        );

        this.toStringAndCheck(
            store,
            "{A1=[B1, C1], E5=[A1, C1]}"
        );
    }

    // SpreadsheetExpressionReferencesStoreTesting.......................................................................

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetSelection.A1;
    }

    @Override
    public SpreadsheetCellReference value() {
        return F99;
    }
    
    @Override
    public TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> createStore() {
        return TreeMapSpreadsheetExpressionReferencesStore.create();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store1 = this.createStore();
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> store2 = this.createStore();

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
            store1,
            store2
        );
    }

    @Test
    public void testEqualsDifferent() {
        final TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> different = this.createStore();

        different.addValue(
            A1,
            B1
        );

        different.addValue(
            A1,
            C1
        );

        this.checkNotEquals(different);
    }

    @Override
    public TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference> createObject() {
        return this.createStore();
    }
    
    // class............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetExpressionReferencesStore<SpreadsheetCellReference>> type() {
        return Cast.to(TreeMapSpreadsheetExpressionReferencesStore.class);
    }

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
