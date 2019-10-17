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

package walkingkooka.spreadsheet.meta.store;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.ReadOnlyStoreTesting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetMetadataStoreTest extends SpreadsheetMetadataStoreTestCase<ReadOnlySpreadsheetMetadataStore>
        implements ReadOnlyStoreTesting<ReadOnlySpreadsheetMetadataStore, SpreadsheetId, SpreadsheetMetadata> {

    @Test
    public void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> ReadOnlySpreadsheetMetadataStore.with(null));
    }

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetMetadata metadata = this.metadata(this.id(), "user@example.com");
        store.save(metadata);

        assertSame(metadata, ReadOnlySpreadsheetMetadataStore.with(store).loadOrFail(this.id()));
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
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetMetadata a = this.metadata(1, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(ReadOnlySpreadsheetMetadataStore.with(store),
                0,
                3,
                a.id().get(), b.id().get(), c.id().get());
    }

    @Override
    public void testIdsWindow() {
    }

    @Test
    public void testValues() {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetMetadata a = this.metadata(1, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(ReadOnlySpreadsheetMetadataStore.with(store),
                a.id().get(),
                3,
                a, b, c);
    }

    @Override
    public void testValuesWindow() {
    }

    @Test
    public void testToString() {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.fake();
        this.toStringAndCheck(ReadOnlySpreadsheetMetadataStore.with(store), store.toString());
    }

    @Override
    public ReadOnlySpreadsheetMetadataStore createStore() {
        return ReadOnlySpreadsheetMetadataStore.with(SpreadsheetMetadataStores.treeMap());
    }

    private ReadOnlySpreadsheetMetadataStore createStore2() {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(this.metadata(1, "user1@example.com"));
        return ReadOnlySpreadsheetMetadataStore.with(store);
    }

    @Override
    public Class<ReadOnlySpreadsheetMetadataStore> type() {
        return ReadOnlySpreadsheetMetadataStore.class;
    }
}
