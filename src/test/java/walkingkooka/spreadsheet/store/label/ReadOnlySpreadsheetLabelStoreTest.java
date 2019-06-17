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

package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.store.ReadOnlyStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<ReadOnlySpreadsheetLabelStore>
        implements ReadOnlyStoreTesting<ReadOnlySpreadsheetLabelStore, SpreadsheetLabelName, SpreadsheetLabelMapping> {

    @Test
    public void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ReadOnlySpreadsheetLabelStore.with(null);
        });
    }

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);

        assertSame(mapping, ReadOnlySpreadsheetLabelStore.with(store).loadOrFail(LABEL));
    }

    @Override
    public void testSaveDeleteLoad() {
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
    public void testAddDeleteWatcherAndDelete() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createStore2(), 1);
    }

    @Test
    public void testIds() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(ReadOnlySpreadsheetLabelStore.with(store), 0, 3, a.id(), b.id(), c.id());
    }

    @Override
    public void testIdsWindow() {
    }

    @Test
    public void testValues() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(ReadOnlySpreadsheetLabelStore.with(store), a.id(), 3, a, b, c);
    }

    @Override
    public void testValuesWindow() {
    }

    @Override
    public void testLabelsWithCellReference() {
        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetExpressionReference.parseCellReference("Z99");

        this.labelsAndCheck(ReadOnlySpreadsheetLabelStore.with(new FakeSpreadsheetLabelStore() {
                    @Override
                    public Set<SpreadsheetLabelName> labels(SpreadsheetCellReference c) {
                        assertEquals(reference, c);
                        return Sets.of(label);
                    }
                }),
                reference,
                label);
    }

    @Override
    public void testLabelsWithCellReference2() {
    }

    @Override
    public void testLabelsWithCellIndirectReference() {
    }

    @Test
    public void testToString() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.fake();
        this.toStringAndCheck(ReadOnlySpreadsheetLabelStore.with(store), store.toString());
    }

    @Override
    public ReadOnlySpreadsheetLabelStore createStore() {
        return ReadOnlySpreadsheetLabelStore.with(SpreadsheetLabelStores.treeMap());
    }

    private ReadOnlySpreadsheetLabelStore createStore2() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(this.labelMapping());
        return ReadOnlySpreadsheetLabelStore.with(store);
    }

    private SpreadsheetLabelMapping labelMapping() {
        return SpreadsheetLabelMapping.with(this.labelName(), this.reference());
    }

    private SpreadsheetLabelName labelName() {
        return SpreadsheetExpressionReference.labelName("elephant");
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetExpressionReference.parseCellReference("A1");
    }

    @Override
    public Class<ReadOnlySpreadsheetLabelStore> type() {
        return ReadOnlySpreadsheetLabelStore.class;
    }
}
