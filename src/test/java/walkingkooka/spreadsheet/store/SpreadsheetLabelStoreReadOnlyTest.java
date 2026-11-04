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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelStoreReadOnlyTest extends SpreadsheetLabelStoreTestCase<SpreadsheetLabelStoreReadOnly>
    implements ReadOnlyStoreTesting<SpreadsheetLabelStoreReadOnly, SpreadsheetLabelName, SpreadsheetLabelMapping>,
    HashCodeEqualsDefinedTesting2<SpreadsheetLabelStoreReadOnly> {

    @Test
    public void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetLabelStoreReadOnly.with(null));
    }

    @Test
    @Override
    public void testSaveAndLoad() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);

        assertSame(mapping, SpreadsheetLabelStoreReadOnly.with(store).loadOrFail(LABEL));
    }

    @Override
    @SuppressWarnings("unused")
    public void testSaveDeleteLoad() {
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

    @Test
    @Override
    public void testCount() {
        this.countAndCheck(this.createStore2(), 1);
    }

    @Test
    @Override
    public void testIds() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(SpreadsheetLabelStoreReadOnly.with(store), 0, 3, a.label(), b.label(), c.label());
    }

    @SuppressWarnings("unused")
    @Override
    public void testIdsWindow() {
    }

    @Test
    @Override
    public void testValues() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(
            SpreadsheetLabelStoreReadOnly.with(store),
            0,
            3,
            a,
            b,
            c
        );
    }

    @Override
    @SuppressWarnings("unused")
    public void testValuesWindow() {
    }

    @Override
    public void testFindLabelsWithReferenceWithCell() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(reference);

        this.findLabelsWithReferenceAndCheck(
            SpreadsheetLabelStoreReadOnly.with(
                new FakeSpreadsheetLabelStore() {
                    @Override
                    public Set<SpreadsheetLabelMapping> findLabelsWithReference(final SpreadsheetExpressionReference r,
                                                                                final int offset,
                                                                                final int count) {
                        checkEquals(reference, r);
                        checkEquals(0, offset, "offset");
                        checkEquals(2, count, "count");
                        return Sets.of(mapping);
                    }
                }),
            reference,
            0,
            2,
            mapping
        );
    }

    @Override
    @SuppressWarnings("unused")
    public void testFindLabelsWithReferenceWithCell2() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testFindLabelsWithReferenceWithCellIndirectReference() {
    }

    @Test
    public void testToString() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.fake();
        this.toStringAndCheck(SpreadsheetLabelStoreReadOnly.with(store), store.toString());
    }

    @Override
    public SpreadsheetLabelStoreReadOnly createStore() {
        return SpreadsheetLabelStoreReadOnly.with(SpreadsheetLabelStores.treeMap());
    }

    private SpreadsheetLabelStoreReadOnly createStore2() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(this.labelMapping());
        return SpreadsheetLabelStoreReadOnly.with(store);
    }

    private SpreadsheetLabelMapping labelMapping() {
        return SpreadsheetLabelMapping.with(this.labelName(), this.reference());
    }

    private SpreadsheetLabelName labelName() {
        return SpreadsheetSelection.labelName("elephant");
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetSelection.A1;
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final SpreadsheetLabelStore store1 = SpreadsheetLabelStores.treeMap();
        final SpreadsheetLabelStore store2 = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping mapping = this.mapping("a", 1, 2);
        store1.save(mapping);

        store2.save(mapping);

        this.checkEquals(
            SpreadsheetLabelStoreReadOnly.with(store1),
            SpreadsheetLabelStoreReadOnly.with(store2)
        );
    }

    @Test
    public void testEqualsDifferent() {
        final SpreadsheetLabelStore different = SpreadsheetLabelStores.treeMap();

        different.save(
            this.mapping("a", 1, 2)
        );

        this.checkNotEquals(
            SpreadsheetLabelStoreReadOnly.with(different)
        );
    }

    @Override
    public SpreadsheetLabelStoreReadOnly createObject() {
        return this.createStore();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelStoreReadOnly> type() {
        return SpreadsheetLabelStoreReadOnly.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
